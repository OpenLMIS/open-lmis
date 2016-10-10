function SingleFacilityReportController($scope, $filter, $controller, $http, CubesGenerateCutParamsService, CubesGenerateUrlService, FeatureToggleService, $cacheFactory, $timeout) {
  $controller('BaseProductReportController', {$scope: $scope});

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

  $scope.$on('$viewContentLoaded', function () {

    FeatureToggleService.get({key: 'view.stock.movement'}, function (result) {
      $scope.viewStockMovementToggle = result.key;
    });

    FeatureToggleService.get({key: 'lot.expiry.dates.report'}, function (result) {
      $scope.isLotExpiryDatesToggleOn = result.key;
    });

    $scope.loadHealthFacilities();
  });

  $scope.loadReport = loadReportAction;

  function populateDataEntry(cutsParams) {
    var cubesPath = 'vw_daily_full_soh';
    if ($scope.isLotExpiryDatesToggleOn) {
      cubesPath = 'vw_lot_daily_full_soh';
    }

    $http.get(CubesGenerateUrlService.generateFactsUrl(cubesPath, cutsParams)).success(function (sohEntries) {
      $scope.reportData = _.chain(sohEntries)
        .groupBy(function (sohEntry) {
          return sohEntry['drug.drug_code'];
        })
        .map(function (sameCodeEntries) {
          var maxOccurredDateEntry = _.max(sameCodeEntries, function (entry) {
            return new Date(entry.occurred_date);
          });
          maxOccurredDateEntry.soh = Number(maxOccurredDateEntry.soh);

          maxOccurredDateEntry.drug_name = maxOccurredDateEntry['drug.drug_name'];
          maxOccurredDateEntry.drug_code = maxOccurredDateEntry['drug.drug_code'];
          return maxOccurredDateEntry;
        })
        .value();
    });
  }

  function loadReportAction() {
    if (validateFacility()) {
      var params = $scope.reportParams;
      $scope.locationIdToCode(params);
      var cutsParams = CubesGenerateCutParamsService.generateCutsParams("occurred", undefined, $filter('date')(params.endTime, "yyyy,MM,dd"),
        params.selectedFacility, undefined, params.selectedProvince, params.selectedDistrict);
      populateDataEntry(cutsParams);
    }
  }

  $scope.saveHistory = function () {
    $scope.cache.put('dataOfStockOnHandReport', $scope.reportParams);
    console.log($scope.reportParams);
  };

  function validateFacility() {
    var facilityId = $scope.reportParams.facilityId;
    $scope.invalid = !facilityId || facilityId === ' ';
    return !$scope.invalid;
  }
}