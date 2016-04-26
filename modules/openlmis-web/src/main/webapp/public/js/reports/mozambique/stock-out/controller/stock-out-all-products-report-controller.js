function StockOutAllProductsReportController($scope, $filter, $q, $controller, $http, CubesGenerateUrlService, messageService, StockOutReportCalculationService, CubesGenerateCutParamsService) {
    $controller('BaseProductReportController', {$scope: $scope});

    $scope.getTimeRange = function (dateRange) {
        $scope.reportParams.startTime = dateRange.startTime;
        $scope.reportParams.endTime = dateRange.endTime;
    };

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadProducts();
        $scope.loadHealthFacilities();
    });

    $scope.loadReport = function () {
        if ($scope.checkDateValidRange()) {
            generateReportTitle();
            getStockOutDataFromCubes();
        }
    };

    function getStockOutDataFromCubes() {
        var params = getStockReportRequestParam();
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
        params.selectedProvince = $scope.getGeographicZoneById($scope.provinces, $scope.reportParams.provinceId);
        params.selectedDistrict = $scope.getGeographicZoneById($scope.districts, $scope.reportParams.districtId);
        params.selectedFacility = _.find($scope.facilities, function (facility) {
            return facility.id == $scope.reportParams.facilityId;
        });
        return params;
    }

    function generateReportTitle() {
        var stockReportParams = getStockReportRequestParam();
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
}