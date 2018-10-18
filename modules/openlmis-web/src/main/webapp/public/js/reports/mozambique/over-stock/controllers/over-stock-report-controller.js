function OverStockReportController($scope, $controller, $filter, OverStockProductsService, DateFormatService) {
  $controller('BaseProductReportController', {$scope: $scope});
  $scope.formattedOverStockList = [];
  $scope.showOverStockProductsTable = false;
  $scope.overStockList = null;
  $scope.filterList = [];
  $scope.filterText = "";
  $scope.isDistrictOverStock = false;

  $scope.$on('$viewContentLoaded', function () {
    $scope.loadHealthFacilities();
    onLoadScrollEvent();
  });

  $scope.loadReport = function () {
    if ($scope.validateProvince() &&
      $scope.validateDistrict() &&
      $scope.validateFacility()) {
      var reportParams = $scope.reportParams;

      var overStockParams = {
        endTime: $filter('date')(reportParams.endTime, "yyyy-MM-dd") + " 23:59:59",
        provinceId: reportParams.provinceId.toString(),
        districtId: reportParams.districtId.toString(),
        facilityId: reportParams.facilityId.toString()
      };

      $scope.isDistrictOverStock = utils.isEmpty(reportParams.districtId);
      $scope.formattedOverStockList = [];

      OverStockProductsService
        .getOverStockProductList()
        .get(_.pick(overStockParams, function (param) {
          if (!utils.isEmpty(param)) {
            return param;
          }
        }), {}, function (overStockResponse) {
        $scope.showOverStockProductsTable = true;
        $scope.formattedOverStockList = formatOverStockList(overStockResponse.rnr_list);
        $scope.showOverStockProductsTable = overStockResponse.rnr_list.length;
        $scope.overStockList = formatOverStockProductListTime(overStockResponse.rnr_list);
        $scope.filterAndSort();
      });
    }
  };

  $scope.exportXLSX = function () {
    var reportParams = $scope.reportParams;

    OverStockProductsService.getDataForExport(
      reportParams.provinceId.toString(),
      reportParams.districtId.toString(),
      reportParams.facilityId.toString(),
      $filter('date')(reportParams.endTime, "yyyy-MM-dd") + " 23:59:59"
    );
  };

  $scope.groupSort = function() {
    if ($scope.sortType) {
      return _.includes(sortList, $scope.sortType) ?
        getSortByNestedObject() :
        $scope.filterList = $filter('orderBy')($scope.filterList, $scope.sortType, $scope.sortReverse);
    }
    return $scope.filterList;
  };

  $scope.filterAndSort = function () {
    $scope.search();
    $scope.groupSort();
    $scope.formattedOverStockList = formatOverStockList($scope.filterList);
  };

  $scope.search = function () {
    $scope.filterList = _.filter($scope.overStockList, function (item) {
      return checkField(item);
    });
  };

  function formatOverStockProductListTime(overStockList) {
    return _.map(overStockList, function (overStockItem) {
      overStockItem.cmm = toFixedNumber(overStockItem.cmm);
      overStockItem.mos = toFixedNumber(overStockItem.mos);
      return overStockItem;
    });
  }

  function onLoadScrollEvent() {
    var fixedBodyDom = document.getElementById("fixed-body");
    fixedBodyDom.onscroll = function () {
      var fixedBodyDomLeft = this.scrollLeft;
      document.getElementById("fixed-header").scrollLeft = fixedBodyDomLeft;
    };
  }

  var sortList = ['lotNumber', 'expiryDate', 'stockOnHandOfLot'];

  function getSortByNestedObject() {
    $scope.filterList = _.sortBy(sortLotItem($scope.filterList, $scope.sortType), function (o) {
      return o.lotList[0][$scope.sortType];
    });
    return $scope.sortReverse ? $scope.filterList.reverse() :
      $scope.filterList;
  }

  function sortLotItem(data, sortType) {
    return _.map(data, function (item) {
      item.lotList = _.sortBy(item.lotList, function (n) {
        return n[sortType];
      });
      $scope.sortReverse ? item.lotList.reverse() : item.lotList;
      return item;
    });
  }

  function checkField(item) {
    var flag = false;
    _.forEach(item, function (value, key) {
      if (key !== "lotList" && checkValueContains(value)) {
        flag = true;
      }
      if (key === "lotList" && checkLotItemInclude(value)) {
        flag = true;
      }
    });
    return flag;
  }

  function checkLotItemInclude(lotLists) {
    return _.find(lotLists, function (lotItem) {
      var flag = false;
      _.forEach(lotItem, function (value, key) {
        if (key === "expiryDate") {
          value = DateFormatService.formatDateWithLocale(value);
        }
        if (checkValueContains(value)) {
          flag = true;
        }
      });
      return flag;
    });
  }

  function checkValueContains(value) {
    return (value + "").toLowerCase().indexOf($scope.filterText.toLowerCase()) > -1;
  }

  function isTimestampValid(dateString) {
    var minDate = new Date('1970-01-01 00:00:01');
    var maxDate = new Date('2038-01-19 03:14:07');
    var date = new Date(dateString);
    return date > minDate && date < maxDate;
  }

  function formatOverStockList(overStockList) {
    var formattedOverStockList = [];
    _.forEach(overStockList, function (overStock) {
      _.forEach(overStock.lotList, function (lot, index) {
        var formatItem = {
          provinceName: overStock.provinceName,
          districtName: overStock.districtName,
          facilityName: overStock.facilityName,
          productCode: overStock.productCode,
          productName: overStock.productName,
          lotNumber: lot.lotNumber,
          expiryDate: DateFormatService.formatDateWithLocale(lot.expiryDate),
          stockOnHandOfLot: lot.stockOnHandOfLot
        };

        if (!$scope.isDistrictOverStock) {
          Object.assign(formatItem, {
            cmm: toFixedNumber(overStock.cmm),
            mos: toFixedNumber(overStock.mos),
            rowSpan: overStock.lotList.length,
            isFirst: index === 0
          });
        }

        formattedOverStockList.push(formatItem);
      });
    });

    return formattedOverStockList;
  }

  function toFixedNumber(originNumber) {
    if (_.isNull(originNumber)) {
      return null;
    }

    return parseFloat(originNumber.toFixed(2));
  }
}

services.factory('OverStockProductsService', function ($resource, $filter, ReportExportExcelService, messageService) {
  function getOverStockProductList() {
    return $resource('/reports/overstock-report', {}, {});
  }

  function getDataForExport(provinceId, districtId, facilityId, endTime) {
    var data = {
      provinceId: provinceId,
      districtId: districtId,
      facilityId: facilityId,
      endTime: endTime,
      reportType: 'overStockProductReport'
    };

    ReportExportExcelService.exportAsXlsxBackend(
      _.pick(data, function (param) {
        if (!utils.isEmpty(param)) {
          return param;
        }
      }), messageService.get('report.file.over.stock.products.report'));
  }

  return {
    getOverStockProductList: getOverStockProductList,
    getDataForExport: getDataForExport
  };
});