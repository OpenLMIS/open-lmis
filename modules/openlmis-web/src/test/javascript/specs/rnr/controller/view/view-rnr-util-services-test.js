describe('viewRnrUtilServices', function () {
    var httpBackend, downloadPdfServiceInject, downSimamServiceInject;
    var windowObj = {location: {href: ''}};
    var scope = {};

    beforeEach(module('openlmis.services'));
    beforeEach(module(function ($provide) {
        $provide.value('$window', windowObj);
    }));
    beforeEach(inject(function ($httpBackend, downloadPdfService, downloadSimamService) {
        scope = {};
        httpBackend = $httpBackend;
        downloadPdfServiceInject = downloadPdfService;
        downSimamServiceInject = downloadSimamService;
    }));

    it('should show download button when toggle on', function () {
        var expectedUrl = "/reference-data/toggle/download.pdf.json";
        httpBackend.expect('GET', expectedUrl).respond(200, "{\"key\":true}");

        expect(downloadPdfServiceInject.init).toBeDefined();
        downloadPdfServiceInject.init(scope, 1);
        httpBackend.flush();

        expect(scope.downloadPdf).toBeDefined();
        scope.downloadPdf();
        expect(windowObj.location.href).toEqual('/requisitions/1/pdf');
    });


    it('should not show download button when toggle off', function () {
        var expectedUrl = "/reference-data/toggle/download.pdf.json";
        httpBackend.expect('GET', expectedUrl).respond(200, "{\"key\":false}");

        expect(downloadPdfServiceInject.init).toBeDefined();
        downloadPdfServiceInject.init(scope, 1);
        httpBackend.flush();

        expect(scope.downloadPdf).toBeUndefined();
    });

    it('should show download SIMAM button when toggle on', function () {
        var expectedUrl = "/reference-data/toggle/download.simam.json";
        httpBackend.expect('GET', expectedUrl).respond(200, "{\"key\":true}");

        expect(downSimamServiceInject.init).toBeDefined();
        downSimamServiceInject.init(scope, 1);
        httpBackend.flush();

        expect(scope.downloadSimam).toBeDefined();
        scope.downloadSimam();
        expect(windowObj.location.href).toEqual('/requisitions/1/simam');
    });

    it('should not show download SIMAM button when toggle off', function () {
        var expectedUrl = "/reference-data/toggle/download.simam.json";
        httpBackend.expect('GET', expectedUrl).respond(200, "{\"key\":false}");

        expect(downSimamServiceInject.init).toBeDefined();
        downSimamServiceInject.init(scope, 1);
        httpBackend.flush();

        expect(scope.downloadSimam).toBeUndefined();
    });
});