function StockOutAllProductsReportController($scope, $filter, $q, $controller, $http, CubesGenerateUrlService, messageService, StockOutReportCalculationService, CubesGenerateCutParamsService, $cacheFactory, $timeout) {
    $controller('BaseProductReportController', {$scope: $scope});

    if ($cacheFactory.get('keepHistoryInStockOutReportPage') === undefined) {
        $scope.cache = $cacheFactory('keepHistoryInStockOutReportPage', {capacity: 10});
    }
    else {
        $scope.cache = $cacheFactory.get('keepHistoryInStockOutReportPage');
        if ($scope.cache.get('saveDataOfStockOutReport') === "yes") {
            $timeout(function waitSelectIsShow() {
                if ($('.facility-choose .select2-choice .select2-chosen').html() !== undefined) {
                    $scope.$broadcast("update-date-pickers", {
                        startTime: $scope.cache.get('startTime'),
                        endTime: $scope.cache.get('endTime')
                    });
                    if ($scope.cache.get('dataOfStockOutReport').selectedFacility !== undefined) {
                        $scope.reportParams.facilityId = $scope.cache.get('dataOfStockOutReport').selectedFacility.id;
                    }
                    if ($scope.cache.get('dataOfStockOutReport').selectedProvince !== undefined) {
                        $scope.reportParams.provinceId = $scope.cache.get('dataOfStockOutReport').selectedProvince.id;
                    }
                    if ($scope.cache.get('dataOfStockOutReport').selectedDistrict !== undefined) {
                        $scope.reportParams.districtId = $scope.cache.get('dataOfStockOutReport').selectedDistrict.id;
                    }
                    loadReportAction();
                } else {
                    $timeout(waitSelectIsShow, 1000);
                }
            }, 1000);
        }
    }

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadProducts();
        $scope.loadHealthFacilities();
    });

    $scope.loadReport = loadReportAction;
    function loadReportAction() {
        if ($scope.checkDateValidRange()) {
            generateReportTitle();
            getStockOutDataFromCubes();
        }
    }
    
    $scope.saveHistory=function () {
        $scope.cache.put('dataOfStockOutReport', putHistoryDataToParams());
        $scope.cache.put('startTime', $scope.reportParams.startTime);
        $scope.cache.put('endTime', $scope.reportParams.endTime);
    };
    
    function getStockOutDataFromCubes() {
        var params = putHistoryDataToParams();
        $scope.cache.put('saveDataOfStockOutReport', "no");
        var cutsParams = CubesGenerateCutParamsService.generateCutsParams("overlapped_date", params.startTime, params.endTime,
            params.selectedFacility, undefined, params.selectedProvince, params.selectedDistrict);

        $http.get(CubesGenerateUrlService.generateFactsUrl('vw_stockouts', cutsParams)).success(function (data) {
            $scope.reportData = [];

            generateStockOutAverageReportData(data);
            formatReportWhenSelectAllFacility();
        });
    }

    function generateStockOutAverageReportData(data) {
        var drugCodeKey = "drug.drug_code";
        var facilityCodeKey = "facility.facility_code";

        _.chain(data)
            .groupBy(drugCodeKey)
            .forEach(function (drug) {
                var occurrences = _.chain(drug)
                    .groupBy(facilityCodeKey)
                    .reduce(function (memo, stockOutsInFacility) {
                        memo += StockOutReportCalculationService.generateIncidents(stockOutsInFacility).length;
                        return memo;
                    }, 0).value();

                var calculationData = StockOutReportCalculationService.calculateStockoutResult(drug, occurrences);
                generateReportItem(drug, calculationData);
            });
    }

    function generateReportItem(drug, calculationData) {
        drug.code = drug[0]['drug.drug_code'];
        drug.name = drug[0]['drug.drug_name'];
        drug.totalDuration = calculationData.totalDuration;
        drug.avgDuration = parseFloat(calculationData.avgDuration);
        drug.occurrences = calculationData.totalOccurrences;
        $scope.reportData.push(drug);
    }

    function formatReportWhenSelectAllFacility() {
        if (!$scope.reportParams.facilityId) {
            $scope.reportData.map(function (data) {
                data.totalDuration = "-";
            });
        }
    }

    function getStockReportRequestParam() {
        var params = {};
        params.startTime = $filter('date')($scope.reportParams.startTime, "yyyy,MM,dd");
        params.endTime = $filter('date')($scope.reportParams.endTime, "yyyy,MM,dd");
        $scope.locationIdToCode(params);
        return params;
    }

    function generateReportTitle() {
        var stockReportParams = putHistoryDataToParams();
        var reportTitle = "";
        if (stockReportParams.selectedProvince) {
            reportTitle = stockReportParams.selectedProvince.name;
        }
        if (stockReportParams.selectedDistrict) {
            reportTitle += ("," + stockReportParams.selectedDistrict.name);
        }
        if (stockReportParams.selectedFacility) {
            reportTitle += reportTitle === "" ? stockReportParams.selectedFacility.name : ("," + stockReportParams.selectedFacility.name);
        }
        $scope.reportParams.reportTitle = reportTitle || messageService.get("label.all");
    }

    function putHistoryDataToParams() {
        var stockReportParams = getStockReportRequestParam();
        if ($cacheFactory.get('keepHistoryInStockOutReportPage') !== undefined) {
            $scope.cache = $cacheFactory.get('keepHistoryInStockOutReportPage');
            var stockReportParamsBuff = $scope.cache.get('dataOfStockOutReport');
            if ($scope.cache.get('saveDataOfStockOutReport') === "yes") {
                stockReportParams = stockReportParamsBuff;
            } else if (stockReportParamsBuff !== undefined) {
                stockReportParams = getStockReportRequestParam();
            }
        }
        return stockReportParams;
    }
}