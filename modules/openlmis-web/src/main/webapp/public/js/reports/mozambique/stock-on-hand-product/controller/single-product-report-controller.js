function SingleProductReportController($scope, $filter, $controller, $http, CubesGenerateCutParamsService, CubesGenerateUrlService, FeatureToggleService, LotExpiryDateService, $window, ReportExportExcelService, messageService, DateFormatService) {
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

  $scope.exportXLSX = function() {
    var data = {
      reportHeaders: {
        drugCode: messageService.get('report.header.drug.code'),
        drugName: messageService.get('report.header.drug.name'),
        province: messageService.get('report.header.province'),
        district: messageService.get('report.header.district'),
        facility: messageService.get('report.header.facility'),
        quantity: messageService.get('report.header.drug.quantity'),
        status: messageService.get('report.header.status'),
        earliestDrugExpiryDate: messageService.get('report.header.earliest.drug.expiry.date'),
        lotStockOnHand: messageService.get('report.header.lot.stock.on.hand'),
        estimatedMonths: messageService.get('report.header.estimated.months'),
        lastUpdateFromTablet: messageService.get('report.header.last.update.from.tablet')
      },
      reportContent: []
    };

    if($scope.reportData) {
      $scope.reportData.forEach(function (sohReportData) {
        var singleProductSOHReportContent = {};
        singleProductSOHReportContent.drugCode = $scope.reportParams.productCode;
        singleProductSOHReportContent.drugName = $scope.getDrugByCode($scope.reportParams.productCode).primaryName;
        singleProductSOHReportContent.province = $scope.reportParams.selectedProvince ? $scope.reportParams.selectedProvince.name : 'All';
        singleProductSOHReportContent.district = $scope.reportParams.selectedDistrict ? $scope.reportParams.selectedDistrict.name : 'All';
        singleProductSOHReportContent.facility = sohReportData.facility_name;
        singleProductSOHReportContent.quantity = sohReportData.soh;
        singleProductSOHReportContent.status = sohReportData.stock_status;
        singleProductSOHReportContent.earliestDrugExpiryDate = sohReportData.formatted_expiry_date;
        singleProductSOHReportContent.lotStockOnHand = sohReportData.soonest_expiring_loh;
        singleProductSOHReportContent.estimatedMonths = sohReportData.estimated_months;
        singleProductSOHReportContent.lastUpdateFromTablet = DateFormatService.formatDateWith24HoursTime(sohReportData.last_sync_date);
        data.reportContent.push(singleProductSOHReportContent);
      });

      ReportExportExcelService.exportAsXlsx(data, messageService.get('report.file.single.drug.soh.report'));
    }

  };

}