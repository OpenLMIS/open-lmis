describe('Report export excel service', function () {
    var reportExportExcelService, httpBackend, dateFormatService;
    beforeEach(module('openlmis'));

    beforeEach(function () {
        inject(function (ReportExportExcelService, _$httpBackend_, DateFormatService) {
            reportExportExcelService = ReportExportExcelService;
            dateFormatService = DateFormatService;
            httpBackend = _$httpBackend_;
        })
    });

    it("should send export request when request body is correct", function () {
        var reportData = {
            reportHeaders: {},
            reportContent: []
        };
        var fileName = 'export-file';

        httpBackend.when('POST', '/reports/download/excel').respond(200, true);
        reportExportExcelService.exportAsXlsx(reportData, fileName);

        expect(httpBackend.flush).not.toThrow();
    });


    it("should add the export date in the file name text", function () {
        var fileName = 'export-file';
        var date = new Date();
        var expectedResult = fileName + '_' + dateFormatService.formatDateElementsTwoCharacters(date.getFullYear()) + '_'
            + dateFormatService.formatDateElementsTwoCharacters((date.getMonth() + 1)) + '_'
            + dateFormatService.formatDateElementsTwoCharacters(date.getDate()) + '_at_'
            + dateFormatService.formatDateElementsTwoCharacters(date.getHours()) + '.'
            + dateFormatService.formatDateElementsTwoCharacters(date.getMinutes()) + '.'
            + dateFormatService.formatDateElementsTwoCharacters(date.getSeconds());
        var result = reportExportExcelService.formatFileNameWithDate(fileName);
        expect(expectedResult).toEqual(result);
    });


    it("should method formatFileNameWithDate has been called", function () {
        var reportData = {
            reportHeaders: {},
            reportContent: []
        };
        var fileName = 'export-file';

        spyOn(dateFormatService, 'formatDateWithUnderscore');

        reportExportExcelService.exportAsXlsx(reportData, fileName);

        expect(dateFormatService.formatDateWithUnderscore).toHaveBeenCalled();
    });

});