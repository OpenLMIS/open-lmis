function SingleProductReportController($scope, $filter, $controller, $http, CubesGenerateCutParamsService, CubesGenerateUrlService, FeatureToggleService, LotExpiryDateService, $window, ReportExportExcelService, messageService, DateFormatService, SingleProductReportService, ReportGroupSortAndFilterService) {
  $controller('BaseProductReportController', {$scope: $scope});

  $scope.filterList = [];

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
        astSyncDate: item.astSyncDate
      };

      var lotList = _.sortBy(item.lotList, function (o) {
        return o.expiryDate;
      });

      formatItem.earliestExpiryDate = lotList[0].expiryDate;
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
            earliestExpiryDate: DateFormatService.formatDateWithLocale(item.earliestExpiryDate),
            lotNumber: lot.lotNumber,
            stockOnHandOfLot: lot.stockOnHandOfLot,
            sumStockOnHand: item.sumStockOnHand,
            cmm: toFixedNumber(item.cmm),
            estimated_months: toFixedNumber(item.mos),
            astSyncDate: DateFormatService.formatDateWithLocale(item.astSyncDate),
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
  var timeFieldList = ['earliestExpiryDate', 'astSyncDate'];
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

      SingleProductReportService
        .getSingleProductList()
        .get(_.pick(singleProductParams, function (param) {
          if (!utils.isEmpty(param)) {
            return param;
          }
        }), {}, function (singleProductResponse) {
          $scope.originData = [
            {
              "provinceId": 16,
              "provinceName": "Província de Zambezia",
              "districtId": 18,
              "districtName": "Distrito de Mocuba",
              "facilityId": 108,
              "facilityCode": "HF79",
              "facilityName": "CS Intome 1",
              "productId": 620,
              "productCode": "08S23",
              "productName": "Nevirapina(NVP)50mg/5mL 240mlSuspensão",
              "lotList": [
                {
                  "lotNumber": "NR0515028-A",
                  "expiryDate": 1543528800000,
                  "stockOnHandOfLot": 4
                }
              ],
              "cmm": 0.0,
              "mos": null,
              "isHiv": true,
              "sumStockOnHand": 4,
              "stockOnHandStatus": "OVER_STOCK",
              "astSyncDate": 1543528800000
            },
            {
              "provinceId": 16,
              "provinceName": "Província de Zambezia",
              "districtId": 18,
              "districtName": "Distrito de Mocuba",
              "facilityId": 108,
              "facilityCode": "HF79",
              "facilityName": "CS Intome 2",
              "productId": 487,
              "productCode": "08L03",
              "productName": "Isoniazida(H)300mgComprimidos",
              "lotList": [
                {
                  "lotNumber": "15TIB007A",
                  "expiryDate": 1567202400000,
                  "stockOnHandOfLot": 0
                },
                {
                  "lotNumber": "17TIB065A",
                  "expiryDate": 1612044000000,
                  "stockOnHandOfLot": 1344
                },
                {
                  "lotNumber": "16TIBO47A",
                  "expiryDate": 1588197600000,
                  "stockOnHandOfLot": 0
                }
              ],
              "cmm": 0.0,
              "mos": null,
              "isHiv": false,
              "sumStockOnHand": 1344,
              "stockOnHandStatus": "OVER_STOCK",
              "astSyncDate": 1612044000000
            },
            {
              "provinceId": 16,
              "provinceName": "Província de Zambezia",
              "districtId": 18,
              "districtName": "Distrito de Mocuba",
              "facilityId": 108,
              "facilityCode": "HF79",
              "facilityName": "CS Intome 3",
              "productId": 816,
              "productCode": "12D09Z",
              "productName": "Axeroftol+Alfa tocoferol200 000UI+40UICápsulas",
              "lotList": [
                {
                  "lotNumber": "SEM-LOTE-12D09Z-032018",
                  "expiryDate": 1602447200000,
                  "stockOnHandOfLot": 100
                },
                {
                  "lotNumber": "S172064",
                  "expiryDate": 1580421600000,
                  "stockOnHandOfLot": 0
                },
                {
                  "lotNumber": "S162169",
                  "expiryDate": 1556575200000,
                  "stockOnHandOfLot": 1
                }
              ],
              "cmm": 0.0,
              "mos": null,
              "isHiv": false,
              "sumStockOnHand": 100,
              "stockOnHandStatus": "OVER_STOCK",
              "astSyncDate": 1556575200000
            },
            {
              "provinceId": 16,
              "provinceName": "Província de Zambezia",
              "districtId": 18,
              "districtName": "Distrito de Mocuba",
              "facilityId": 108,
              "facilityCode": "HF79",
              "facilityName": "CS Intome 4",
              "productId": 1543,
              "productCode": "11A23A",
              "productName": "Sais de Rehidratacao Oral 27.9g, WHO mod. Saquetas",
              "lotList": [
                {
                  "lotNumber": "160411",
                  "expiryDate": 1553983200000,
                  "stockOnHandOfLot": 0
                },
                {
                  "lotNumber": "NO7084",
                  "expiryDate": 1582927200000,
                  "stockOnHandOfLot": 0
                },
                {
                  "lotNumber": "NO7085",
                  "expiryDate": 1582927200000,
                  "stockOnHandOfLot": 0
                },
                {
                  "lotNumber": "NO7086",
                  "expiryDate": 1582927200000,
                  "stockOnHandOfLot": 0
                },
                {
                  "lotNumber": "NO7292",
                  "expiryDate": 1635631200000,
                  "stockOnHandOfLot": 0
                },
                {
                  "lotNumber": "NO7291",
                  "expiryDate": 1601416800000,
                  "stockOnHandOfLot": 0
                },
                {
                  "lotNumber": "NO7289",
                  "expiryDate": 1601416800000,
                  "stockOnHandOfLot": 50
                },
                {
                  "lotNumber": "NO7290",
                  "expiryDate": 1538258400000,
                  "stockOnHandOfLot": 250
                }
              ],
              "cmm": 0.0,
              "mos": null,
              "isHiv": false,
              "sumStockOnHand": 300,
              "stockOnHandStatus": "OVER_STOCK",
              "astSyncDate": 1538258400000
            }
          ];
          $scope.originData = formatSingleProductListForSearch($scope.originData);
          $scope.reportData = formatSingleProductList($scope.originData);
          $scope.filterAndSort();
          // $scope.reportData = formatSingleProductList(singleProductResponse.rnr_list);
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

services.factory('SingleProductReportService', function ($resource, $filter, ReportExportExcelService, messageService) {
  function getSingleProductList() {
    return $resource('/reports/overstock-report', {}, {});
  }

  return {
    getSingleProductList: getSingleProductList
  };
});