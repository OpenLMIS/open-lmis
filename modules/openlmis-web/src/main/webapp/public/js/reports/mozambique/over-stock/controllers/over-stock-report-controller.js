function OverStockReportController($scope, $controller, OverStockProductsService) {
  $controller('BaseProductReportController', {$scope: $scope});
  $scope.showOverStockProductsTable = false;
  $scope.overStockItems = [];

  $scope.$on('$viewContentLoaded', function () {
    $scope.loadHealthFacilities();
  });

  $scope.loadReport = function () {
    if ($scope.validateProvince() &&
      $scope.validateDistrict() &&
      $scope.validateFacility()) {
      var reportParams = $scope.reportParams;

      var overStockParams = {
        startTime: reportParams.startTime + " 00:00:00",
        endTime: reportParams.endTime + " 23:59:59",
        provinceId: reportParams.provinceId.toString(),
        districtId: reportParams.districtId.toString(),
        facilityId: reportParams.facilityId.toString()
      };

      OverStockProductsService.get(overStockParams, {}, function (overStockResponse) {
        $scope.overStockItems = overStockResponse.rnr_list;
        $scope.showOverStockProductsTable = true;
        console.log($scope.overStockItems);
      });
    }
  };

  $scope.exportXLSX = function () {

  };
}

services.factory('OverStockProductsService', function ($resource) {
  return $resource('/reports/overStockProduct-report', {}, {});
});