function TracerDrugsReportController($scope, $controller, TracerDrugsChartService, NosDrugsChartService) {
  $controller('BaseProductReportController', {$scope: $scope});
  $scope.reportLoaded = false;
  $scope.selectedDrugCode = '';
  $scope.buttonDisplay = false;

  init();

  $scope.loadReport = function () {
    if ($scope.validateProvince() && $scope.validateDistrict()) {
      getReportLoaded();
    }
  };

  $scope.exportXLSX = function () {
    NosDrugsChartService.exportXLSX($scope.reportParams.startTime, $scope.reportParams.endTime, getSelectedProvince(), getSelectedDistrict(), "tracerDrug");
  };

  $scope.onChangeSelectedDrug = function () {
    $scope.loadReport();
  };

  function getReportLoaded() {
    $scope.reportLoaded = true;
    NosDrugsChartService.getNosDrugItemsPromise(getSelectedProvince(), getSelectedDistrict(),
      $scope.reportParams.startTime, $scope.reportParams.endTime, $scope.selectedDrugCode, "tracerDrug")
      .$promise.then(function (result) {
      $scope.buttonDisplay = result.data.length > 0;
      NosDrugsChartService.makeNosDrugHistogram('tracer-report', result.data);
    });
  }

  function init() {
    var tracerDrugListPromis = TracerDrugsChartService.getTracerDrugList();

    tracerDrugListPromis.then(function (tracerDrugListResult) {
      $scope.tracerDrugList = tracerDrugListResult.data;
      $scope.selectedDrugCode = tracerDrugListResult.data[0]['drug.drug_code'];
    });
  }

  function getSelectedProvince() {
    return $scope.getGeographicZoneById($scope.provinces, $scope.reportParams.provinceId);
  }

  function getSelectedDistrict() {
    return $scope.getGeographicZoneById($scope.districts, $scope.reportParams.districtId);
  }
}