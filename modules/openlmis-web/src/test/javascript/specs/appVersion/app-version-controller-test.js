describe('app version report controller', function () {
  var scope, reportExportExcelService;

  beforeEach(module('openlmis'));
  beforeEach(module('ui.bootstrap.dialog'));
  beforeEach(inject(function ($rootScope, $controller, ReportExportExcelService) {
    scope = $rootScope.$new();
    reportExportExcelService = ReportExportExcelService;
    $controller(VersionReportController, {$scope: scope});
  }));

  function getExpectedHeaders() {
    var expectedHeader = {
      user: 'label.app.version.user',
      provinceName: 'label.app.version.province.name',
      districtName: 'label.app.version.district.name',
      facilityName: 'label.app.version.facilityname',
      appVersion: 'label.app.version.appversion'
    };
    return expectedHeader;
  }


  iit('should export data with district province name successfully', function () {
    var expectedHeader = getExpectedHeaders();
    scope.appVersions = [{
      userName: "Boane",
      provinceName: "Maputo Província",
      districtName: "Boane",
      facilityName: "Boane",
      appVersion: "1.11.82"
    },
    {
      userName: "Mahanhane",
      provinceName: "Maputo Província",
      districtName: "Mahanhane",
      facilityName: "Mahanhane",
      appVersion: "1.11.82"
    },
    {
      userName: "Mahubo",
      provinceName: "Maputo Província",
      districtName: "Mahubo",
      facilityName: "Mahubo",
      appVersion: "1.11.82"
    }];

    spyOn(reportExportExcelService, 'exportAsXlsx');
    scope.exportXLSX();

    var expectedContent = [{
        user: "Boane",
        provinceName: "Maputo Província",
        districtName: "Boane",
        facilityName: "Boane",
        appVersion: "1.11.82"
      },
      {
        user: "Mahanhane",
        provinceName: "Maputo Província",
        districtName: "Mahanhane",
        facilityName: "Mahanhane",
        appVersion: "1.11.82"
      },
      {
        user: "Mahubo",
        provinceName: "Maputo Província",
        districtName: "Mahubo",
        facilityName: "Mahubo",
        appVersion: "1.11.82"
      }];

    var expectedExcel = {
      reportHeaders: expectedHeader,
      reportContent: expectedContent
    };

    expect(reportExportExcelService.exportAsXlsx).toHaveBeenCalledWith(expectedExcel, 'report.file.app.version');

  });
});