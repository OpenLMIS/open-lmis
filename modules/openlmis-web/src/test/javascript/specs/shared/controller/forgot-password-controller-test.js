/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("ForgotPasswordController", function () {

  var controller, messageService, $httpBackend, scope;
  beforeEach(module('openlmis'));
  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, _messageService_) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    messageService = _messageService_;
    controller = $controller(ForgotPasswordController, {$scope: scope, messageService: messageService});
    spyOn(messageService, 'get');
  }));

  it("Should give error message if User does not enter both username and email", function () {
    scope.username = "";
    scope.email = "";
    scope.sendForgotPasswordEmail();
    expect(messageService.get).toHaveBeenCalledWith('enter.emailInfo')
  });

  it("Should send the forgot password email when user enters either username or email", function () {
    scope.user = {"userName": "Admin123"};
    scope.email = "";
    var user = {username: "Admin123"};
    $httpBackend.expectPOST('/forgot-password.json').respond(200, {"user": user});
    scope.sendForgotPasswordEmail();
    expect(scope.submitDisabled).toEqual(true);
    expect(messageService.get).toHaveBeenCalledWith('sending.label')
  });
});
