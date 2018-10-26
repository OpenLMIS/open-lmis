function OverStockReportController($scope, $controller, $filter, OverStockProductsService, DateFormatService,
                                   ReportGroupSortAndFilterService, UnitService) {
  $controller('BaseProductReportController', {$scope: $scope});
  $scope.formattedOverStockList = [];
  $scope.showOverStockProductsTable = false;
  $scope.overStockList = null;
  $scope.filterList = [];
  $scope.filterText = "";
  $scope.isDistrictOverStock = false;
  
  $scope.$on('$viewContentLoaded', function () {
    $scope.loadHealthFacilities();
    UnitService.onLoadScrollEvent('fixed-body', 'fixed-header');
  });
  
  $scope.loadReport = function () {
    if ($scope.validateProvince() &&
      $scope.validateDistrict() &&
      $scope.validateFacility()) {
      var reportParams = $scope.reportParams;
      
      var overStockParams = {
        endTime: $filter('date')(reportParams.endTime, "yyyy-MM-dd") + " 23:59:59",
        provinceId: reportParams.provinceId.toString(),
        districtId: reportParams.districtId.toString(),
        facilityId: reportParams.facilityId.toString()
      };
      
      $scope.isDistrictOverStock = utils.isEmpty(reportParams.districtId);
      $scope.formattedOverStockList = [];
      
      OverStockProductsService
        .getOverStockProductList()
        .get(utils.pickEmptyObject(overStockParams), {}, function (overStockResponse) {
          $scope.showOverStockProductsTable = true;
          $scope.formattedOverStockList = formatOverStockList(overStockResponse.rnr_list);
          $scope.showOverStockProductsTable = overStockResponse.rnr_list.length;
          $scope.overStockList = formatOverStockProductListTime(overStockResponse.rnr_list);
          $scope.filterAndSort();
        });
    }
  };
  
  $scope.exportXLSX = function () {
    var reportParams = $scope.reportParams;
    
    OverStockProductsService.getDataForExport(
      reportParams.provinceId.toString(),
      reportParams.districtId.toString(),
      reportParams.facilityId.toString(),
      $filter('date')(reportParams.endTime, "yyyy-MM-dd") + " 23:59:59"
    );
  };
  
  var sortList = ['lotNumber', 'expiryDate', 'stockOnHandOfLot'];
  $scope.filterAndSort = function () {
    $scope.filterList = ReportGroupSortAndFilterService.search($scope.overStockList, $scope.filterText, "lotList", "expiryDate");
    $scope.filterList = ReportGroupSortAndFilterService.groupSort($scope.filterList, $scope.sortType, $scope.sortReverse, sortList);
    $scope.formattedOverStockList = formatOverStockList($scope.filterList);
  };

  function formatOverStockProductListTime(overStockList) {
    return _.map(overStockList, function (overStockItem) {
      overStockItem.cmm = utils.toFixedNumber(overStockItem.cmm, true);
      overStockItem.mos = utils.toFixedNumber(overStockItem.mos, true);
      return overStockItem;
    });
  }
  
  function formatOverStockList(overStockList) {
    var formattedOverStockList = [];
    _.forEach(overStockList, function (overStock) {
      _.forEach(overStock.lotList, function (lot, index) {
        var formatItem = {
          provinceName: overStock.provinceName,
          districtName: overStock.districtName,
          facilityName: overStock.facilityName,
          productCode: overStock.productCode,
          productName: overStock.productName,
          lotNumber: lot.lotNumber,
          expiryDate: DateFormatService.formatDateWithLocale(lot.expiryDate),
          stockOnHandOfLot: lot.stockOnHandOfLot
        };
        
        if (!$scope.isDistrictOverStock) {
          _.assign(formatItem, {
            cmm: utils.toFixedNumber(overStock.cmm, true),
            mos: utils.toFixedNumber(overStock.mos, true),
            rowSpan: overStock.lotList.length,
            isFirst: index === 0
          });
        }
        
        formattedOverStockList.push(formatItem);
      });
    });
    
    return formattedOverStockList;
  }
}

services.factory('OverStockProductsService', function ($resource, $filter, ReportExportExcelService, messageService) {
  function getOverStockProductList() {
    return $resource('/reports/overstock-report', {}, {});
  }
  
  function getDataForExport(provinceId, districtId, facilityId, endTime) {
    var data = {
      provinceId: provinceId,
      districtId: districtId,
      facilityId: facilityId,
      endTime: endTime,
      reportType: 'overStockProductReport'
    };
    
    ReportExportExcelService.exportAsXlsxBackend(
      utils.pickEmptyObject(data), messageService.get('report.file.over.stock.products.report'));
  }
  
  return {
    getOverStockProductList: getOverStockProductList,
    getDataForExport: getDataForExport
  };
});