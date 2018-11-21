function ALReportController($scope, $controller, $filter, ReportDataServices, messageService) {
  $controller("BaseProductReportController", {$scope: $scope});
  
  $scope.$on('$viewContentLoaded', function () {
    $scope.loadHealthFacilities();
  });
  
  $scope.loadReport = function () {
    if ($scope.validateProvince() &&
      $scope.validateDistrict() &&
      $scope.validateFacility()) {
      var reportParams = $scope.reportParams;
      
      var alReportParams = {
        startTime: $filter('date')(reportParams.startTime, "yyyy-MM-dd") + " 23:59:59",
        endTime: $filter('date')(reportParams.endTime, "yyyy-MM-dd") + " 23:59:59",
        provinceId: reportParams.provinceId.toString(),
        districtId: reportParams.districtId.toString(),
        facilityId: reportParams.facilityId.toString(),
        reportType: 'alReport'
      };
      
      ReportDataServices
        .getProductList()
        .post(utils.pickEmptyObject(alReportParams), function (alReportResponse) {
          $scope.alReportObject = formatALResponseData(alReportResponse.data);
          $scope.responseNoData = _.isEmpty($scope.alReportObject);
        });
    }
  };
  
  $scope.exportXLSX = function () {
    var reportParams = $scope.reportParams;
    var reportParamsObject = {
      provinceId: reportParams.provinceId.toString(),
      districtId: reportParams.districtId.toString(),
      facilityId: reportParams.facilityId.toString(),
      endTime: $filter('date')(reportParams.endTime, "yyyy-MM-dd") + " 23:59:59",
      startTime: $filter('date')(reportParams.startTime, "yyyy-MM-dd") + " 23:59:59",
      reportType: 'alReport'
    };
    
    ReportDataServices.getDataForExport(reportParamsObject,
      messageService.get('report.file.al.report'));
  };
  
  function formatALResponseData(responseData) {
    if (responseData.length >= 3) {
      return {
        hf: responseData[0].splice(1, 8),
        chw: responseData[1].splice(1, 8),
        total: responseData[2].splice(1, 8)
      };
    }
    
    return {};
  }
}
