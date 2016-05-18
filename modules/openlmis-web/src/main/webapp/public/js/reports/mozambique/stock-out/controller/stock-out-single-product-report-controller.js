function StockOutSingleProductReportController($scope, $filter, $q, $controller, $http, $timeout, CubesGenerateUrlService, $routeParams, ProductReportService, StockoutSingleProductFacilityChartService, StockoutSingleProductZoneChartService, StockoutSingleProductTreeDataBuilder, CubesGenerateCutParamsService, FeatureToggleService) {
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

    $scope.getTimeRange = function (dateRange) {
        $scope.reportParams.startTime = dateRange.startTime;
        $scope.reportParams.endTime = dateRange.endTime;
    };

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
            $scope.reportParams.productCode = $routeParams.code;
        });
    };

    $scope.loadReport = function () {
        if ($scope.checkDateValidRange()) {
            $scope.selectedProduct = getProductByCode($scope.reportParams.productCode);
            getStockOutDataFromCubes();
        }
    };

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
}