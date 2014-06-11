/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe("Facility Sub Filters Controller", function () {

  var scope, $httpBackend, ctrl, facilityTypeList;
  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
    scope = $rootScope.$new();

    facilityTypeList = [
      {"id": 1, "name": "district1"},
      {"id": 2, "name": "district2"}
    ];

    $httpBackend = _$httpBackend_;
    ctrl = $controller('FacilitySubFiltersController', {$scope: scope});
  }));

  it('should cancel filters', function () {
    scope.$parent={$parent:{"zone":{"name": "geo zone"},"type":{}}};
    scope.selectedFacilityType = {"name": "warehouse"};
    scope.selectedGeoZone = {"name": "moz"};

    scope.cancelFilters();

    expect(scope.selectedFacilityType).toEqual({});
    expect(scope.selectedGeoZone).toEqual({"name": "geo zone"});
    expect(scope.filterModal).toBeFalsy();
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


  it('should show filter modal with facilityTypes', function () {
    scope.filterModal = false;
    $httpBackend.when('GET', '/facility-types.json').respond({"facilityTypeList": facilityTypeList});
    scope.showFilterModal();
    $httpBackend.flush();

    expect(scope.filterModal).toBeTruthy();
    expect(scope.facilityTypes).toEqual(facilityTypeList);
  });


  it('should set filters', function () {
    scope.$parent = {"$parent": {"zone": {}, "type": {}, "showFacilitySearchResults" : function(){} }};
    spyOn(scope.$parent.$parent,"showFacilitySearchResults");
    scope.selectedFacilityType = {"name": "warehouse"};
    scope.selectedGeoZone = {"name": "moz"};

    scope.setFilters();

    expect(scope.$parent.$parent.zone).toEqual(scope.selectedGeoZone);
    expect(scope.$parent.$parent.type).toEqual(scope.selectedFacilityType);
    expect(scope.filterModal).toBeFalsy();
    expect(scope.$parent.$parent.showFacilitySearchResults).toHaveBeenCalled();
  });
});