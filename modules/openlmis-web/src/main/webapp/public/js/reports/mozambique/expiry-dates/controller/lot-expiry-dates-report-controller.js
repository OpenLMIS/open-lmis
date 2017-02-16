function LotExpiryDatesReportController($scope, $controller, $http, CubesGenerateUrlService, messageService, CubesGenerateCutParamsService, $routeParams, DateFormatService, ReportExportExcelService) {
  $controller('BaseProductReportController', {$scope: $scope});

  var expiryDatesReportParams;

  $scope.populateOptions = function () {
    if(!_.isEmpty($routeParams)) {
      $scope.reportParams.endTime = $routeParams.date;
      if($routeParams.facilityCode) {
        $scope.reportParams.facilityId = $scope.getFacilityByCode($routeParams.facilityCode).id;
      }
      $scope.cursor = $routeParams.drug_code;
      $scope.loadReport();
    }
  };

  $scope.$on('$viewContentLoaded', function () {
    $scope.loadProducts();
    $scope.loadHealthFacilities();
  });

  $scope.loadReport = function () {
    if ($scope.validateProvince() && $scope.validateDistrict() && $scope.validateFacility()) {
      generateReportTitle();
      queryLotExpiryDatesReportDataFromCubes();
    }
  };

  $scope.highlightDate = function (date) {
    var selectedDate = new Date($scope.reportParams.endTime);
    var threeMonthsFromOccurred = new Date(selectedDate.getFullYear(), selectedDate.getMonth() + 3, selectedDate.getDate());
    return date <= threeMonthsFromOccurred;
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
          var lotNumber = oneLotExpiryDate.split(" - ")[0];
          var formattedExpiryDate = formatExpiryDate(oneLotExpiryDate.split(" - ")[1]);
          var lotExpiryDateObj = {
            'lot_number': lotNumber,
            'lot_expiry': lotNumber + " - " + formattedExpiryDate,
            'expiry_date': new Date(oneLotExpiryDate.split(" - ")[1]),
            'formatted_expiry_date': formattedExpiryDate,
            'lot_on_hand': drug.lot_expiry_dates[oneLotExpiryDate]
          };
          expiryDateArray.push(lotExpiryDateObj);
        }
      });
      drug.lot_expiry_dates = _.sortBy(_.uniq(expiryDateArray), 'expiry_date');
      if(drug.lot_expiry_dates.length > 0) {
        drug.first_expiry_date = drug.lot_expiry_dates[0].expiry_date;
        $scope.reportData.push(drug);
      }
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
        if (drugOccurredHash[drugCode].lot_expiry_dates[item.lot_number + " - " + item.expiry_date] === undefined || ((occurredDate === drugOccurredHash[drugCode].occurred_date && createdDate >= drugOccurredHash[drugCode].createddate) || occurredDate > drugOccurredHash[drugCode].occurred_date)) {
          drugOccurredHash[drugCode].occurred_date = occurredDate;
          drugOccurredHash[drugCode].createddate = createdDate;
          drugOccurredHash[drugCode].lot_expiry_dates[item.lot_number + " - " + item.expiry_date] = item.lotonhand;
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
        drugOccurredHash[drugCode].lot_expiry_dates[item.lot_number + " - " + item.expiry_date] = item.lotonhand;
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
    expiryDatesReportParams = getExpiryDateReportsParams();
    var reportTitle = "";
    if (expiryDatesReportParams.selectedProvince) {
      reportTitle = expiryDatesReportParams.selectedProvince.name;
    }
    if (expiryDatesReportParams.selectedDistrict) {
      reportTitle = expiryDatesReportParams.selectedDistrict.name;
    }
    if (expiryDatesReportParams.selectedFacility) {
      reportTitle = expiryDatesReportParams.selectedFacility.name;
    }
    $scope.reportParams.reportTitle = reportTitle || messageService.get("label.all");
  }

  $scope.partialPropertiesFilter = function(searchValue) {
    return function(entry) {
      var regex = new RegExp(searchValue, "gi");

      return regex.test(entry.code) ||
          regex.test(entry.name) ||
          regex.test(_.pluck(entry.lot_expiry_dates, 'lot_expiry').join(' '));
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

        lot: messageService.get('report.header.lot'),
        expiryDate: messageService.get('report.header.expiry.date'),
        soh: messageService.get('report.header.stock.on.hand'),
        reportGenerateDate: messageService.get('report.header.report.generate.date')
      },
      reportContent: []
    };

    if($scope.reportData) {

      $scope.reportData.forEach(function (expiryDateReportData) {
        expiryDateReportData.lot_expiry_dates.forEach(function(lot) {
          var expiryDateReportContent = {};
          expiryDateReportContent.drugCode = expiryDateReportData.code;
          expiryDateReportContent.drugName = expiryDateReportData.name;
          expiryDateReportContent.province = expiryDatesReportParams.selectedProvince ? expiryDatesReportParams.selectedProvince.name : '[All]';
          expiryDateReportContent.district = expiryDatesReportParams.selectedDistrict ? expiryDatesReportParams.selectedDistrict.name : '[All]';
          expiryDateReportContent.facility = expiryDatesReportParams.selectedFacility ? expiryDatesReportParams.selectedFacility.name : '[All]';
          expiryDateReportContent.lot = lot.lot_number;
          expiryDateReportContent.expiryDate = lot.formatted_expiry_date;
          expiryDateReportContent.soh = lot.lot_on_hand;
          expiryDateReportContent.reportGenerateDate = DateFormatService.formatDateWithLocale(expiryDatesReportParams.endTime);
          data.reportContent.push(expiryDateReportContent);
        });
      });

      ReportExportExcelService.exportAsXlsx(data, messageService.get('report.file.expiry.dates.report'));
    }

  };

  $scope.$on('ngRepeatFinished', function () {
    if (!_.isEmpty($routeParams)) {
      $(".content-table")[0].scrollTop += ($("#" + $routeParams.drugCode).offset().top - $(".content-table").offset().top - 10);
      $("#" + $routeParams.drugCode).parent().parent().addClass("highlight");
    }
  });
}