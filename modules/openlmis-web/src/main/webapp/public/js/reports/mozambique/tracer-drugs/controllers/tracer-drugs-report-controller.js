function TracerDrugsReportController($scope, $controller, $filter, DateFormatService, TracerDrugsChartService, CubesGenerateUrlService, CubesGenerateCutParamsService) {
    $controller('BaseProductReportController', {$scope: $scope});

    $scope.getTimeRange = function (dateRange) {
        $scope.reportParams.startTime = dateRange.startTime;
        $scope.reportParams.endTime = dateRange.endTime;
    };

    $scope.$on('$viewContentLoaded', function () {
        renderDefaultTracerDrugsReport();
    });

    $scope.loadReport = function () {
        TracerDrugsChartService.makeTracerDrugsChart('tracer-report', 'legend-div', new Date($scope.reportParams.startTime), new Date($scope.reportParams.endTime), getSelectedProvince(), getSelectedDistrict());
    };

    $scope.downLoadRawData = function () {
        var params = [{
            dimension: "fields",
            values: ["facility.facility_name","drug.drug_name","date","soh"]
        }, {dimension: "format", values: ["csv"]}];

        window.open(CubesGenerateUrlService.generateFactsUrlWithParams('vw_weekly_tracer_soh', CubesGenerateCutParamsService.generateCutsParams('cutDate',
            $filter('date')($scope.reportParams.startTime, "yyyy,MM,dd"),
            $filter('date')($scope.reportParams.endTime, "yyyy,MM,dd"),
            undefined, undefined, getSelectedProvince(), getSelectedDistrict()), params));
    };

    function getSelectedProvince() {
        return $scope.getGeographicZoneById($scope.provinces, $scope.reportParams.provinceId);
    }

    function getSelectedDistrict() {
        return $scope.getGeographicZoneById($scope.districts, $scope.reportParams.districtId);
    }

    function renderDefaultTracerDrugsReport() {
        var defaultStartTime = DateFormatService.formatDateWithFirstDayOfMonth(new Date());
        var defaultEndTime = $scope.reportParams.endTime;

        TracerDrugsChartService.makeTracerDrugsChart('tracer-report', 'legend-div', new Date(defaultStartTime), new Date(defaultEndTime), undefined, undefined);
    }

}
