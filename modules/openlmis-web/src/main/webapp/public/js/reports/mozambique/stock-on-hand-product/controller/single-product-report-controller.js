function SingleProductReportController($scope, $filter, $controller, $http, CubesGenerateCutParamsService, CubesGenerateUrlService, FeatureToggleService, LotExpiryDateService, $window) {
  $controller('BaseProductReportController', {$scope: $scope});

  $scope.$on('$viewContentLoaded', function () {
    FeatureToggleService.get({key: 'lot.expiry.dates.report'}, function (result) {
      $scope.isLotExpiryDatesToggleOn = result.key;
    });

    $scope.loadProducts();
  });

  function populateDateEntry(cutsParams) {
    var cubesPath = 'vw_daily_full_soh';
    if ($scope.isLotExpiryDatesToggleOn) {
      cubesPath = 'vw_lot_daily_full_soh';
    }

    $http.get(CubesGenerateUrlService.generateFactsUrl(cubesPath, cutsParams)).success(function (sohEntries) {
      $scope.reportData = _.chain(sohEntries)
        .groupBy(function (sohEntry) {
          return sohEntry['drug.drug_code'] + sohEntry['facility.facility_code'];
        })
        .map(function (sameFacilitySameDrugEntries) {
          var maxOccurredDateEntry = _.max(sameFacilitySameDrugEntries, function (entry) {
            return new Date(entry.occurred_date);
          });
          maxOccurredDateEntry.soh = Number(maxOccurredDateEntry.soh);
          maxOccurredDateEntry.facility_name = maxOccurredDateEntry['facility.facility_name'];
          maxOccurredDateEntry.facility_code = maxOccurredDateEntry['facility.facility_code'];
          maxOccurredDateEntry.estimated_months = (maxOccurredDateEntry.cmm === -1.0 || maxOccurredDateEntry.cmm === 0) ? undefined : Math.floor(10 * maxOccurredDateEntry.soh/maxOccurredDateEntry.cmm)/10;

          maxOccurredDateEntry.formatted_expiry_date = $scope.formatMonth(maxOccurredDateEntry.expiry_date) ;
          var rawLastSyncDate = maxOccurredDateEntry.last_sync_date;
          maxOccurredDateEntry.formatted_last_sync_date = $scope.formatDateWithTimeAndLocale(rawLastSyncDate);
          maxOccurredDateEntry.stock_status = $scope.getEntryStockStatus(maxOccurredDateEntry);

          return maxOccurredDateEntry;
        })
        .value();

      $scope.lotOnHandHash = {};
      LotExpiryDateService.populateLotOnHandInformationForSoonestExpiryDate($scope.reportData, $scope.lotOnHandHash);
    });
  }

  $scope.loadReport = function () {
    if (validateProduct()) {
      var params = $scope.reportParams;
      $scope.locationIdToCode(params);
      var selectedProduct = [{'drug.drug_code': $scope.reportParams.productCode}];
      var cutsParams = CubesGenerateCutParamsService.generateCutsParams("occurred", undefined, $filter('date')(params.endTime, "yyyy,MM,dd"),
        params.selectedFacility, selectedProduct, params.selectedProvince, params.selectedDistrict);
      populateDateEntry(cutsParams);
    }
  };

  $scope.generateRedirectToExpiryDateReportURL = function(facilityCode) {
    var date = $filter('date')($scope.reportParams.endTime, "yyyy-MM-dd");

    var redirectedURL = "/public/pages/reports/mozambique/index.html#/lot-expiry-dates" + "?" +
        "facilityCode=" + facilityCode + "&" +
        "date=" + date + "&" +
        "drugCode=" + $scope.reportParams.productCode;
    return redirectedURL;
  };

  $scope.redirectToLotExpiryDateReport = function(facilityCode) {
    $window.location.href = $scope.generateRedirectToExpiryDateReportURL(facilityCode);
  };

  function validateProduct() {
    $scope.invalid = !$scope.reportParams.productCode;
    return !$scope.invalid;
  }

  $scope.partialPropertiesFilter = function(searchValue) {
    return function(entry) {
      var regex = new RegExp(searchValue, "gi");

      return regex.test(entry.cmm.toString()) ||
          regex.test(entry.soh.toString()) ||
          regex.test(entry.expiry_date)||
          regex.test(entry.estimated_months) ||
          regex.test(entry.facility_name) ||
          regex.test(entry.formatted_expiry_date) ||
          regex.test(entry.soonest_expiring_loh) ||
          regex.test(entry.stock_status) ||
          regex.test(entry.formatted_last_sync_date);
    };
  };

}