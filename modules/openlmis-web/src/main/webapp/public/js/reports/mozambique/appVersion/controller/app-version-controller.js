function VersionReportController($scope, VersionReportService, $cacheFactory, ReportExportExcelService, messageService) {
  $scope.$on('$viewContentLoaded', function () {
    $scope.loadUserSummary();
  });

  $scope.loadUserSummary = function () {
    VersionReportService.get(function (data) {
      $scope.appVersions = data.app_versions;
      $scope.sortType = 'userName';
    });
  };

  if ($cacheFactory.get('keepHistoryInStockOnHandPage') !== undefined) {
    $cacheFactory.get('keepHistoryInStockOnHandPage').put('saveDataOfStockOnHand', "no");
  }
  if ($cacheFactory.get('stockOutReportParams') !== undefined) {
    $cacheFactory.get('stockOutReportParams').put('shouldLoadStockOutReportAllProductsFromCache', "no");
    $cacheFactory.get('stockOutReportParams').put('shouldLoadStockOutReportSingleProductFromCache', "no");
  }

  $scope.partialPropertiesFilter = function (searchValue) {
    return function (entry) {
      var regex = new RegExp(searchValue, "gi");

      return regex.test(entry.userName) ||
        regex.test(entry.facilityName) ||
        regex.test(entry.appVersion);
    };
  };

  $scope.exportXLSX = function () {
    var data = {
      reportHeaders: {
        user: messageService.get('label.app.version.user'),
        provinceName: messageService.get('label.app.version.province.name'),
        districtName: messageService.get('label.app.version.district.name'),
        facilityName: messageService.get('label.app.version.facilityname'),
        appVersion: messageService.get('label.app.version.appversion')
      },
      reportContent: []
    };

    $scope.appVersions.forEach(function (appVersion) {
      var appVersionReportEntry = {};
      appVersionReportEntry.user = appVersion.userName;
      appVersionReportEntry.provinceName = appVersion.provinceName;
      appVersionReportEntry.districtName = appVersion.districtName;
      appVersionReportEntry.facilityName = appVersion.facilityName;
      appVersionReportEntry.appVersion = appVersion.appVersion;

      data.reportContent.push(appVersionReportEntry);
    });

    ReportExportExcelService.exportAsXlsx(data, messageService.get('report.file.app.version'));
  };
}