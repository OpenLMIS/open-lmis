/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe('View Report Controller', function () {
  var scope, ctrl, template;
  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, $controller) {
    scope = $rootScope.$new();
    template = {id: 1, name: "test report", parameters: [
      {name: "Parameter1", defaultValue: 12},
      {name: "Parameter2", defaultValue: "param2"}
    ]};
    ctrl = $controller('ViewReportController', {$scope: scope, template: template});
  }));


  it('should set template in scope', function () {
    expect(scope.template).toEqual(template);
  });

  it('should set template parameters in scope', function () {
    expect(scope.parameters).toEqual(template.parameters);
  });

  it('should set default values of template parameters in a map and params string in scope', function () {
    expect(scope.parameterMap).toEqual({"Parameter1": 12, "Parameter2": "param2"});
  });

  it('should set params string in scope', function () {
    expect(scope.params).toEqual("Parameter1=12&&Parameter2=param2&&");
  });

  it('should check if parameter data type is invalid or not', function () {
    expect(scope.isInvalid("java.sql.Timestamp")).toBeTruthy();
  });
});

describe('View Report Resolve', function () {
  var $httpBackend, deferredObject, $q, $timeout, ctrl, route;

  beforeEach(module('openlmis'));
  beforeEach(inject(function (_$httpBackend_, $controller, _$timeout_) {
    $httpBackend = _$httpBackend_;
    deferredObject = {promise: {id: 1}, resolve: function () {
    }};
    spyOn(deferredObject, 'resolve');
    $q = {defer: function () {
      return deferredObject
    }};
    $timeout = _$timeout_;
    ctrl = $controller;
  }));

  it('should fetch template with parameters', function () {
    var $route = {current: {params: {id: 1}}};
    var response = {template: {}};
    $httpBackend.expect('GET', '/reports/1.json').respond(response);
    ctrl(ViewReportController.resolve.template, {$q: $q, $route: $route});
    $timeout.flush();
    $httpBackend.flush();
    expect(deferredObject.resolve).toHaveBeenCalledWith(response.template);
  });
});