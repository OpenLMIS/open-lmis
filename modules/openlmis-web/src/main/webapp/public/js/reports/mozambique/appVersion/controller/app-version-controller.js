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
  if ($cacheFactory.get('keepHistoryInStockOutReportPage') !== undefined) {
    $cacheFactory.get('keepHistoryInStockOutReportPage').put('saveDataOfStockOutReport', "no");
    $cacheFactory.get('keepHistoryInStockOutReportPage').put('saveDataOfStockOutReportForSingleProduct', "no");
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
        facilityName: messageService.get('label.app.version.facilityname'),
        appVersion: messageService.get('label.app.version.appversion')
      },
      reportContent: []
    };

    $scope.appVersions.forEach(function (appVersion) {
      var appVersionReportEntry = {};
      appVersionReportEntry.user = appVersion.userName;
      appVersionReportEntry.facilityName = appVersion.facilityName;
      appVersionReportEntry.appVersion = appVersion.appVersion;

      data.reportContent.push(appVersionReportEntry);
    });

    ReportExportExcelService.exportAsXlsx(data, messageService.get('report.file.app.version'));
  };
}