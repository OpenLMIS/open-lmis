function NosDrugsReportController($scope, $controller, NosDrugsChartService) {
  $controller('BaseProductReportController', {$scope: $scope});
  $scope.reportLoaded = false;
  $scope.selectedDrugCode = '';

  init();

  $scope.loadReport = function () {
    if ($scope.validateProvince() && $scope.validateDistrict()) {
      // $scope.reportLoaded = true;
      // NosDrugsChartService.makeNosDrugHistogram('tracer-report', $scope.reportParams.startTime,
      //   $scope.reportParams.endTime, getSelectedProvince(), getSelectedDistrict(), $scope.selectedDrugCode)
      $scope.reportLoaded = NosDrugsChartService.makeNosDrugsChart('tracer-report', 'legend-div', new Date($scope.reportParams.startTime), new Date($scope.reportParams.endTime), getSelectedProvince(), getSelectedDistrict());
    }
  };

  $scope.exportXLSX = function () {
    NosDrugsChartService.exportXLSX($scope.reportParams.startTime, $scope.reportParams.endTime, getSelectedProvince(), getSelectedDistrict());
  };

  $scope.onChangeSelectedDrug = function () {
    if ($scope.validateProvince() && $scope.validateDistrict()) {

      NosDrugsChartService.makeNosDrugHistogram('tracer-report', getSelectedProvince(), getSelectedDistrict(),
        $scope.reportParams.startTime, $scope.reportParams.endTime, $scope.selectedDrugCode
      );
    }
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