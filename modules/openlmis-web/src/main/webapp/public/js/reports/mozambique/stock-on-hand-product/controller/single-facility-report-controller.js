function SingleFacilityReportController($scope, $filter, $controller, $http, CubesGenerateCutParamsService, CubesGenerateUrlService, FeatureToggleService, $cacheFactory, $timeout, LotExpiryDateService, $window, messageService, DateFormatService, ReportExportExcelService) {
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

          maxOccurredDateEntry.formatted_expiry_date = $scope.formatMonth(maxOccurredDateEntry.expiry_date) ;
          var rawLastSyncDate = maxOccurredDateEntry.last_sync_date;
          maxOccurredDateEntry.formatted_last_sync_date = $scope.formatDateWithTimeAndLocale(rawLastSyncDate);
          maxOccurredDateEntry.estimated_months = (maxOccurredDateEntry.cmm === -1.0 || maxOccurredDateEntry.cmm === 0) ? undefined : Math.floor(10 * maxOccurredDateEntry.soh/maxOccurredDateEntry.cmm)/10;
          maxOccurredDateEntry.stock_status = $scope.getEntryStockStatus(maxOccurredDateEntry);
          return maxOccurredDateEntry;
        })
        .value();

      $scope.lotOnHandHash = {};
      LotExpiryDateService.populateLotOnHandInformationForSoonestExpiryDate($scope.reportData, $scope.lotOnHandHash);
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

  $scope.generateRedirectToExpiryDateReportURL = function (drugCode) {
    var date = $filter('date')($scope.reportParams.endTime, "yyyy-MM-dd");

    var redirectedURL = "/public/pages/reports/mozambique/index.html#/lot-expiry-dates" + "?" +
        "facilityCode=" + $scope.reportParams.selectedFacility.code + "&" +
        "date=" + date + "&" +
        "drugCode=" + drugCode;

    return redirectedURL;
  };

  $scope.redirectToLotExpiryDateReport = function(drugCode) {
    $window.location.href = $scope.generateRedirectToExpiryDateReportURL(drugCode);
  };

  function validateFacility() {
    var facilityId = $scope.reportParams.facilityId;
    $scope.invalid = !facilityId || facilityId === ' ';
    return !$scope.invalid;
  }

  $scope.partialPropertiesFilter = function(searchValue) {
    return function(entry) {
      var regex = new RegExp(searchValue, "gi");

      return regex.test(entry.cmm.toString()) ||
          regex.test(entry.soh.toString()) ||
          regex.test(entry.expiry_date)||
          regex.test(entry.estimated_months) ||
          regex.test(entry.formatted_expiry_date) ||
          regex.test(entry.soonest_expiring_loh) ||
          regex.test(entry.stock_status) ||
          regex.test(entry.drug_code) ||
          regex.test(entry.drug_name) ||
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
        var singleFacilitySOHReportContent = {};
        singleFacilitySOHReportContent.drugCode = sohReportData.drug_code;
        singleFacilitySOHReportContent.drugName = sohReportData.drug_name;
        singleFacilitySOHReportContent.province = $scope.reportParams.selectedProvince ? $scope.reportParams.selectedProvince.name : 'All';
        singleFacilitySOHReportContent.district = $scope.reportParams.selectedDistrict ? $scope.reportParams.selectedDistrict.name : 'All';
        singleFacilitySOHReportContent.facility = $scope.reportParams.selectedFacility ? $scope.reportParams.selectedFacility.name : 'All';
        singleFacilitySOHReportContent.quantity = sohReportData.soh;
        singleFacilitySOHReportContent.status = sohReportData.stock_status;
        singleFacilitySOHReportContent.earliestDrugExpiryDate = sohReportData.formatted_expiry_date;
        singleFacilitySOHReportContent.lotStockOnHand = sohReportData.soonest_expiring_loh;
        singleFacilitySOHReportContent.estimatedMonths = sohReportData.estimated_months;
        singleFacilitySOHReportContent.lastUpdateFromTablet = DateFormatService.formatDateWith24HoursTime(sohReportData.last_sync_date);
        data.reportContent.push(singleFacilitySOHReportContent);
      });

      ReportExportExcelService.exportAsXlsx(data, messageService.get('report.file.single.facility.soh.report'));
    }

  };
}