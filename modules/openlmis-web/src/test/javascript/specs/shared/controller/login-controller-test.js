/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe("LoginController", function () {

  beforeEach(module('openlmis.services'));

  beforeEach(module('openlmis.localStorage'));

  var scope, ctrl, httpBackend, messageService, controller;

  beforeEach(inject(function ($rootScope, $controller, _messageService_, _$httpBackend_) {
    httpBackend = _$httpBackend_;
    scope = $rootScope.$new();
    messageService = _messageService_;
    spyOn(messageService, 'get');
    controller = $controller;

    ctrl = controller(LoginController, {$scope: scope, messageService: messageService});

  }));


  it('should not login and show error when login fails', function () {
    scope.username = "john";
    scope.password = "openLmis";

    ctrl = controller(LoginController, {$scope: scope, messageService: messageService});

    spyOn(messageService, 'populate');
    spyOn(location, 'reload');
    httpBackend.when('POST', '/j_spring_security_check').respond({"authenticated": false, "error": "error msg"});

    scope.doLogin();
    httpBackend.flush();

    expect(scope.loginError).toBe('error msg');
  });

  it('should not login and show error when server returns error', function () {
    scope.username = "john";
    scope.password = "openLmis";


    spyOn(messageService, 'populate');
    httpBackend.when('POST', '/j_spring_security_check').respond(403, {'error': 'error msg'});

    scope.doLogin();
    httpBackend.flush();

    expect(scope.loginError).toBe('error msg');
  });

  it('should show error when username is missing', function () {
    scope.username = undefined;
    scope.doLogin();
    expect(messageService.get).toHaveBeenCalledWith('error.login.username');
  });

  it('should show error when username is only whitespaces', function () {
    scope.username = "   ";
    scope.doLogin();
    expect(messageService.get).toHaveBeenCalledWith('error.login.username');
  })

  it('should show error when password is missing', function () {
    scope.username = "someUser";
    scope.password = undefined;
    scope.doLogin();
    expect(messageService.get).toHaveBeenCalledWith('error.login.password');
  });

  it('should clear password on failed login attempt', function () {
    scope.username = "john";
    scope.password = "john-password";

    spyOn(messageService, 'populate');
    httpBackend.when('POST', '/j_spring_security_check').respond(403, {'error': 'error msg'});

    scope.doLogin();
    httpBackend.flush();

    expect(scope.password).toBe(undefined);
  });

  it('should clear password on successful login attempt', function () {
    scope.username = "john";
    scope.password = "john-password";
    scope.loginConfig = {modalShown: false, preventReload: true};

    spyOn(messageService, 'populate');
    httpBackend.when('POST', '/j_spring_security_check').respond(200,
      {authenticated: true, name: "john", rights: [
        {right: "MANAGE_USER", type: "ADMIN"}
      ]});

    scope.doLogin();
    httpBackend.flush();

    expect(scope.password).toBe(undefined);
  });
});