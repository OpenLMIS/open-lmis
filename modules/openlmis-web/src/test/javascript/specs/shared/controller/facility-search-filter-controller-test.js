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

  var scope, $httpBackend, ctrl, facilityTypeList;
  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
    scope = $rootScope.$new();

    facilityTypeList = [
      {"id": 1, "name": "district1"},
      {"id": 2, "name": "district2"}
    ];

    $httpBackend = _$httpBackend_;
    ctrl = $controller('FacilitySearchFilterController', {$scope: scope});
  }));

  it("should associate a facility", function () {
    scope.$parent = {associate: function () {
    }};
    spyOn(scope.$parent, "associate");
    spyOn(scope, "clearFacilitySearch");
    spyOn(scope, "clearVisibleFilters");
    spyOn(scope, "$broadcast");
    var facility = {code: "F10", name: "Village Dispensary"};

    scope.associate(facility);

    expect(scope.$parent.associate).toHaveBeenCalledWith(facility);
    expect(scope.clearFacilitySearch).toHaveBeenCalled();
    expect(scope.clearVisibleFilters).toHaveBeenCalled();
    expect(scope.$broadcast).toHaveBeenCalledWith("singleSelectSearchCleared");
  });

  it('should clear visible filters', function () {
    scope.type = {name: "type"};
    scope.zone = {name: "zone"};

    scope.clearVisibleFilters();

    expect(scope.type).toEqual({});
    expect(scope.zone).toEqual({});
  });

  it('should clear facility searched results', function () {
    scope.facilitySearchParam = "F1";
    scope.facilityList = [
      {name: "F1"},
      {name: "F2"}
    ];
    scope.facilityResultCount = 34;

    scope.clearFacilitySearch();

    expect(scope.facilitySearchParam).toBeUndefined();
    expect(scope.facilityList).toBeUndefined();
    expect(scope.facilityResultCount).toBeUndefined();
  });

  it('should trigger fetching facility search results on pressing enter key', function () {
    var event = {keyCode: 13};
    spyOn(scope, 'showFacilitySearchResults');

    scope.triggerSearch(event);

    expect(scope.showFacilitySearchResults).toHaveBeenCalled();
  });

  it('should not trigger fetching facility search results on any key except enter key', function () {
    var event = {keyCode: 213};
    spyOn(scope, 'showFacilitySearchResults');

    scope.triggerSearch(event);

    expect(scope.showFacilitySearchResults).not.toHaveBeenCalled();
  });

  it('should not search results if query is undefined', function () {
    spyOn($httpBackend, 'expectGET');
    scope.facilitySearchParam = undefined;

    scope.showFacilitySearchResults();

    expect($httpBackend.expectGET).not.toHaveBeenCalledWith('/filter-facilities.json?searchParam=undefined');
  });

  it('should set search result facilities in scope', function () {
    scope.facilitySearchParam = "Fac";
    var facility1 = {code: "F10", name: "Village Dispensary1"};
    var facility2 = {code: "F11", name: "Village Dispensary2"};
    var response = {"facilityList": [facility1, facility2]};
    scope.extraParams = {"virtualFacility": null, "enabled": true };

    $httpBackend.when('GET', '/filter-facilities.json?enabled=true&searchParam=Fac').respond(response);
    scope.showFacilitySearchResults();
    $httpBackend.flush();

    expect(scope.facilityList).toEqual([facility1, facility2]);
    expect(scope.message).toEqual(undefined);
    expect(scope.facilityResultCount).toEqual(2);
    expect(scope.resultCount).toEqual(2);
  });

  it('should set search and filtered result facilities in scope', function () {
    scope.facilitySearchParam = "Fac";
    scope.type = {"id": 2};
    scope.zone = {"id": 6};

    var facility1 = {code: "F10", name: "Village Dispensary1"};
    var facility2 = {code: "F11", name: "Village Dispensary2"};
    var response = {"facilityList": [facility1, facility2]};
    scope.extraParams = {"virtualFacility": true, "enabled": null };

    $httpBackend.when('GET', '/filter-facilities.json?facilityTypeId=2&geoZoneId=6&searchParam=Fac&virtualFacility=true').respond(response);
    scope.showFacilitySearchResults();
    $httpBackend.flush();

    expect(scope.facilityList).toEqual([facility1, facility2]);
    expect(scope.message).toEqual(undefined);
    expect(scope.facilityResultCount).toEqual(2);
  });

  it('should set message if too many searched facilities found', function () {
    scope.facilitySearchParam = "Fac";
    var response = {"facilityList": [], "message": "Too may results found"};
    scope.extraParams = {"virtualFacility": null, "enabled": true };

    $httpBackend.when('GET', '/filter-facilities.json?enabled=true&searchParam=Fac').respond(response);
    scope.showFacilitySearchResults();
    $httpBackend.flush();

    expect(scope.facilityList).toEqual([]);
    expect(scope.message).toEqual("Too may results found");
    expect(scope.facilityResultCount).toEqual(0);
  });
});
