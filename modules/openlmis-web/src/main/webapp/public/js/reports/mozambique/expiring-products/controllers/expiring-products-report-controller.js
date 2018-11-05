function ExpiringProductsReportController($scope, $controller, $filter, DateFormatService, ReportGroupSortAndFilterService,
                                         UnitService, ReportDataServices, messageService) {
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
  
      ReportDataServices
        .getProductList()
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
    var reportParamsObject = {
      provinceId: reportParams.provinceId.toString(),
      districtId: reportParams.districtId.toString(),
      facilityId: reportParams.facilityId.toString(),
      endTime: $filter('date')(reportParams.endTime, "yyyy-MM-dd") + " 23:59:59",
      reportType: 'expiredProductsReport'
    };
    
    ReportDataServices.getDataForExport(reportParamsObject,
      messageService.get('report.file.expired.products.report'));
  };
  
  var sortList = ['lotNumber', 'expiryDate', 'stockOnHandOfLot'];
  var timeFieldList = ['expiryDate'];
  var ignoreSearchList = ['expiryDateLocalTime'];
  $scope.filterAndSort = function () {
    $scope.filterList = ReportGroupSortAndFilterService.search($scope.expiredProductList, $scope.filterText, "lotList", timeFieldList, ignoreSearchList);
    $scope.filterList = ReportGroupSortAndFilterService.groupSort($scope.filterList, $scope.sortType, $scope.sortReverse, sortList);
    $scope.formattedExpiredProductList = formatExpiredProductList($scope.filterList);
  };
  
  function formatExpiredProductListTime(expiredProductList) {
    return _.map(expiredProductList, function (expiredProduct) {
      var item = {};
      item.provinceName = expiredProduct.provinceName;
      item.districtName = expiredProduct.districtName;
      item.facilityName = expiredProduct.facilityName;
      item.productCode = expiredProduct.productCode;
      item.productName = expiredProduct.productName;
      item.lotList = expiredProduct.lotList;
      if (!$scope.isDistrictExpiredProduct) {
        item.cmm = utils.toFixedNumber(expiredProduct.cmm, true);
        item.mos = utils.toFixedNumber(expiredProduct.mos, true);
      }
      item.price = expiredProduct.price;
      return item;
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
          price: lot.price,
          isFirst: index === 0
        };
        
        if (!$scope.isDistrictExpiredProduct) {
          _.assign(formatItem, {
            cmm: utils.toFixedNumber(expiredProduct.cmm, true),
            mos: utils.toFixedNumber(expiredProduct.mos, true),
            rowSpan: expiredProduct.lotList.length
          });
        }
  
        formattedExpiredProductList.push(formatItem);
      });
    });
    
    return formattedExpiredProductList;
  }
}
