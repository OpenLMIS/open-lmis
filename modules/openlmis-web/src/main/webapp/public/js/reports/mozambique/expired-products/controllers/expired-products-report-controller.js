function ExpiredProductsReportController($scope, $controller, $filter, ExpiredProductsService, DateFormatService,
                                         ReportGroupSortAndFilterService) {
  $controller('BaseProductReportController', {$scope: $scope});
  $scope.formattedExpiredProductList = [];
  $scope.showExpiredProductsTable = false;
  $scope.expiredProductList = null;
  $scope.filterList = [];
  $scope.filterText = "";
  $scope.isDistrictExpiredProduct = false;
  
  $scope.$on('$viewContentLoaded', function () {
    $scope.loadHealthFacilities();
    onLoadScrollEvent();
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
      
      ExpiredProductsService
        .getExpiredProductList()
        .get(_.pick(overStockParams, function (param) {
          if (!utils.isEmpty(param)) {
            return param;
          }
        }), {}, function (overStockResponse) {
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
    $scope.formattedOverStockList = formatOverStockList($scope.filterList);
    
  };
  
  function formatOverStockProductListTime(overStockList) {
    return _.map(overStockList, function (overStockItem) {
      overStockItem.cmm = toFixedNumber(overStockItem.cmm);
      overStockItem.mos = toFixedNumber(overStockItem.mos);
      return overStockItem;
    });
    
  }
  
  function onLoadScrollEvent() {
    var fixedBodyDom = document.getElementById("fixed-body");
    fixedBodyDom.onscroll = function () {
      var fixedBodyDomLeft = this.scrollLeft;
      document.getElementById("fixed-header").scrollLeft = fixedBodyDomLeft;
    };
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
        
        if (!$scope.isDistrictExpiredProduct) {
          _.assign(formatItem, {
            cmm: toFixedNumber(overStock.cmm),
            mos: toFixedNumber(overStock.mos),
            rowSpan: overStock.lotList.length,
            isFirst: index === 0
          });
        }
        
        formattedOverStockList.push(formatItem);
      });
    });
    
    return formattedOverStockList;
  }
  
  function toFixedNumber(originNumber) {
    if (_.isNull(originNumber)) {
      return null;
    }
    
    return parseFloat(originNumber.toFixed(2));
  }
}

services.factory('ExpiredProductsService', function ($resource, $filter, ReportExportExcelService, messageService) {
  function getExpiredProductList() {
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
      _.pick(data, function (param) {
        if (!utils.isEmpty(param)) {
          return param;
        }
      }), messageService.get('report.file.over.stock.products.report'));
  }
  
  return {
    getExpiredProductList: getExpiredProductList,
    getDataForExport: getDataForExport
  };
});