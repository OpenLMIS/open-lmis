function OverStockReportController($scope, $controller, $filter, DateFormatService, ReportGroupSortAndFilterService,
                                   UnitService, ReportDataServices, messageService) {
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
        facilityId: reportParams.facilityId.toString(),
        reportType: 'overStockProductsReport'
      };
      
      $scope.isDistrictOverStock = utils.isEmpty(reportParams.districtId);
      $scope.formattedOverStockList = [];
  
      ReportDataServices
        .getProductList()
        .post(utils.pickEmptyObject(overStockParams), function (overStockResponse) {
          $scope.showOverStockProductsTable = true;
          $scope.formattedOverStockList = formatOverStockList(overStockResponse.data);
          $scope.showOverStockProductsTable = overStockResponse.data.length;
          $scope.overStockList = formatOverStockListNumber(overStockResponse.data);
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
      reportType: 'overStockProductsReport'
    };
  
    ReportDataServices.getDataForExport(reportParamsObject,
      messageService.get('report.file.over.stock.products.report'));
  };
  
  var sortList = ['lotNumber', 'expiryDate', 'stockOnHandOfLot'];
  var timeList = ['expiryDate'];
  var ignoreSearchList = ['expiryDateLocalTime'];
  $scope.filterAndSort = function () {
    $scope.filterList = ReportGroupSortAndFilterService.search($scope.overStockList, $scope.filterText, "lotList", timeList, ignoreSearchList);
    $scope.filterList = ReportGroupSortAndFilterService.groupSort($scope.filterList, $scope.sortType, $scope.sortReverse, sortList);
    $scope.formattedOverStockList = formatOverStockList($scope.filterList);
  };
  
  function formatOverStockListNumber(overStockList) {
    return _.map(overStockList, function (overStockItem) {
      var item = {};
      item.provinceName = overStockItem.provinceName;
      item.districtName = overStockItem.districtName;
      item.facilityName = overStockItem.facilityName;
      item.productCode = overStockItem.productCode;
      item.productName = overStockItem.productName;
      item.lotList = overStockItem.lotList;
      item.cmm = utils.toFixedNumber(overStockItem.cmm, true);
      item.mos = utils.toFixedNumber(overStockItem.mos, true);
      return item;
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
          expiryDate: lot.expiryDate,
          expiryDateFormat: DateFormatService.formatDateWithLocale(lot.expiryDate),
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
