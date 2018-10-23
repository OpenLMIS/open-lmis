function StockMovementReportController($scope, $routeParams, Facility, $http, CubesGenerateUrlService,
                                       DateFormatService, $cacheFactory, $filter) {
  var currentDate = new Date(),
    DATE_FORMAT = 'yyyy,MM,dd';
  
  var thisPeriodStartDate = currentDate.getDate() < 21 ?
    new Date(currentDate.getFullYear(), currentDate.getMonth() - 1, 21) :
    new Date(currentDate.getFullYear(), currentDate.getMonth(), 21);
  
  var threePeriodsBeforeIncludingCurrent = new Date(thisPeriodStartDate.getFullYear(), thisPeriodStartDate.getMonth() - 2, 21);
  if (thisPeriodStartDate.getMonth() - 2 > currentDate.getMonth()) {
    threePeriodsBeforeIncludingCurrent = new Date(threePeriodsBeforeIncludingCurrent).setYear(currentDate.getFullYear() - 1);
  }
  
  var twelvePeriodsBeforeIncludingCurrent = new Date(thisPeriodStartDate.getFullYear(), thisPeriodStartDate.getMonth() - 11, 21);
  if (thisPeriodStartDate.getMonth() - 11 > currentDate.getMonth()) {
    twelvePeriodsBeforeIncludingCurrent = new Date(twelvePeriodsBeforeIncludingCurrent).setYear(currentDate.getFullYear() - 2);
  } else {
    twelvePeriodsBeforeIncludingCurrent = new Date(twelvePeriodsBeforeIncludingCurrent).setYear(currentDate.getFullYear() - 1);
  }
  
  var periodOptions = {
    'period': thisPeriodStartDate,
    '3periods': threePeriodsBeforeIncludingCurrent,
    'year': twelvePeriodsBeforeIncludingCurrent
  };
  
  $scope.periodTags = Object.keys(periodOptions);
  $scope.selectedPeriodTag = '';
  $scope.isSelectDateRange = false;
  $scope.dateRange = {
    startTime: null,
    endTime: null
  };
  
  $scope.loadFacilityAndStockMovements = function () {
    if ($cacheFactory.get('keepHistoryInStockOnHandPage') !== undefined) {
      $cacheFactory.get('keepHistoryInStockOnHandPage').put('saveDataOfStockOnHand', "yes");
    }
    if ($cacheFactory.get('stockOutReportParams') !== undefined) {
      $cacheFactory.get('stockOutReportParams').put('shouldLoadStockOutReportSingleProductFromCache', "yes");
    }
    
    loadStockMovements();
  };
  
  $scope.changePeriodOption = function (periodTag) {
    $scope.selectedPeriodTag = periodTag;
    $scope.dateRange = {
      startTime: DateFormatService.formatDateWithStartDayOfPeriod(new Date(periodOptions[periodTag])),
      endTime: DateFormatService.formatDateWithEndDayOfPeriod(currentDate)
    };
    
    loadStockMovements();
  };
  
  $scope.$on('messagesPopulated', function () {
    $scope.formatDate();
  });
  
  $scope.formatDate = function (dateString) {
    return DateFormatService.formatDateWithLocale(dateString);
  };
  
  $scope.selectedDateRange = function () {
    var dateRange = $scope.dateRange;
    if (dateRange.startTime && dateRange.endTime) {
      loadStockMovements();
    }
  };
  
  $scope.$on('$viewContentLoaded', function () {
    $scope.productCode = $routeParams.productCode;
    $scope.facilityCode = $routeParams.facilityCode;
    
    $scope.loadFacilityAndStockMovements();
  });
  
  function loadStockMovements() {
    var cuts = [
      {dimension: "movement", values: [$scope.productCode]},
      {dimension: "facility", values: [$scope.facilityCode]}
    ];
    
    if ($scope.selectedPeriodTag ||
      ($scope.dateRange.startTime && $scope.dateRange.endTime)) {
      cuts.push({
        dimension: "movementdate",
        values: [$filter('date')($scope.dateRange.startTime, DATE_FORMAT) + '-' +
        $filter('date')($scope.dateRange.endTime, DATE_FORMAT)],
        skipEscape: true
      });
    }
    
    getStockMovementsAPI(cuts);
  }
  
  function getStockMovementsAPI(cuts) {
    $http.get(CubesGenerateUrlService.generateFactsUrl('vw_stock_movements', cuts)).success(function (data) {
      if (!data.length) {
        $scope.stockMovements = [];
        return;
      }
      
      var firstEntry = data[0];
      $scope.facilityName = firstEntry["facility.facility_name"];
      $scope.district = firstEntry["location.district_name"];
      $scope.province = firstEntry["location.province_name"];
      
      $scope.stockMovements = [];
      _.each(data, function (item) {
        setQuantityByType(item);
        $scope.stockMovements.push(item);
      });
      
      $scope.stockMovements = _.sortBy($scope.stockMovements, function (item) {
        return [item["movement.date"], item["movement.id"]].join("_");
      });
      $scope.stockMovements.reverse();
    });
  }
  
  function setQuantityByType(item) {
    var quantity = Math.abs(item["movement.quantity"]);
    switch (item["movement.type"]) {
      case 'RECEIVE' :
        item.entries = quantity;
        break;
      case 'ISSUE':
        item.issues = quantity;
        break;
      case 'NEGATIVE_ADJUST':
        item.negativeAdjustment = quantity;
        break;
      case 'POSITIVE_ADJUST':
        item.positiveAdjustment = quantity;
        break;
    }
  }
}