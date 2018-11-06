function ExpiringProductsReportController($scope, $controller, $filter, DateFormatService, ReportGroupSortAndFilterService,
                                          UnitService, ReportDataServices, messageService) {
  $controller('BaseProductReportController', {$scope: $scope});
  $scope.formattedExpiringProductList = [];
  $scope.showExpiringProductsTable = false;
  $scope.expiringProductList = null;
  $scope.filterList = [];
  $scope.filterText = "";
  $scope.isDistrictExpiringProduct = false;
  
  $scope.$on('$viewContentLoaded', function () {
    $scope.loadHealthFacilities();
    UnitService.onLoadScrollEvent('fixed-body', 'fixed-header');
  });
  
  $scope.loadReport = function () {
    if ($scope.validateProvince() &&
      $scope.validateDistrict() &&
      $scope.validateFacility()) {
      var reportParams = $scope.reportParams;
      
      var expiringProductParams = {
        endTime: $filter('date')(reportParams.endTime, "yyyy-MM-dd") + " 23:59:59",
        provinceId: reportParams.provinceId.toString(),
        districtId: reportParams.districtId.toString(),
        facilityId: reportParams.facilityId.toString(),
        reportType: 'expiringProductsReport'
      };
      
      $scope.isDistrictExpiringProduct = utils.isEmpty(reportParams.districtId);
      $scope.formattedExpiringProductList = [];
      
      ReportDataServices
        .getProductList()
        .post(utils.pickEmptyObject(expiringProductParams), function (expiringProductResponse) {
          $scope.showExpiringProductsTable = true;
          $scope.formattedExpiringProductList = formatExpiringProductList(expiringProductResponse.data);
          $scope.showExpiringProductsTable = expiringProductResponse.data.length;
          $scope.expiringProductList = formatExpiringProductListTime(expiringProductResponse.data);
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
      reportType: 'expiringProductsReport'
    };
    
    ReportDataServices.getDataForExport(reportParamsObject,
      messageService.get('report.file.expiring.products.report'));
  };
  
  var sortList = ['lotNumber', 'expiryDate', 'stockOnHandOfLot'];
  var timeFieldList = ['expiryDate'];
  var ignoreSearchList = ['expiryDateLocalTime'];
  $scope.filterAndSort = function () {
    $scope.filterList = ReportGroupSortAndFilterService.search($scope.expiringProductList, $scope.filterText, "lotList", timeFieldList, ignoreSearchList);
    $scope.filterList = ReportGroupSortAndFilterService.groupSort($scope.filterList, $scope.sortType, $scope.sortReverse, sortList);
    $scope.formattedExpiringProductList = formatExpiringProductList($scope.filterList);
  };
  
  function formatExpiringProductListTime(expiringProductList) {
    return _.map(expiringProductList, function (expiringProduct) {
      var item = {};
      item.provinceName = expiringProduct.provinceName;
      item.districtName = expiringProduct.districtName;
      item.facilityName = expiringProduct.facilityName;
      item.productCode = expiringProduct.productCode;
      item.productName = expiringProduct.productName;
      item.lotList = expiringProduct.lotList;
      if (!$scope.isDistrictExpiringProduct) {
        item.cmm = utils.toFixedNumber(expiringProduct.cmm, true);
        item.mos = utils.toFixedNumber(expiringProduct.mos, true);
      }
      item.price = expiringProduct.price;
      return item;
    });
  }
  
  function formatExpiringProductList(expiringProductList) {
    var formattedExpiringProductList = [];
    _.forEach(expiringProductList, function (expiringProduct) {
      _.forEach(expiringProduct.lotList, function (lot, index) {
        var formatItem = {
          provinceName: expiringProduct.provinceName,
          districtName: expiringProduct.districtName,
          facilityName: expiringProduct.facilityName,
          productCode: expiringProduct.productCode,
          productName: expiringProduct.productName,
          lotNumber: lot.lotNumber,
          expiryDate: DateFormatService.formatDateWithLocale(lot.expiryDate),
          stockOnHandOfLot: lot.stockOnHandOfLot,
          price: lot.price,
          isFirst: index === 0
        };
        
        if (!$scope.isDistrictExpiringProduct) {
          _.assign(formatItem, {
            cmm: utils.toFixedNumber(expiringProduct.cmm, true),
            mos: utils.toFixedNumber(expiringProduct.mos, true),
            rowSpan: expiringProduct.lotList.length
          });
        }
        
        formattedExpiringProductList.push(formatItem);
      });
    });
    
    return formattedExpiringProductList;
  }
}
