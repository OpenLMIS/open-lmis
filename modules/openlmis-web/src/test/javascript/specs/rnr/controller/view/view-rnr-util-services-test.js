describe('viewRnrUtilServices', function () {
    var httpBackend, service;
    var windowObj = {location: {href: ''}};
    var scope = {};

    beforeEach(module('openlmis.services'));
    beforeEach(module(function ($provide) {
        $provide.value('$window', windowObj);
    }));
    beforeEach(inject(function ($httpBackend, downloadPdfService) {
        scope = {};
        httpBackend = $httpBackend;
        service = downloadPdfService;
    }));

    it('should show download button when toggle on', function () {
        var expectedUrl = "/reference-data/toggle/download.pdf.json";
        httpBackend.expect('GET', expectedUrl).respond(200, "{\"key\":true}");

        expect(service.init).toBeDefined();
        service.init(scope, 1);
        httpBackend.flush();

        expect(scope.downloadPdf).toBeDefined();
        scope.downloadPdf();
        expect(windowObj.location.href).toEqual('/requisitions/1/pdf');
    });

    it('should not show download button when toggle off', function () {
        var expectedUrl = "/reference-data/toggle/download.pdf.json";
        httpBackend.expect('GET', expectedUrl).respond(200, "{\"key\":false}");

        expect(service.init).toBeDefined();
        service.init(scope, 1);
        httpBackend.flush();

        expect(scope.downloadPdf).toBeUndefined();
    });
});