/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("ResetPasswordController", function () {

  var scope, $httpBackend, messageService, route, controller;

  beforeEach(module('openlmis'));
  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $route, _messageService_) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    messageService = _messageService_;
    route = $route;
    route.current = {params : {token : "token"}};
    spyOn(messageService, 'get');
    controller = $controller(ResetPasswordController, {$scope:scope, tokenValid:true, route:route});
  }));

  it("Should give error message if password is less than 8 characters", function () {
    scope.password1 = "1234567";
    scope.resetPassword();
    expect(messageService.get).toHaveBeenCalledWith('error.password.invalid');
  });

  it("Should give error message if password does not contain a digit", function () {
    scope.password1 = "abcdefghi";
    scope.resetPassword();
    expect(messageService.get).toHaveBeenCalledWith('error.password.invalid');
  });

  it("Should give error message if password contains spaces", function () {
    scope.password1 = "abcd efghi4  ";
    scope.resetPassword();
    expect(messageService.get).toHaveBeenCalledWith('error.password.invalid');
  });

  it("Should give error message passwords do not match", function () {
    scope.password1 = "abcdefghi4";
    scope.password2 = "abcdefghi5";
    scope.resetPassword();
    expect(messageService.get).toHaveBeenCalledWith('error.password.mismatch');
  });

  it("Should update password if it is valid and both passwords match", function () {
    $httpBackend.expectPUT('/user/resetPassword/token.json').respond(200);
    scope.password1 = "abcdefghi4";
    scope.password2 = "abcdefghi4";
    scope.resetPassword();
    $httpBackend.flush();
  });
});
