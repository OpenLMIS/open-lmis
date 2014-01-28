/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("LoginController", function () {

  beforeEach(module('openlmis'));

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
    httpBackend.when('POST', '/j_spring_security_check').respond(401, {'error': 'error msg'});

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
    httpBackend.when('POST', '/j_spring_security_check').respond(401, {'error': 'error msg'});

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