/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('Upload Controller Test', function () {

  var scope, ctrl, httpBackend, supportedUploads, http, controller, messageService;

  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));

  beforeEach(inject(function ($httpBackend, $rootScope, $controller, $http, _messageService_) {
    scope = $rootScope.$new();
    controller = $controller;
    httpBackend = $httpBackend;
    http = $http;
    messageService = _messageService_;
    spyOn(messageService, 'get').andCallFake(function(messageKey){
      if (messageKey == 'upload.select.type') return 'select upload type';
      if (messageKey == 'upload.select.file') return 'select file';
    });
    supportedUploads = {"supportedUploads":{'product':{'displayName':'Product'}}};

    httpBackend.when('GET', '/supported-uploads.json').respond(supportedUploads);

    ctrl = controller(UploadController, {$scope:scope, $http:http, messageService:messageService});
  }));

  it('should get uploads supported by system data', function () {
    httpBackend.flush();
    expect(scope.supportedUploads).toEqual({'product':{'displayName':'Product'}});
  });

  it('should show error message if form is invalid', function () {
    var formData = ['value:', 'value:'];

    scope.uploadForm = [];
    scope.uploadForm['model'] = {'errorMessage':''};
    scope.uploadForm['csvFile'] =  {'errorMessage':''};

    ctrl.validate(formData);

    expect(scope.uploadForm['model'].errorMessage).toEqual('select upload type');
    expect(scope.uploadForm['csvFile'].errorMessage).toEqual('select file');
  });

});
