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

  var scope, httpBackend, ctrl;
  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
    scope = $rootScope.$new();
    httpBackend = _$httpBackend_;
    ctrl = $controller('MultipleFacilitySearchFilterController', {$scope: scope});
  }));

  it('should associate facilities', function () {

    var facility = {"selected": false, "name": "fac1"};

    scope.associate(facility);

    expect(scope.tempFacilities[0]).toEqual(facility);
    expect(facility.selected).toBeTruthy();
    expect(scope.disableAddFacility).toBeFalsy();
  });

  it('should filter duplicate facilities on associate', function () {

    var facility = {"selected": true, "name": "fac1", "id": 1};
    scope.tempFacilities = [facility];
    scope.disableAddFacility = true;

    scope.associate(facility);

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
    var spyClearFacilitySearch = spyOn(scope, "clearFacilitySearch");

    scope.addMembers();

    expect(spyAddMembers).toHaveBeenCalledWith(scope.tempFacilities);
    expect(spyClearFacilitySearch).toHaveBeenCalled();
  });

  it('should not search results if query is undefined', function () {
    spyOn(httpBackend, 'expectGET');
    scope.facilitySearchParam = undefined;

    scope.showFacilitySearchResults();

    expect(httpBackend.expectGET).not.toHaveBeenCalledWith('/filter-facilities.json?searchParam=undefined');
    expect(scope.tempFacilities).toEqual([]);
  });

  it('should clear facility search results', function () {
    scope.facilitySearchParam = "searchParam";
    scope.facilityList = [];
    scope.facilityResultCount = 3;
    var element = angular.element('<div id="search" class="search-list"></div>');
    var spy = spyOn(angular, "element").andReturn(element);

    scope.clearFacilitySearch();
    element.trigger('slideUp');

    expect(scope.facilityResultCount).toBeUndefined();
    expect(scope.facilityList).toBeUndefined();
    expect(scope.facilitySearchParam).toBeUndefined();
    expect(scope.disableAddFacility).toBeTruthy();
    expect(scope.tempFacilities).toEqual([]);
  });
});