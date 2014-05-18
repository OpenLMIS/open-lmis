/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe("Facility Search Filter Controller", function () {

  var scope, $httpBackend, ctrl, rootScope;
  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
    rootScope = $rootScope;
    scope = $rootScope.$new();
    scope.$parent.$parent = scope;
    $httpBackend = _$httpBackend_;
    ctrl = $controller('FacilitySearchFilterController', {$scope:scope});
  }));

  it("should associate a facility", function() {
    scope.supervisoryNode = undefined;
    scope.sliderState = undefined;
    var facility = {code: "F10", name: "Village Dispensary"};

    scope.associateFacility(facility);

    expect(scope.supervisoryNode.facility).toBe(facility);
    expect(scope.sliderState).toBeTruthy();
  });

  it('should not search results if query is undefined', function () {
    spyOn($httpBackend,"expectGET");
    scope.query = undefined;

    scope.facilitySearchResults();

    expect($httpBackend.expectGET).not.toHaveBeenCalledWith('/facilities.json?searchParam=' + scope.query);
  });


  it('should get all searched facilities in scope', function () {
    scope.query = "Fac";
    var facility1 = {"id": 1, "code": "F1", "name": "Dispensary 1"};
    var facility2 = {"id": 2, "code": "F2", "name": "Facility 2"};
    var facility3 = {"id": 3, "code": "F3", "name": "Facility 3"};
    var response = {"facilityList":[facility2, facility3], "message": ""};

    $httpBackend.when('GET', '/facilities.json?searchParam=' + scope.query).respond(response);
    scope.facilitySearchResults();
    $httpBackend.flush();

    expect(scope.facilityList).toEqual([facility2, facility3]);
    expect(scope.message).toEqual("");
    expect(scope.resultCount).toEqual(2);
  });

  it('should not get resultCount as Zero if no searched facilities found', function () {
    scope.query = "Fac";
    var response = {"facilityList":[], "message": "Too may results found"};

    $httpBackend.when('GET', '/facilities.json?searchParam=' + scope.query).respond(response);
    scope.facilitySearchResults();
    $httpBackend.flush();

    expect(scope.facilityList).toEqual([]);
    expect(scope.message).toEqual("Too may results found");
    expect(scope.resultCount).toEqual(0);
  });
});
