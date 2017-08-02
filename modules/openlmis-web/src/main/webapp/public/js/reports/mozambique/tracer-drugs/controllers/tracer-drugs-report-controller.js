function TracerDrugsReportController($scope, $controller, TracerDrugsChartService) {
  $controller('BaseProductReportController', {$scope: $scope});
  $scope.reportLoaded = false;

  $scope.loadReport = function () {
    if ($scope.validateProvince() && $scope.validateDistrict()) {
      $scope.reportLoaded = TracerDrugsChartService.makeTracerDrugsChart('tracer-report', 'legend-div', new Date($scope.reportParams.startTime), new Date($scope.reportParams.endTime), getSelectedProvince(), getSelectedDistrict());
    }
  };

  $scope.exportXLSX = function () {
    TracerDrugsChartService.exportXLSX($scope.reportParams.startTime, $scope.reportParams.endTime, getSelectedProvince(), getSelectedDistrict());
  };

  function getSelectedProvince() {
    return $scope.getGeographicZoneById($scope.provinces, $scope.reportParams.provinceId);
  }

  function getSelectedDistrict() {
    return $scope.getGeographicZoneById($scope.districts, $scope.reportParams.districtId);
  }
}