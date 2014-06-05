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
    scope.$parent = {"$parent": {"associate": function () {
    }, "showSlider": false}};

    spyOn(scope.$parent.$parent, "associate");
    var facility = {code: "F10", name: "Village Dispensary"};

    scope.associate(facility);

    expect(scope.$parent.$parent.associate).toHaveBeenCalledWith(facility);
    expect(scope.$parent.$parent.showSlider).toBeTruthy();
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

    $httpBackend.when('GET', '/filter-facilities.json?searchParam=Fac').respond(response);
    scope.showFacilitySearchResults();
    $httpBackend.flush();

    expect(scope.facilityList).toEqual([facility1, facility2]);
    expect(scope.message).toEqual(undefined);
    expect(scope.facilityResultCount).toEqual(2);
  });

  it('should set search and filtered result facilities in scope', function () {
    scope.facilitySearchParam = "Fac";
    scope.type = {"id": 2};
    scope.zone = {"id": 6};

    var facility1 = {code: "F10", name: "Village Dispensary1"};
    var facility2 = {code: "F11", name: "Village Dispensary2"};
    var response = {"facilityList": [facility1, facility2]};

    $httpBackend.when('GET', '/filter-facilities.json?facilityTypeId=2&geoZoneId=6&searchParam=Fac').respond(response);
    scope.showFacilitySearchResults();
    $httpBackend.flush();

    expect(scope.facilityList).toEqual([facility1, facility2]);
    expect(scope.message).toEqual(undefined);
    expect(scope.facilityResultCount).toEqual(2);
  });

  it('should set message if too many searched facilities found', function () {
    scope.facilitySearchParam = "Fac";
    var response = {"facilityList": [], "message": "Too may results found"};

    $httpBackend.when('GET', '/filter-facilities.json?searchParam=Fac').respond(response);
    scope.showFacilitySearchResults();
    $httpBackend.flush();

    expect(scope.facilityList).toEqual([]);
    expect(scope.message).toEqual("Too may results found");
    expect(scope.facilityResultCount).toEqual(0);
  });

  it('should show filter modal with facilityTypes', function () {
    scope.filterModal = false;
    $httpBackend.when('GET', '/facility-types.json').respond({"facilityTypeList": facilityTypeList});
    scope.showFilterModal();
    $httpBackend.flush();

    expect(scope.filterModal).toBeTruthy();
    expect(scope.facilityTypes).toEqual(facilityTypeList);
  });

  it('should search geo zone and set results in scope', function () {
    scope.geoZoneSearchParam = "moz";
    var response = {"geoZones": [
      {"name": "moz", "level": {"name": "district"}}
    ]};

    $httpBackend.when('GET', '/filtered-geographicZones.json?searchParam=moz').respond(response);
    scope.searchGeoZone();
    $httpBackend.flush();

    expect(scope.geoZoneList).toEqual([
      {"name": "moz", "level": {"name": "district"}}
    ]);
    expect(scope.levels).toEqual(["district"]);
    expect(scope.showResults).toEqual(true);
  });

  it('should set message if too many searched geo zones found', function () {
    scope.geoZoneSearchParam = "moz";
    var response = {"geoZones": [], "message": "Too may results found"};

    $httpBackend.when('GET', '/filtered-geographicZones.json?searchParam=moz').respond(response);
    scope.searchGeoZone();
    $httpBackend.flush();

    expect(scope.geoZoneList).toEqual([]);
    expect(scope.manyGeoZoneMessage).toEqual("Too may results found");
    expect(scope.geoZonesResultCount).toEqual(0);
  });

  it('should not search geo zone if query undefined', function () {
    spyOn($httpBackend, 'expectGET');
    scope.geoZoneSearchParam = undefined;

    scope.searchGeoZone();

    expect($httpBackend.expectGET).not.toHaveBeenCalledWith('/filtered-geographicZones.json?searchParam=undefined');
  });

  it('should set geo zone', function () {
    spyOn(scope, 'clearGeoZoneSearch');
    var geoZone = {"name": "moz"};
    scope.setGeoZone(geoZone);

    expect(scope.selectedGeoZone).toEqual(geoZone);
    expect(scope.clearGeoZoneSearch).toHaveBeenCalled();
  });

  it('should clear geo zone search', function () {
    scope.showResults = true;
    scope.geoZoneList = [
      {"name": "moz"}
    ];
    scope.geoZoneQuery = "moz";
    scope.geoZoneSearchParam = "moz";

    scope.clearGeoZoneSearch();

    expect(scope.showResults).toBeFalsy();
    expect(scope.geoZoneList).toEqual([]);
    expect(scope.geoZoneQuery).toBeUndefined();
    expect(scope.geoZoneSearchParam).toBeUndefined();
  });

  it('should set facility type', function () {
    scope.facilityType = {"name": "warehouse"};

    scope.setFacilityType();

    expect(scope.selectedFacilityType).toEqual({"name": "warehouse"});
    expect(scope.facilityType).toBeUndefined();
  });

  it('should set filters', function () {
    spyOn(scope, 'showFacilitySearchResults');
    scope.selectedFacilityType = {"name": "warehouse"};
    scope.selectedGeoZone = {"name": "moz"};

    scope.setFilters();

    expect(scope.zone).toEqual(scope.selectedGeoZone);
    expect(scope.type).toEqual(scope.selectedFacilityType);
    expect(scope.filterModal).toBeFalsy();
    expect(scope.showFacilitySearchResults).toHaveBeenCalled();
  });

  it('should cancel filters', function () {
    scope.selectedFacilityType = {"name": "warehouse"};
    scope.selectedGeoZone = {"name": "moz"};
    scope.zone = {"name": "geo zone"};

    scope.cancelFilters();

    expect(scope.selectedGeoZone).toEqual({"name": "geo zone"});
    expect(scope.selectedFacilityType).toEqual({});
    expect(scope.filterModal).toBeFalsy();
  });

});
