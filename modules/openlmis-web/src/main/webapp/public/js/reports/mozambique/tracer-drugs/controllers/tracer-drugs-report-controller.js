function TracerDrugsReportController($scope, $controller, TracerDrugsChartService) {
    $controller('BaseProductReportController', {$scope: $scope});

    $scope.getTimeRange = function (dateRange) {
        $scope.reportParams.startTime = dateRange.startTime;
        $scope.reportParams.endTime = dateRange.endTime;
    };

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadProducts();
    });

    $scope.loadReport = function () {
        var selectedProvinceCode = $scope.reportParams.provinceId ? $scope.getGeographicZoneById($scope.provinces, $scope.reportParams.provinceId).code : undefined;
        var selectedDistrictCode = $scope.reportParams.districtId ? $scope.getGeographicZoneById($scope.districts, $scope.reportParams.districtId).code : undefined;

        TracerDrugsChartService.makeTracerDrugsChart('tracer-report', new Date($scope.reportParams.startTime), new Date($scope.reportParams.endTime), selectedProvinceCode, selectedDistrictCode);
    };

}
