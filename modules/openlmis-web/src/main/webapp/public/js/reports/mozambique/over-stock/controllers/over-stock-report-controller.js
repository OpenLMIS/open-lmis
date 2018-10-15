function OverStockReportController($scope, $controller, OverStockProductsService, DateFormatService) {
  $controller('BaseProductReportController', {$scope: $scope});
  $scope.formattedOverStockList = [];

  $scope.$on('$viewContentLoaded', function () {
    $scope.loadHealthFacilities();
  });

  $scope.loadReport = function () {
    if ($scope.validateProvince() &&
      $scope.validateDistrict() &&
      $scope.validateFacility()) {
      var reportParams = $scope.reportParams;

      if (!reportParams.provinceId) {
        return;
      }

      var overStockParams = {
        startTime: reportParams.startTime + " 00:00:00",
        endTime: reportParams.endTime + " 23:59:59",
        provinceId: reportParams.provinceId.toString(),
        districtId: reportParams.districtId.toString(),
        facilityId: reportParams.facilityId.toString()
      };

      OverStockProductsService.get(overStockParams, {}, function (overStockResponse) {
        $scope.formattedOverStockList = formatOverStockList(overStockResponse.rnr_list);
      });
    }
  };

  $scope.exportXLSX = function () {

  };

  function formatOverStockList(overStockList) {
    var formattedOverStockList = [];
    _.forEach(overStockList, function (overStock) {
      _.forEach(overStock.lotList, function (lot, index) {
        formattedOverStockList.push({
          provinceName: overStock.provinceName,
          districtName: overStock.districtName,
          facilityName: overStock.facilityName,
          productCode: overStock.productCode,
          productName: overStock.productName,
          lotNumber: lot.lotNumber,
          expiryDate: DateFormatService.formatDateWithLocale(lot.expiryDate),
          stockOnHandOfLot: lot.stockOnHandOfLot,
          cmm: overStock.cmm || 0,
          mos: overStock.mos || 0,
          rowSpan: overStock.lotList.length,
          isFirst: index === 0
        });
      });
    });

    return formattedOverStockList;
  }
}

services.factory('OverStockProductsService', function ($resource) {
  return $resource('/reports/overstock-report', {}, {});
});