/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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
