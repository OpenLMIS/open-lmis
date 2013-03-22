/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe("ResetPasswordController", function () {


  beforeEach(module('openlmis.services'));
  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $route) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    route = $route;
    route.current = {params : {token : "token"}};
    controller = $controller(ResetPasswordController, {$scope:scope, tokenValid:true, route:route});
  }));

  it("Should give error message if password is less than 8 characters", function () {
    scope.password1 = "1234567";
    scope.resetPassword();
    expect(scope.error).toEqual("Password is invalid. Password must be between 8 to 16 characters, should not contain spaces and contain at least 1 number.");
  });

  it("Should give error message if password does not contain a digit", function () {
    scope.password1 = "abcdefghi";
    scope.resetPassword();
    expect(scope.error).toEqual("Password is invalid. Password must be between 8 to 16 characters, should not contain spaces and contain at least 1 number.");
  });

  it("Should give error message if password contains spaces", function () {
    scope.password1 = "abcd efghi4  ";
    scope.resetPassword();
    expect(scope.error).toEqual("Password is invalid. Password must be between 8 to 16 characters, should not contain spaces and contain at least 1 number.");
  });

  it("Should give error message passwords do not match", function () {
    scope.password1 = "abcdefghi4";
    scope.password2 = "abcdefghi5";
    scope.resetPassword();
    expect(scope.error).toEqual("Passwords do not match");
  });

  it("Should update password if it is valid and both passwords match", function () {
    $httpBackend.expectPUT('/user/resetPassword/token.json').respond(200);
    scope.password1 = "abcdefghi4";
    scope.password2 = "abcdefghi4";
    scope.resetPassword();
    $httpBackend.flush();
  });
});
