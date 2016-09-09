function LotExpiryDatesReportController($scope, $controller, $http, CubesGenerateUrlService, messageService, DateFormatService, CubesGenerateCutParamsService) {
  $controller('BaseProductReportController', {$scope: $scope});

  $scope.$on('$viewContentLoaded', function () {
    $scope.loadProducts();
    $scope.loadHealthFacilities();
  });

  $scope.loadReport = function () {
    generateReportTitle();
    queryLotExpiryDatesReportDataFromCubes();
  };

  $scope.highlightDate = function (date) {
    var today = new Date();
    var sixMonthsFromNow = new Date(today.getFullYear(), today.getMonth() - 1 + 6, today.getDate());
    var compareDate = DateFormatService.formatDateWithLastDayOfMonth(sixMonthsFromNow);
    return date <= compareDate;
  };

  function queryLotExpiryDatesReportDataFromCubes() {
    var params = getExpiryDateReportsParams();
    var cutsParams = CubesGenerateCutParamsService.generateCutsParams('occurred', undefined, params.endTime, params.selectedFacility, undefined, params.selectedProvince, params.selectedDistrict);

    $http.get(CubesGenerateUrlService.generateFactsUrl('vw_lot_expiry_dates', cutsParams))
      .success(function (data) {
        generateReportData(data);
      });
  }

  function generateReportData(data) {
    $scope.reportData = [];

    var drugHash = {};

    _.forEach(_.groupBy(data, 'facility.facility_code'), function (item) {
      var expiryDatesForTheFacility = getExpiryDatesBeforeOccurredForFacility(item);

      _.forEach(expiryDatesForTheFacility, function (expiryDateItem, drugCode) {
        if (drugHash[drugCode]) {
          _.forEach(_.keys(expiryDateItem.lot_expiry_dates), function(lotExpiryKey) {
            if (drugHash[drugCode].lot_expiry_dates[lotExpiryKey] !== undefined) {
              drugHash[drugCode].lot_expiry_dates[lotExpiryKey] += expiryDateItem.lot_expiry_dates[lotExpiryKey];
            } else {
              drugHash[drugCode].lot_expiry_dates[lotExpiryKey] = expiryDateItem.lot_expiry_dates[lotExpiryKey];
            }
          });
        } else {
          drugHash[drugCode] = expiryDateItem;
        }
      });
    });

    _.forEach(_.values(drugHash), function (drug) {
      var expiryDateArray = [];
      _.forEach(_.keys(drug.lot_expiry_dates), function (oneLotExpiryDate) {
        if (drug.lot_expiry_dates[oneLotExpiryDate] > 0) {
          var lotExpiryDateObj = {
            "lot_expiry": oneLotExpiryDate,
            "lot_on_hand": drug.lot_expiry_dates[oneLotExpiryDate]
          };
          expiryDateArray.push(lotExpiryDateObj);
        }
      });
      drug.lot_expiry_dates = expiryDateArray;
      $scope.reportData.push(drug);
    });
  }

  function formatExpiryDate(expiryDate) {
    var options = {year: 'numeric', month: 'short'};
    return new Date(expiryDate).toLocaleString(locale, options);
  }

  function getExpiryDatesBeforeOccurredForFacility(dataForOneFacility) {
    var drugOccurredHash = {};
    _.forEach(dataForOneFacility, function (item) {
      var createdDate = item.createddate;
      var occurredDate = item.occurred;
      var drugCode = item['drug.drug_code'];

      if (drugOccurredHash[drugCode]) {
        if (drugOccurredHash[drugCode].lot_expiry_dates[item.lot_number + " - " + formatExpiryDate(item.expiry_dates)] === undefined || ((occurredDate === drugOccurredHash[drugCode].occurred_date && createdDate >= drugOccurredHash[drugCode].createddate) || occurredDate > drugOccurredHash[drugCode].occurred_date)) {
          drugOccurredHash[drugCode].occurred_date = occurredDate;
          drugOccurredHash[drugCode].createddate = createdDate;
          drugOccurredHash[drugCode].lot_expiry_dates[item.lot_number + " - " + formatExpiryDate(item.expiry_dates)] = item.lotonhand;
        }
      } else {
        drugOccurredHash[drugCode] = {
          code: drugCode,
          name: item['drug.drug_name'],
          createddate: createdDate,
          occurred_date: occurredDate,
          lot_expiry_dates: {},
          facility_code: item['facility.facility_code']
        };
        drugOccurredHash[drugCode].lot_expiry_dates[item.lot_number + " - " + formatExpiryDate(item.expiry_dates)] = item.lotonhand;
      }
    });
    return drugOccurredHash;
  }

  function getExpiryDateReportsParams() {
    var params = {};
    params.endTime = new Date($scope.reportParams.endTime).setHours(23, 59, 59, 999);
    $scope.locationIdToCode(params);
    return params;
  }

  function generateReportTitle() {
    var expiryDatesReportParams = getExpiryDateReportsParams();
    var reportTitle = "";
    if (expiryDatesReportParams.selectedProvince) {
      reportTitle = expiryDatesReportParams.selectedProvince.name;
    }
    if (expiryDatesReportParams.selectedDistrict) {
      reportTitle += ("," + expiryDatesReportParams.selectedDistrict.name);
    }
    if (expiryDatesReportParams.selectedFacility) {
      reportTitle += reportTitle === "" ? expiryDatesReportParams.selectedFacility.name : ("," + expiryDatesReportParams.selectedFacility.name);
    }
    $scope.reportParams.reportTitle = reportTitle || messageService.get("label.all");
  }
}