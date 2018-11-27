function ALReportController($scope, $controller, $filter, ReportDataServices, messageService) {
  $controller("BaseProductReportController", {$scope: $scope});
  $scope.filterText = "";
  
  $scope.$on('$viewContentLoaded', function () {
    $scope.loadHealthFacilities();
  });
  
  $scope.loadReport = function () {
    if ($scope.validateProvince() &&
      $scope.validateDistrict() &&
      $scope.validateFacility()) {
      var reportParams = $scope.reportParams;
      
      var alReportParams = {
        startTime: $filter('date')(reportParams.startTime, 'yyyy-MM-dd') + ' 00:00:00',
        endTime: $filter('date')(reportParams.endTime, 'yyyy-MM-dd') + ' 23:59:59',
        provinceId: reportParams.provinceId.toString(),
        districtId: reportParams.districtId.toString(),
        facilityId: reportParams.facilityId.toString(),
        reportType: 'alReport'
      };
      
      ReportDataServices
        .getProductList()
        .post(utils.pickEmptyObject(alReportParams), function (alReportResponse) {
          var formattedALResponseData = formatALResponseData(alReportResponse.data);
          $scope.total = formattedALResponseData.total || {};
          $scope.HFAndCHWList = formattedALResponseData.HFAndCHWList || [];
          $scope.displayHFAndCHWList = $scope.HFAndCHWList;
        });
    }
  };
  
  $scope.exportXLSX = function () {
    var reportParams = $scope.reportParams;
    var reportParamsObject = {
      provinceId: reportParams.provinceId.toString(),
      districtId: reportParams.districtId.toString(),
      facilityId: reportParams.facilityId.toString(),
      startTime: $filter('date')(reportParams.startTime, 'yyyy-MM-dd') + ' 00:00:00',
      endTime: $filter('date')(reportParams.endTime, 'yyyy-MM-dd') + ' 23:59:59',
      reportType: 'alReport',
      facility: reportParams.facilityName,
      district: reportParams.districtName,
      province: reportParams.provinceName
    };
    
    ReportDataServices.getDataForExport(reportParamsObject,
      messageService.get('report.file.al.report'));
  };
  
  $scope.filterAndSort = function () {
    var regex = new RegExp($scope.filterText, "gi");
  
    $scope.displayHFAndCHWList = $scope.HFAndCHWList.filter(function (HFAndCHW) {
      return _.some(HFAndCHW.value, function(value) {
        return regex.test(value);
      });
    });
  };
  
  function formatALResponseData(responseData) {
    if (responseData.length >= 3) {
      return {
        total: {
          name: 'total',
          value: responseData[2].splice(1, 8),
        },
        HFAndCHWList: [
          {
            name: 'hf',
            value: responseData[0].splice(1, 8),
          },
          {
            name: 'chw',
            value: responseData[1].splice(1, 8),
          }
        ]
      };
    }
    
    return {};
  }
}
