function StockOutSingleProductReportController($scope, $filter, $q, $controller, $http, $timeout, CubesGenerateUrlService, $routeParams, ProductReportService, StockoutSingleProductFacilityChartService, StockoutSingleProductZoneChartService, StockoutSingleProductTreeDataBuilder, CubesGenerateCutParamsService, FeatureToggleService, $cacheFactory, messageService, ReportExportExcelService) {
    $controller('BaseProductReportController', {$scope: $scope});

    var stockOuts;
    var carryStartDates;

    $scope.expanding_property = {
        field: "name",
        displayName: " "
    };

    $scope.col_defs = [
        {
            field: "avgDuration",
            displayName: 'report.stock.out.avg.duration'
        },
        {
            field: "totalOccurrences",
            displayName: 'report.stock.out.occurrences'
        },
        {
            field: "totalDuration",
            displayName: 'report.stock.out.total'
        }, {
            field: "incidents",
            displayName: 'report.stock.out.incidents'
        }
    ];

    $scope.$on('$viewContentLoaded', function () {

        FeatureToggleService.get({key: 'view.stock.movement'}, function (result) {
              $scope.viewStockMovementToggle = result.key;
        });

        $scope.loadProducts();
        $scope.loadHealthFacilities();
    });

    $scope.loadProducts = function () {
        ProductReportService.loadProductsWithStockCards().get({}, function (data) {
            $scope.products = data.productList;
            if ($routeParams.code !== undefined) {
                $scope.reportParams.productCode = $routeParams.code;
                $scope.cache.put('shouldLoadStockOutReportAllProductsFromCache', "yes");
            }
        });
    };

    $scope.loadReport = loadReportAction;
    function loadReportAction() {
        if ($scope.checkDateValidRange()) {
            var params = $scope.reportParams;
            $scope.locationIdToCode(params);
            $scope.selectedProduct = getProductByCode($scope.reportParams.productCode);
            getStockOutDataFromCubes();
        }
        $scope.cache.put('singleProductData', $scope.reportParams);
        $scope.cache.put('shouldLoadStockOutReportSingleProductFromCache', "no");
    }

    function getProductByCode(code) {
        return _.find($scope.products, function (product) {
            return product.code === code;
        });
    }

    function getStockOutDataFromCubes() {
        if (!validateProduct()) {
            return;
        }

        var selectedProduct = [{'drug.drug_code': $scope.reportParams.productCode}];
        var selectedEndTime = $filter('date')($scope.reportParams.endTime, "yyyy,MM,dd");

        var requestStockOuts = $http.get(CubesGenerateUrlService.generateFactsUrl('vw_stockouts', CubesGenerateCutParamsService.generateCutsParams('overlapped_date',
            $filter('date')($scope.reportParams.startTime, "yyyy,MM,dd"), selectedEndTime, undefined, selectedProduct, undefined, undefined)));

        var requestCarryStartDates = $http.get(CubesGenerateUrlService.generateFactsUrl('vw_carry_start_dates', CubesGenerateCutParamsService.generateCutsParams('carry_start',
            undefined, selectedEndTime, undefined, selectedProduct, undefined, undefined)));

        $q.all([requestStockOuts, requestCarryStartDates]).then(function (arrayOfResults) {
            stockOuts = arrayOfResults[0].data;
            carryStartDates = arrayOfResults[1].data;

            $scope.tree_data = StockoutSingleProductTreeDataBuilder.buildTreeData(stockOuts, carryStartDates);
        });
    }

    function validateProduct() {
        $scope.invalid = !$scope.reportParams.productCode;
        return !$scope.invalid;
    }

    $scope.onExpanded = function (branch) {
        function renderFacilitiesInDistrict(facilities) {
            _.forEach(facilities, function (facility) {
                StockoutSingleProductFacilityChartService.makeStockoutChartForFacility({
                    name: facility.name,
                    code: facility.facilityCode
                }, facility.facilityCode, new Date($scope.reportParams.startTime), new Date($scope.reportParams.endTime), stockOuts);
            });
        }

        function renderDistricts(districts) {
            _.forEach(districts, function (district) {
                StockoutSingleProductZoneChartService.makeStockoutChartForZone({
                    zoneCode: district.districtCode,
                    zonePropertyName: "location.district_code"
                }, district.districtCode, new Date($scope.reportParams.startTime), new Date($scope.reportParams.endTime), stockOuts, carryStartDates);

                if (district.expanded) {
                    renderFacilitiesInDistrict(district.children);
                }
            });
        }

        var isDistrict = branch.districtCode !== undefined;
        var isProvince = branch.provinceCode !== undefined;
        if (isDistrict) {
            renderFacilitiesInDistrict(branch.children);
        } else if (isProvince) {
            renderDistricts(branch.children);
        }
    };

    $scope.onChartPlaceHolderShown = function (branch) {
        $timeout(function () {
            var isProvince = branch.provinceCode !== undefined;
            if (isProvince) {
                StockoutSingleProductZoneChartService.makeStockoutChartForZone({
                    zoneCode: branch.provinceCode,
                    zonePropertyName: "location.province_code"
                }, branch.provinceCode, new Date($scope.reportParams.startTime), new Date($scope.reportParams.endTime), stockOuts, carryStartDates);
            }
        });
    };

    if($cacheFactory.get('stockOutReportParams') === undefined) {
        $scope.cache = $cacheFactory('stockOutReportParams',{capacity: 10});
    } else {
        $scope.cache=$cacheFactory.get('stockOutReportParams');
        if ($scope.cache.get('shouldLoadStockOutReportSingleProductFromCache') === "yes") {
            $timeout(function waitSelectIsShow() {
                if ($('.select2-container .select2-choice .select2-chosen').html() !== undefined) {
                    $scope.reportParams.productCode = $scope.cache.get('singleProductData').productCode;
                    $scope.$broadcast("update-date-pickers", {
                        startTime: $scope.cache.get('singleProductData').startTime,
                        endTime: $scope.cache.get('singleProductData').endTime
                    });
                    $scope.reportParams=$scope.cache.get('singleProductData');
                    loadReportAction();
                } else {
                    $timeout(waitSelectIsShow, 1000);
                }
            }, 1000);
        }
    }


    $scope.exportXLSX = function() {
        var data = {
            reportHeaders: {
                drugCode: messageService.get('report.header.drug.code'),
                drugName: messageService.get('report.header.drug.name'),
                province: messageService.get('report.header.province'),
                district: messageService.get('report.header.district'),
                facility: messageService.get('report.header.facility'),
                avgDuration: messageService.get('report.header.avg.duration'),
                totalStockoutOccurrences: messageService.get('report.header.total.stockout.occurrences'),
                totalDaysStockedOut: messageService.get('report.header.total.days.stocked.out'),
                reportStartDate: messageService.get('report.header.report.start.date'),
                reportEndDate: messageService.get('report.header.report.end.date')
            },
            reportContent: []
        };

        if($scope.tree_data) {
            var stockoutReportData= $scope.tree_data;
            var drugCode = $scope.reportParams.productCode;
            var drugName = $scope.getDrugByCode($scope.reportParams.productCode).name;

            stockoutReportData.forEach(function (provinceLevelData) {
                var provinceLevelContent = {};
                provinceLevelContent.drugCode = drugCode;
                provinceLevelContent.drugName = drugName;
                provinceLevelContent.province = provinceLevelData.name;
                provinceLevelContent.district = '[All]';
                provinceLevelContent.facility = '[All]';
                provinceLevelContent.totalStockoutOccurrences = provinceLevelData.totalOccurrences;
                provinceLevelContent.avgDuration =  provinceLevelData.avgDuration;
                provinceLevelContent.reportStartDate = $filter('date')($scope.reportParams.startTime, 'dd/MM/yyyy');
                provinceLevelContent.reportEndDate =  $filter('date')($scope.reportParams.endTime, 'dd/MM/yyyy');
                data.reportContent.push(provinceLevelContent);

                provinceLevelData.children.forEach(function (districtLevelData) {
                    var districtLevelContent = {};
                    districtLevelContent.drugCode = drugCode;
                    districtLevelContent.drugName = drugName;
                    districtLevelContent.province = provinceLevelContent.province;
                    districtLevelContent.district = districtLevelData.name;
                    districtLevelContent.facility = '[All]';
                    districtLevelContent.totalStockoutOccurrences = districtLevelData.totalOccurrences;
                    districtLevelContent.avgDuration =  districtLevelData.avgDuration;
                    districtLevelContent.reportStartDate = $filter('date')($scope.reportParams.startTime, 'dd/MM/yyyy');
                    districtLevelContent.reportEndDate =  $filter('date')($scope.reportParams.endTime, 'dd/MM/yyyy');
                    data.reportContent.push(districtLevelContent);

                    districtLevelData.children.forEach(function (facilityLevelData) {
                        var facilityLevelContent = {};
                        facilityLevelContent.drugCode = drugCode;
                        facilityLevelContent.drugName = drugName;
                        facilityLevelContent.province = districtLevelContent.province;
                        facilityLevelContent.district = districtLevelContent.district;
                        facilityLevelContent.facility = facilityLevelData.name;
                        facilityLevelContent.totalStockoutOccurrences = facilityLevelData.totalOccurrences;
                        facilityLevelContent.avgDuration =  facilityLevelData.avgDuration;
                        facilityLevelContent.totalDaysStockedOut = facilityLevelData.totalDuration;
                        facilityLevelContent.reportStartDate = $filter('date')($scope.reportParams.startTime, 'dd/MM/yyyy');
                        facilityLevelContent.reportEndDate =  $filter('date')($scope.reportParams.endTime, 'dd/MM/yyyy');
                        data.reportContent.push(facilityLevelContent);
                    });
                });
            });

            ReportExportExcelService.exportAsXlsx(data, messageService.get('report.file.single.product.stockout.report'));
        }
    };
}