describe('Report export excel service', function() {
  var reportExportExcelService, httpBackend;
  beforeEach(module('openlmis'));

  beforeEach(function () {
    inject(function (ReportExportExcelService, _$httpBackend_) {
      reportExportExcelService = ReportExportExcelService;
      httpBackend = _$httpBackend_;
    })
  });

  it("should send export request when request body is correct", function () {
    var reportData = {
      reportHeaders: {
      },
      reportContent: []
    };
    var fileName = 'export-file';

    httpBackend.when('POST', '/reports/download/excel').respond(200, true );
    reportExportExcelService.exportAsXlsx(reportData, fileName);

    expect(httpBackend.flush).not.toThrow();
  });
});