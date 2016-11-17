function TracerDrugsReportController($scope, $controller, $filter, DateFormatService, TracerDrugsChartService, CubesGenerateUrlService, CubesGenerateCutParamsService) {
    $controller('BaseProductReportController', {$scope: $scope});

    $scope.$on('$viewContentLoaded', function () {
        renderDefaultTracerDrugsReport();
    });

    $scope.loadReport = function () {
        TracerDrugsChartService.makeTracerDrugsChart('tracer-report', 'legend-div', new Date($scope.reportParams.startTime), new Date($scope.reportParams.endTime), getSelectedProvince(), getSelectedDistrict());
    };

    $scope.exportXLSX = function() {
        TracerDrugsChartService.exportXLSX($scope.reportParams.startTime, $scope.reportParams.endTime,  getSelectedProvince(), getSelectedDistrict());
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
