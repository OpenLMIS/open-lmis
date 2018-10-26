function SingleProductReportController($scope, $filter, $controller, $http, CubesGenerateCutParamsService,
                                       CubesGenerateUrlService, FeatureToggleService, LotExpiryDateService,
                                       $window, ReportExportExcelService, messageService, DateFormatService,
                                       ReportGroupSortAndFilterService, NewReportService) {
  $controller('BaseProductReportController', {$scope: $scope});

  $scope.filterList = [];
  $scope.filterText = "";

  var CMM_STATUS = {
    'STOCK_OUT': 'stock-out',
    'REGULAR_STOCK': 'regular-stock',
    'OVER_STOCK': 'over-stock',
    'LOW_STOCK': 'low-stock'
  };

  $scope.$on('$viewContentLoaded', function () {
    FeatureToggleService.get({key: 'lot.expiry.dates.report'}, function (result) {
      $scope.isLotExpiryDatesToggleOn = result.key;
    });

    $scope.loadProducts();
  });

  $scope.hasSyncTimeColumn = true;

  function formatSingleProductListForSearch(data) {
    var formattedSingleProductList = [];
    _.forEach(data, function (item) {
      var formatItem = {
        facilityName: item.facilityName,
        facilityCode: item.facilityCode,
        productName: item.productName,
        stockOnHandStatus: item.stockOnHandStatus,
        sumStockOnHand: item.sumStockOnHand,
        mos: item.mos,
        cmm: item.cmm,
        syncDate: item.syncDate
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

  function formatSingleProductList(data) {
    var formattedSingleProductList = [];
    _.forEach(data, function (item) {
        _.forEach(item.lotList, function (lot, index) {
          var formatItem = {
            facilityName: item.facilityName,
            productName: item.productName,
            stockOnHandStatus: item.stockOnHandStatus,
            facilityCode: item.facilityCode,
            expiry_date: DateFormatService.formatDateWithLocale(item.expiry_date),
            lotNumber: lot.lotNumber,
            stockOnHandOfLot: lot.stockOnHandOfLot,
            sumStockOnHand: item.sumStockOnHand,
            cmm: toFixedNumber(item.cmm),
            estimated_months: toFixedNumber(item.mos),
            syncDate: DateFormatService.formatDateWithLocale(item.syncDate),
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

  var sortList = ['lotNumber', 'stockOnHandOfLot'];
  var ignoreSearchList = ['expiryDate'];
  var timeFieldList = ['expiry_date', 'syncDate'];
  $scope.filterAndSort = function () {
    $scope.filterList = ReportGroupSortAndFilterService.search($scope.originData, $scope.filterText, "lotList", timeFieldList, ignoreSearchList);
    $scope.filterList = ReportGroupSortAndFilterService.groupSort($scope.filterList, $scope.sortType, $scope.sortReverse, sortList);
    $scope.reportData = formatSingleProductList($scope.filterList);
  };

  $scope.loadReport = function () {
    if ($scope.validateProvince() && $scope.validateDistrict() && $scope.validateProduct()) {
      var reportParams = $scope.reportParams;

      var singleProductParams = {
        endTime: $filter('date')(reportParams.endTime, "yyyy-MM-dd") + " 23:59:59",
        provinceId: reportParams.provinceId.toString(),
        districtId: reportParams.districtId.toString(),
        facilityId: reportParams.facilityId.toString(),
        productCode: reportParams.productCode.toString(),
        reportType: "singleStockOnHand"
      };

      $scope.formattedOverStockList = [];

      NewReportService.get(_.pick(singleProductParams, function (param) {
          if (!utils.isEmpty(param)) {
            return param;
          }
        })).$promise.then(function (singleProductResponse) {
          $scope.originData = singleProductResponse.data;
          $scope.originData = formatSingleProductListForSearch($scope.originData);
          $scope.reportData = formatSingleProductList($scope.originData);
          $scope.filterAndSort();
        });
    }
  };

  $scope.generateRedirectToExpiryDateReportURL = function (facilityCode) {
    var date = $filter('date')($scope.reportParams.endTime, "yyyy-MM-dd");

    var redirectedURL = "/public/pages/reports/mozambique/index.html#/lot-expiry-dates" + "?" +
      "facilityCode=" + facilityCode + "&" +
      "date=" + date + "&" +
      "drugCode=" + $scope.reportParams.productCode;
    return redirectedURL;
  };

  $scope.redirectToLotExpiryDateReport = function (facilityCode) {
    $window.location.href = $scope.generateRedirectToExpiryDateReportURL(facilityCode);
  };

  $scope.cmmStatusStyle = function (status) {
    return CMM_STATUS[status];
  };

  $scope.exportXLSX = function () {
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
        lastUpdateFromTablet: messageService.get('report.header.last.update.from.tablet'),
        generatedFor: messageService.get('report.header.generated.for')
      },
      reportContent: []
    };

    if ($scope.reportData) {
      $scope.reportData.forEach(function (sohReportData) {
        var singleProductSOHReportContent = {};
        singleProductSOHReportContent.drugCode = sohReportData["drug.drug_code"];
        singleProductSOHReportContent.drugName = sohReportData["drug.drug_name"];
        singleProductSOHReportContent.province = sohReportData["location.province_name"];
        singleProductSOHReportContent.district = sohReportData["location.district_name"];
        singleProductSOHReportContent.facility = sohReportData["facility.facility_name"];
        singleProductSOHReportContent.quantity = sohReportData.soh;
        singleProductSOHReportContent.status = sohReportData.stock_status;
        singleProductSOHReportContent.earliestDrugExpiryDate = sohReportData.formatted_expiry_date;
        singleProductSOHReportContent.lotStockOnHand = sohReportData.soonest_expiring_loh;
        singleProductSOHReportContent.estimatedMonths = sohReportData.estimated_months;
        singleProductSOHReportContent.lastUpdateFromTablet = DateFormatService.formatDateWith24HoursTime(sohReportData.last_sync_date);
        singleProductSOHReportContent.generatedFor = DateFormatService.formatDateWithDateMonthYearForString($scope.reportParams.endTime);
        data.reportContent.push(singleProductSOHReportContent);
      });
      ReportExportExcelService.exportAsXlsx(data, messageService.get('report.file.single.product.soh.report'));
    }
  };
}