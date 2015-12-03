/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe("Multiple Facility Search Filter Controller", function () {

  var scope, parentScope, httpBackend, ctrl;
  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
    scope = $rootScope.$new();
    parentScope = $rootScope.$new();
    httpBackend = _$httpBackend_;
    ctrl = $controller('MultipleFacilitySearchFilterController', {$scope: scope});
  }));

  it('should set searched facilities in scope', function () {
    scope.$parent = {"$parent": parentScope};
    scope.multipleFacilitiesSearchParam = "Fac";
    var facility1 = {code: "F10", name: "Village Dispensary1"};
    var facility2 = {code: "F11", name: "Village Dispensary2"};
    var response = {"facilityList": [facility1, facility2]};
    scope.extraMultipleParams = {"virtualFacility": null, "enabled": true };

    httpBackend.when('GET', '/filter-facilities.json?enabled=true&searchParam=Fac').respond(response);
    scope.showFacilitySearchResults();
    httpBackend.flush();

    expect(scope.multipleFacilities).toEqual([facility1, facility2]);
    expect(scope.multipleFacilitiesMessage).toEqual(undefined);
    expect(scope.multipleFacilitiesResultCount).toEqual(2);
    expect(scope.resultCount).toEqual(2);
  });

  it('should set searched facilities in scope and count as zero if search results empty', function () {
    scope.$parent = {"$parent": parentScope};
    scope.multipleFacilitiesSearchParam = "Fac";
    var response = {"facilityList": undefined};
    scope.extraMultipleParams = {"virtualFacility": null, "enabled": true };

    httpBackend.when('GET', '/filter-facilities.json?enabled=true&searchParam=Fac').respond(response);
    scope.showFacilitySearchResults();
    httpBackend.flush();

    expect(scope.multipleFacilities).toBeUndefined();
    expect(scope.multipleFacilitiesMessage).toEqual(undefined);
    expect(scope.multipleFacilitiesResultCount).toEqual(0);
    expect(scope.resultCount).toEqual(0);
  });

  it('should not search results if query is undefined', function () {
    spyOn(httpBackend, 'expectGET');
    scope.multipleFacilitiesSearchParam = undefined;

    scope.showFacilitySearchResults();

    expect(httpBackend.expectGET).not.toHaveBeenCalledWith('/filter-facilities.json?searchParam=undefined');
  });

  it('should associate facilities', function () {

    var facility = {"selected": false, "name": "fac1"};

    scope.addToFacilityList(facility);

    expect(scope.tempFacilities[0]).toEqual(facility);
    expect(facility.selected).toBeTruthy();
    expect(scope.disableAddFacility).toBeFalsy();
  });

  it('should filter duplicate facilities on associate', function () {

    var facility = {"selected": true, "name": "fac1", "id": 1};
    scope.tempFacilities = [facility];
    scope.disableAddFacility = true;

    scope.addToFacilityList(facility);

    expect(scope.tempFacilities).toEqual([]);
    expect(facility.selected).toBeFalsy();
    expect(scope.disableAddFacility).toBeTruthy();

  });

  it('should clear facility search on successful addition of members', function () {
    scope.$parent = {"$parent": {"addMembers": function () {
      return true;
    }}};

    var facility = {"selected": true, "name": "fac1", "id": 1};
    scope.tempFacilities = [facility];

    var spyAddMembers = spyOn(scope.$parent.$parent, "addMembers").andReturn(true);
    var spyClearFacilitySearch = spyOn(scope, "clearMultiSelectFacilitySearch");
    var spyClearVisibleFilters = spyOn(scope, "clearVisibleFilters");
    var spyBroadcast = spyOn(scope, "$broadcast");

    scope.addMembers();

    expect(spyAddMembers).toHaveBeenCalledWith(scope.tempFacilities);
    expect(spyClearFacilitySearch).toHaveBeenCalled();
    expect(spyClearVisibleFilters).toHaveBeenCalled();
    expect(spyBroadcast).toHaveBeenCalledWith('multiSelectSearchCleared');
  });

  it('should not clear facility search on unsuccessful addition of members ', function () {
    scope.$parent = {"$parent": {"addMembers": function () {
    }}};

    var facility = {"selected": true, "name": "fac1", "id": 1};
    scope.tempFacilities = [facility];

    var spyAddMembers = spyOn(scope.$parent.$parent, "addMembers").andReturn(false);
    var spyClearFacilitySearch = spyOn(scope, "clearMultiSelectFacilitySearch");
    var spyClearVisibleFilters = spyOn(scope, "clearVisibleFilters");
    var spyBroadcast = spyOn(scope, "$broadcast");

    scope.addMembers();

    expect(spyAddMembers).toHaveBeenCalledWith(scope.tempFacilities);
    expect(spyClearFacilitySearch).not.toHaveBeenCalled();
    expect(spyClearVisibleFilters).not.toHaveBeenCalled();
    expect(spyBroadcast).not.toHaveBeenCalledWith('multiSelectSearchCleared');
  });

  it('should not search results if query is undefined', function () {
    spyOn(httpBackend, 'expectGET');
    scope.multipleFacilitiesSearchParam = undefined;

    scope.showFacilitySearchResults();

    expect(httpBackend.expectGET).not.toHaveBeenCalledWith('/filter-facilities.json?searchParam=undefined');
    expect(scope.tempFacilities).toEqual([]);
  });

  it('should clear facility search results', function () {
    scope.multipleFacilitiesSearchParam = "searchParam";
    scope.multipleFacilities = [];
    scope.multipleFacilitiesResultCount = 3;
    var element = angular.element('<div id="search" class="search-list"></div>');
    spyOn(angular, "element").andReturn(element);

    scope.clearMultiSelectFacilitySearch();
    element.trigger('slideUp');

    expect(scope.multipleFacilitiesResultCount).toBeUndefined();
    expect(scope.multipleFacilities).toBeUndefined();
    expect(scope.multipleFacilitiesSearchParam).toBeUndefined();
    expect(scope.disableAddFacility).toBeTruthy();
    expect(scope.tempFacilities).toEqual([]);
  });


  it('should clear visible filters', function () {
    scope.type = {name: "type"};
    scope.zone = {name: "zone"};

    scope.clearVisibleFilters();

    expect(scope.type).toEqual({});
    expect(scope.zone).toEqual({});
  });

  it('should clear facility searched results', function () {
    scope.multipleFacilitiesSearchParam = "F1";
    scope.multipleFacilities = [
      {name: "F1"},
      {name: "F2"}
    ];
    scope.multipleFacilitiesResultCount = 34;
    scope.disableAddFacility = false;
    scope.tempFacilities = [
      {name: "F1"}
    ];

    scope.clearMultiSelectFacilitySearch();

    expect(scope.multipleFacilitiesSearchParam).toBeUndefined();
    expect(scope.multipleFacilities).toBeUndefined();
    expect(scope.multipleFacilitiesResultCount).toBeUndefined();
    expect(scope.disableAddFacility).toBeTruthy();
    expect(scope.tempFacilities).toEqual([]);
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
});