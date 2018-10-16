function OverStockReportController($scope, $controller, $filter, OverStockProductsService, DateFormatService) {
  $controller('BaseProductReportController', {$scope: $scope});
  $scope.formattedOverStockList = [];
  $scope.showOverStockProductsTable = false;
  $scope.overStockList = null;
  $scope.filterList = [];
  $scope.filterText = "";

  $scope.$on('$viewContentLoaded', function () {
    $scope.loadHealthFacilities();
    onLoadScrollEvent();
  });

  $scope.loadReport = function () {
    if ($scope.validateProvince() &&
      $scope.validateDistrict() &&
      $scope.validateFacility()) {
      var reportParams = $scope.reportParams;

      if (!reportParams.provinceId) {
        return;
      }

      var overStockParams = {
        endTime: $filter('date')(reportParams.endTime, "yyyy-MM-dd") + " 23:59:59",
        provinceId: reportParams.provinceId.toString(),
        districtId: reportParams.districtId.toString(),
        facilityId: reportParams.facilityId.toString()
      };

      OverStockProductsService.getOverStockProductList().get(overStockParams, {}, function (overStockResponse) {
        $scope.formattedOverStockList = formatOverStockList(overStockResponse.rnr_list);
        $scope.showOverStockProductsTable = overStockResponse.rnr_list.length;
        $scope.overStockList = overStockResponse.rnr_list;
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

  function onLoadScrollEvent() {
    var fixedBodyDom = document.getElementById("fixed-body");
    fixedBodyDom.onscroll = function(){
      var fixedBodyDomLeft = this.scrollLeft;
      document.getElementById("fixed-header").scrollLeft = fixedBodyDomLeft;
    };
  }

  $scope.search = function () {
    $scope.filterList = _.filter($scope.overStockList, function (item) {
      return checkField(item);
    });
    $scope.formattedOverStockList = formatOverStockList($scope.filterList);
  };

  function checkField(item) {
    var flag = false;
    _.forEach(item, function (value, key) {
      if (key !== "lotList" && (value + "").toLowerCase().indexOf($scope.filterText.toLowerCase()) > -1) {
        flag = true;
      }
      if (key === "lotList" && checkLotNumberInclude(value)) {
        flag = true;
      }
    });
    return flag;
  }

  function checkLotNumberInclude(lotLists) {
    return _.find(lotLists, function (lotItem) {
      return lotItem.lotNumber.indexOf($scope.filterText) !== -1;
    });
  }


  function formatOverStockList(overStockList) {
    var formattedOverStockList = [];
    _.forEach(overStockList, function (overStock) {
      _.forEach(overStock.lotList, function (lot, index) {
        formattedOverStockList.push({
          provinceName: overStock.provinceName,
          districtName: overStock.districtName,
          facilityName: overStock.facilityName,
          productCode: overStock.productCode,
          productName: overStock.productName,
          lotNumber: lot.lotNumber,
          expiryDate: DateFormatService.formatDateWithLocale(lot.expiryDate),
          stockOnHandOfLot: lot.stockOnHandOfLot,
          cmm: toFixedNumber(overStock.cmm),
          mos: toFixedNumber(overStock.mos),
          rowSpan: overStock.lotList.length,
          isFirst: index === 0
        });
      });
    });

    return formattedOverStockList;
  }

  function toFixedNumber(originNumber) {
    if (_.isNull(originNumber)) {
      return 0;
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

    ReportExportExcelService.exportAsXlsxBackend(data, messageService.get('report.file.over.stock.products.report'));
  }

  return {
    getOverStockProductList: getOverStockProductList,
    getDataForExport: getDataForExport
  };
});