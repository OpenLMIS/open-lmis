function SingleFacilityReportController($scope, $filter, $controller, $http, NewReportService,
                                        CubesGenerateCutParamsService, CubesGenerateUrlService,
                                        FeatureToggleService, $cacheFactory, $timeout,
                                        LotExpiryDateService, $window, messageService,
                                        DateFormatService, ReportExportExcelService,
                                        ReportGroupSortAndFilterService) {
  $controller('BaseProductReportController', {$scope: $scope});
  
  $scope.filterList = [];
  $scope.filterText = "";
  
  var CMM_STATUS = {
    'STOCK OUT': 'stock-out',
    'REGULAR STOCK': 'regular-stock',
    'OVER STOCK': 'over-stock',
    'LOW STOCK': 'low-stock'
  };

  if ($cacheFactory.get('keepHistoryInStockOnHandPage') === undefined) {
    $scope.cache = $cacheFactory('keepHistoryInStockOnHandPage', {capacity: 10});
  }
  else {
    $scope.cache = $cacheFactory.get('keepHistoryInStockOnHandPage');
    if ($scope.cache.get('saveDataOfStockOnHand') === "yes") {
      $timeout(function waitHistorySelectShow() {
        if ($('.select2-container .select2-choice .select2-chosen').html() !== undefined) {
          $scope.reportParams.facilityId = $scope.cache.get('dataOfStockOnHandReport').facilityId;
          $scope.reportParams.provinceId = $scope.cache.get('dataOfStockOnHandReport').provinceId;
          $scope.reportParams.districtId = $scope.cache.get('dataOfStockOnHandReport').districtId;
          $scope.reportParams.endTime = $filter('date')($scope.cache.get('dataOfStockOnHandReport').endTime, "yyyy-MM-dd");
          loadReportAction();
        } else {
          $timeout(waitHistorySelectShow, 1000);
        }
      }, 1000);
    }
  }

  $scope.hasSyncTimeColumn = false;

  $scope.$on('$viewContentLoaded', function () {

    FeatureToggleService.get({key: 'view.stock.movement'}, function (result) {
      $scope.viewStockMovementToggle = result.key;
    });

    FeatureToggleService.get({key: 'lot.expiry.dates.report'}, function (result) {
      $scope.isLotExpiryDatesToggleOn = result.key;
    });

    $scope.loadHealthFacilities();
  });
  
  var sortList = ['lotNumber', 'stockOnHandOfLot'];
  var ignoreSearchList = ['expiryDate', 'productName', 'facilityCode'];
  var timeFieldList = ['expiry_date', 'syncDate'];
  $scope.filterAndSort = function () {
    $scope.filterList = ReportGroupSortAndFilterService.search($scope.originData, $scope.filterText, "lotList", timeFieldList, ignoreSearchList);
    $scope.filterList = ReportGroupSortAndFilterService.groupSort($scope.filterList, $scope.sortType, $scope.sortReverse, sortList);
    $scope.reportData = formatAllProductList($scope.filterList);
  };
  
  $scope.loadReport = loadReportAction;
  
  $scope.cmmStatusStyle = function (status) {
    return CMM_STATUS[status];
  };
  
  function loadReportAction() {
    if ($scope.validateSingleFacility()) {
      var reportParams = $scope.reportParams;
    
      var allProductParams = {
        endTime: $filter('date')(reportParams.endTime, "yyyy-MM-dd") + " 23:59:59",
        provinceId: reportParams.provinceId.toString(),
        districtId: reportParams.districtId.toString(),
        facilityId: reportParams.facilityId.toString(),
        reportType: "stockOnHandAll"
      };
    
      $scope.formattedOverStockList = [];
    
      NewReportService.get(utils.pickEmptyObject(allProductParams))
        .$promise.then(function (allProductResponse) {
        $scope.originData = allProductResponse.data;
        $scope.originData = formatAllProductListForSearch($scope.originData);
        $scope.reportData = formatAllProductList($scope.originData);
        $scope.filterAndSort();
      });
    }
  }
  
  function formatAllProductListForSearch(data) {
    var formattedSingleProductList = [];
    _.forEach(data, function (item) {
      var formatItem = {
        facilityCode: item.facilityCode,
        facilityName: item.facilityName,
        productCode: item.productCode,
        productName: item.productName,
        stockOnHandStatus: item.stockOnHandStatus.replace('_', ' '),
        sumStockOnHand: item.sumStockOnHand,
        mos: item.mos,
        cmm: item.cmm
      };
      
      var lotList = _.sortBy(item.lotList, function (o) {
        return o.expiryDate;
      });
      
      formatItem.expiry_date = lotList[0].expiryDate;
      formatItem.lotList = lotList;
      formattedSingleProductList.push(formatItem);
    });
    
    
    return formattedSingleProductList;
  }
  
  function formatAllProductList(data) {
    var formattedSingleProductList = [];
    _.forEach(data, function (item) {
      _.forEach(item.lotList, function (lot, index) {
        var formatItem = {
          facilityCode: item.facilityCode,
          facilityName: item.facilityName,
          productCode: item.productCode,
          productName: item.productName,
          stockOnHandStatus: item.stockOnHandStatus,
          expiry_date: DateFormatService.formatDateWithLocale(item.expiry_date),
          lotNumber: lot.lotNumber,
          stockOnHandOfLot: lot.stockOnHandOfLot,
          sumStockOnHand: item.sumStockOnHand,
          cmm: toFixedNumber(item.cmm),
          estimated_months: toFixedNumber(item.mos),
          rowSpan: item.lotList.length,
          isFirst: index === 0
        };
        
        formattedSingleProductList.push(formatItem);
      });
    });
    
    return formattedSingleProductList;
  }
  
  function toFixedNumber(originNumber) {
    if (_.isNull(originNumber)) {
      return null;
    }
    
    return parseFloat(originNumber.toFixed(2));
  }
  
  
  $scope.saveHistory = function () {
    $scope.cache.put('dataOfStockOnHandReport', $scope.reportParams);
    console.log($scope.reportParams);
  };

  $scope.generateRedirectToExpiryDateReportURL = function (productCode, facilityCode) {
    var date = $filter('date')($scope.reportParams.endTime, "yyyy-MM-dd");

    var redirectedURL = "/public/pages/reports/mozambique/index.html#/lot-expiry-dates" + "?" +
        "facilityCode=" + facilityCode + "&" +
        "date=" + date + "&" +
        "drugCode=" + productCode;

    return redirectedURL;
  };

  $scope.redirectToLotExpiryDateReport = function(productCode, facilityCode) {
    $window.location.href = $scope.generateRedirectToExpiryDateReportURL(productCode, facilityCode);
  };

  $scope.exportXLSX = function() {};
}