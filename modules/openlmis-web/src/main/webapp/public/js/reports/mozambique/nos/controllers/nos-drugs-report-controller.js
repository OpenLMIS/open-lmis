function NosDrugsReportController($scope, $controller, NosDrugsChartService) {
  $controller('BaseProductReportController', {$scope: $scope});
  $scope.reportLoaded = false;
  $scope.selectedDrugCode = '';
  $scope.buttonDisplay = false;

  init();

  function getReportLoaded() {
    isButtonDisplay();
    return NosDrugsChartService.makeNosDrugHistogram('tracer-report', getSelectedProvince(), getSelectedDistrict(),
      $scope.reportParams.startTime, $scope.reportParams.endTime, $scope.selectedDrugCode);
  }

  function isButtonDisplay() {
    NosDrugsChartService.getNosDrugItemsPromise(getSelectedProvince(), getSelectedDistrict(),
      $scope.reportParams.startTime, $scope.reportParams.endTime, $scope.selectedDrugCode).$promise.then(function (result) {
      $scope.buttonDisplay = result.data.length > 0;
    });
  }

  $scope.loadReport = function () {
    if ($scope.validateProvince() && $scope.validateDistrict()) {
      $scope.reportLoaded = getReportLoaded();
    }
  };

  $scope.exportXLSX = function () {
    NosDrugsChartService.exportXLSX($scope.reportParams.startTime, $scope.reportParams.endTime, getSelectedProvince(), getSelectedDistrict());
  };

  $scope.onChangeSelectedDrug = function () {
    $scope.loadReport();
  };

  function init() {
    var nosDrugListPromis = NosDrugsChartService.getNosDrugList();

    nosDrugListPromis.then(function (nosDrugListResult) {
      $scope.nosDrugList = nosDrugListResult.data;
      $scope.selectedDrugCode = nosDrugListResult.data[0]['drug.drug_code'];
    });
  }

  function getSelectedProvince() {
    return $scope.getGeographicZoneById($scope.provinces, $scope.reportParams.provinceId);
  }

  function getSelectedDistrict() {
    return $scope.getGeographicZoneById($scope.districts, $scope.reportParams.districtId);
  }
}