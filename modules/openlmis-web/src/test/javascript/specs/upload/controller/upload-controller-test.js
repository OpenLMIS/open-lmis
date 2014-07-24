/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('Upload Controller Test', function () {

  var scope, ctrl, httpBackend, supportedUploads, http, controller, messageService;
  var loginConfig;
  beforeEach(module('openlmis'));

  beforeEach(inject(function ($httpBackend, $rootScope, $controller, $http, _messageService_) {
    scope = $rootScope.$new();
    controller = $controller;
    httpBackend = $httpBackend;
    http = $http;
    messageService = _messageService_;
    loginConfig = {modalShown: false, preventReload: true};
    spyOn(messageService, 'get').andCallFake(function (messageKey) {
      if (messageKey == 'upload.select.type') return 'select upload type';
      if (messageKey == 'upload.select.file') return 'select file';
    });
    supportedUploads = {"supportedUploads": {'product': {'displayName': 'Product'}}};

    httpBackend.when('GET', '/supported-uploads.json').respond(supportedUploads);

    ctrl = controller(UploadController,
      {$scope: scope, $http: http, messageService: messageService, loginConfig: loginConfig});
  }));

  it('should get uploads supported by system data', function () {
    httpBackend.flush();
    expect(scope.supportedUploads).toEqual({'product': {'displayName': 'Product'}});
  });

  it('should clear messages', function () {
    scope.successMsg = scope.errorMsg = "Message";

    scope.clearMessages();

    expect(scope.successMsg).toEqual("");
    expect(scope.errorMsg).toEqual("");
  });

  describe('Upload form', function () {

    beforeEach(function () {
      scope.uploadForm = [];
      scope.uploadForm['model'] = {'errorMessage': ''};
      scope.uploadForm['csvFile'] = {'errorMessage': ''};
    });

    it('should show error message if first field is blank', function () {
      var formData = [
        {value: ''},
        {value: 'file'}
      ];
      scope.validate(formData);
      expect(scope.uploadForm['model'].errorMessage).toEqual('select upload type');
      expect(scope.uploadForm['csvFile'].errorMessage).toEqual('');
      expect(scope.inProgress).toEqual(false);
      expect(scope.errorMsg).toEqual('');
      expect(scope.successMsg).toEqual('');
    });

    it('should show error message if second field is blank', function () {
      var formData = [
        {value: 'facility'},
        {value: ''}
      ];
      scope.validate(formData);
      expect(scope.uploadForm['model'].errorMessage).toEqual('');
      expect(scope.uploadForm['csvFile'].errorMessage).toEqual('select file');
      expect(scope.inProgress).toEqual(false);
      expect(scope.errorMsg).toEqual('');
      expect(scope.successMsg).toEqual('');
    });

    it('should show error message if all fields are blank', function () {
      var formData = [
        {value: ''},
        {value: ''}
      ];
      scope.validate(formData);
      expect(scope.uploadForm['model'].errorMessage).toEqual('select upload type');
      expect(scope.uploadForm['csvFile'].errorMessage).toEqual('select file');
      expect(scope.inProgress).toEqual(false);
      expect(scope.errorMsg).toEqual('');
      expect(scope.successMsg).toEqual('');
    });

    it('should submit form if all fields are correctly filled', function () {
      var formData = [
        {value: 'facility'},
        {value: 'file'}
      ];
      scope.validate(formData);
      expect(scope.inProgress).toEqual(true);
      expect(scope.errorMsg).toEqual('');
      expect(scope.successMsg).toEqual('');
    });
  });
});
