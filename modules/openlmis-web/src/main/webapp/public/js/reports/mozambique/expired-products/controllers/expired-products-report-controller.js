function ExpiredProductsReportController($scope, $controller, $filter, ExpiredProductsService, DateFormatService,
                                         ReportGroupSortAndFilterService, UnitService) {
  $controller('BaseProductReportController', {$scope: $scope});
  $scope.formattedExpiredProductList = [];
  $scope.showExpiredProductsTable = false;
  $scope.expiredProductList = null;
  $scope.filterList = [];
  $scope.filterText = "";
  $scope.isDistrictExpiredProduct = false;
  
  $scope.$on('$viewContentLoaded', function () {
    $scope.loadHealthFacilities();
    UnitService.onLoadScrollEvent('fixed-body', 'fixed-header');
  });
  
  $scope.loadReport = function () {
    if ($scope.validateProvince() &&
      $scope.validateDistrict() &&
      $scope.validateFacility()) {
      var reportParams = $scope.reportParams;
      
      var expiredProductParams = {
        endTime: $filter('date')(reportParams.endTime, "yyyy-MM-dd") + " 23:59:59",
        provinceId: reportParams.provinceId.toString(),
        districtId: reportParams.districtId.toString(),
        facilityId: reportParams.facilityId.toString(),
        reportType: 'expiredProductsReport'
      };
      
      $scope.isDistrictExpiredProduct = utils.isEmpty(reportParams.districtId);
      $scope.formattedExpiredProductList = [];
      
      ExpiredProductsService
        .getExpiredProductList()
        .post(utils.pickEmptyObject(expiredProductParams), function (expiredProductResponse) {
          $scope.showExpiredProductsTable = true;
          $scope.formattedExpiredProductList = formatExpiredProductList(expiredProductResponse.data);
          $scope.showExpiredProductsTable = expiredProductResponse.data.length;
          $scope.expiredProductList = formatExpiredProductListTime(expiredProductResponse.data);
          $scope.filterAndSort();
  
          UnitService.fixedScrollBorHeaderStyles('fixed-header', 'fixed-body');
        });
    }
  };
  
  $scope.exportXLSX = function () {
    var reportParams = $scope.reportParams;
    
    ExpiredProductsService.getDataForExport(
      reportParams.provinceId.toString(),
      reportParams.districtId.toString(),
      reportParams.facilityId.toString(),
      $filter('date')(reportParams.endTime, "yyyy-MM-dd") + " 23:59:59"
    );
  };
  
  var sortList = ['lotNumber', 'expiryDate', 'stockOnHandOfLot'];
  $scope.filterAndSort = function () {
    $scope.filterList = ReportGroupSortAndFilterService.search($scope.expiredProductList, $scope.filterText, "lotList", "expiryDate");
    $scope.filterList = ReportGroupSortAndFilterService.groupSort($scope.filterList, $scope.sortType, $scope.sortReverse, sortList);
    $scope.formattedExpiredProductList = formatExpiredProductList($scope.filterList);
  };
  
  function formatExpiredProductListTime(expiredProductList) {
    return _.map(expiredProductList, function (expiredProduct) {
      expiredProduct.cmm = utils.toFixedNumber(expiredProduct.cmm, true);
      expiredProduct.mos = utils.toFixedNumber(expiredProduct.mos, true);
      return expiredProduct;
    });
  }
  
  function formatExpiredProductList(expiredProductList) {
    var formattedExpiredProductList = [];
    _.forEach(expiredProductList, function (expiredProduct) {
      _.forEach(expiredProduct.lotList, function (lot, index) {
        var formatItem = {
          provinceName: expiredProduct.provinceName,
          districtName: expiredProduct.districtName,
          facilityName: expiredProduct.facilityName,
          productCode: expiredProduct.productCode,
          productName: expiredProduct.productName,
          lotNumber: lot.lotNumber,
          expiryDate: DateFormatService.formatDateWithLocale(lot.expiryDate),
          stockOnHandOfLot: lot.stockOnHandOfLot,
          price: lot.price
        };
        
        if (!$scope.isDistrictExpiredProduct) {
          _.assign(formatItem, {
            cmm: utils.toFixedNumber(expiredProduct.cmm, true),
            mos: utils.toFixedNumber(expiredProduct.mos, true),
            rowSpan: expiredProduct.lotList.length,
            isFirst: index === 0
          });
        }
  
        formattedExpiredProductList.push(formatItem);
      });
    });
    
    return formattedExpiredProductList;
  }
}

services.factory('ExpiredProductsService', function ($resource, $filter, ReportExportExcelService, messageService) {
  function getExpiredProductList() {
    return $resource('/reports/data', {}, {post: {method: 'POST'}});
  }
  
  function getDataForExport(provinceId, districtId, facilityId, endTime) {
    var data = {
      provinceId: provinceId,
      districtId: districtId,
      facilityId: facilityId,
      endTime: endTime,
      reportType: 'expiredProductsReport'
    };
    
    ReportExportExcelService.exportAsXlsxBackend(
      utils.pickEmptyObject(data), messageService.get('report.file.over.stock.products.report'));
  }
  
  return {
    getExpiredProductList: getExpiredProductList,
    getDataForExport: getDataForExport
  };
});