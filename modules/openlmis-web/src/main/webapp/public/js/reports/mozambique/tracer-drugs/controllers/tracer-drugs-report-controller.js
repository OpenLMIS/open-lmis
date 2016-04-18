function TracerDrugsReportController($scope, $controller, DateFormatService, TracerDrugsChartService) {
    $controller('BaseProductReportController', {$scope: $scope});

    $scope.getTimeRange = function (dateRange) {
        $scope.reportParams.startTime = dateRange.startTime;
        $scope.reportParams.endTime = dateRange.endTime;
    };

    $scope.$on('$viewContentLoaded', function () {
        renderDefaultTracerDrugsReport();
    });

    $scope.loadReport = function () {
        var selectedProvinceCode = $scope.reportParams.provinceId ? $scope.getGeographicZoneById($scope.provinces, $scope.reportParams.provinceId).code : undefined;
        var selectedDistrictCode = $scope.reportParams.districtId ? $scope.getGeographicZoneById($scope.districts, $scope.reportParams.districtId).code : undefined;

        TracerDrugsChartService.makeTracerDrugsChart('tracer-report', new Date($scope.reportParams.startTime), new Date($scope.reportParams.endTime), selectedProvinceCode, selectedDistrictCode);
    };

    function renderDefaultTracerDrugsReport() {
        var defaultStartTime = DateFormatService.formatDateWithFirstDayOfMonth(new Date());
        var defaultEndTime = $scope.reportParams.endTime;

        TracerDrugsChartService.makeTracerDrugsChart('tracer-report', new Date(defaultStartTime), new Date(defaultEndTime), undefined, undefined);
    }

}
