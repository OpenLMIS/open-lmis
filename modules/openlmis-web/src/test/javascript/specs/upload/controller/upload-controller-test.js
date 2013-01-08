describe('Upload Controller Test', function () {

    var scope, ctrl, httpBackend, supportedUploads, http,controller;

    beforeEach(module('openlmis.services'));

    beforeEach(inject(function ($httpBackend, $rootScope, $controller, $http ) {
        scope = $rootScope.$new();
        controller = $controller;
        httpBackend = $httpBackend;
        http=$http;

        supportedUploads = {"supportedUploads":{'product':{'displayName':'Product'}}};

        httpBackend.when('GET', '/supported-uploads.json').respond(supportedUploads);

        ctrl = controller(UploadController, {$scope:scope, $http:http});
    }));


    it('should get uploads supported by system data', function () {
        httpBackend.flush();
        expect(scope.supportedUploads).toEqual({'product':{'displayName':'Product'}});
    });

});
