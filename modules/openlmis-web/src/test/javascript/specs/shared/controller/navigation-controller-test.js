/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */


describe("NavigationController", function () {

  var scope, ctrl, $httpBackend, $location, window, rights;
  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, $controller, _localStorageService_, _$httpBackend_, _$location_) {
    $httpBackend = _$httpBackend_;
    $location = _$location_;
    window = {};
    scope = $rootScope.$new();
    localStorageService = _localStorageService_;
    rights = [{name:'MANAGE_FACILITY',type:'ADMIN'},{name:'UPLOADS', type: 'REPORTING'}];
    spyOn(localStorageService, 'get').andReturn(JSON.stringify(rights));
    ctrl = $controller(NavigationController, {$scope: scope, localStorageService: localStorageService, $window: window});
  }));

  it('should check permission', function () {
    expect(true).toEqual(scope.hasPermission("MANAGE_FACILITY"));
    expect(false).toEqual(scope.hasPermission("CREATE_REQUISITION"));
  });

  it('should check reporting permission', function(){
    expect(scope.hasReportingPermission()).toBeTruthy();
  });

  it('should set user rights into scope', function () {
    expect(scope.rights).toEqual(JSON.stringify(rights));
  });

  describe("go online", function () {
    it("should take user to root if currently on offline home page and network is connected", function () {
      $httpBackend.expectGET('/settings/LOGIN_SUCCESS_DEFAULT_LANDING_PAGE.json').respond(200, { settings: { value: '/public/pages/dashboard/index.html' } });
      $httpBackend.expectGET("/locales.json").respond(200, {locales: ['en', 'pt']});
      spyOn($location, 'absUrl').andReturn("/public/pages/offline.html");
      spyOn($location, 'path');

      scope.goOnline();

      $httpBackend.flush();
      expect(window.location).toEqual("/");
      expect(scope.showNetworkError).toBeFalsy();
    });

    it("should take user to online version of app if network is connected", function () {
      $httpBackend.expectGET('/settings/LOGIN_SUCCESS_DEFAULT_LANDING_PAGE.json').respond(200, { settings: { value: '/public/pages/dashboard/index.html' } });
      $httpBackend.expectGET("/locales.json").respond(200, {locales: ['en', 'pt']});
      spyOn($location, 'absUrl').andReturn("/page/offline.html#/list");
      spyOn($location, 'path');

      scope.goOnline();

      $httpBackend.flush();
      expect(window.location).toEqual("/page/index.html#/manage");
      expect(scope.showNetworkError).toBeFalsy();
    });

    it("should set offline flag and not change URI if network is disconnected", function () {
      $httpBackend.expectGET('/settings/LOGIN_SUCCESS_DEFAULT_LANDING_PAGE.json').respond(200, { settings: { value: '/public/pages/dashboard/index.html' } });
      $httpBackend.expectGET("/locales.json").respond(200, {locales: undefined});
      window = {location: "/pages/test"};

      scope.goOnline();

      $httpBackend.flush();
      expect(window.location).toEqual("/pages/test");
      expect(scope.showNetworkError).toBeTruthy();
    });
  });
});
