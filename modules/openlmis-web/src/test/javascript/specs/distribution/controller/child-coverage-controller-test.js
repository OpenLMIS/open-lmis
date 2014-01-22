/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe('Child Coverage Controller', function () {

  var scope, distributionService, routeParams, childCoverage;

  beforeEach(module('distribution'));
  beforeEach(inject(function ($controller, $rootScope, _distributionService_) {
    scope = $rootScope.$new();
    distributionService = _distributionService_;
    childCoverage = {lineItems: [
      {vaccination: 'BCG'},
      {vaccination: 'penta10 dose'}
    ]};
    distributionService.distribution = {facilityDistributions: {1: {childCoverage: childCoverage}, 2: {}}};
    routeParams = {facility: 1};
    $controller(ChildCoverageController, {$scope: scope, $routeParams: routeParams, distributionService: distributionService});
  }));

  it('should set distribution in scope', function () {
    expect(scope.distribution).toEqual(distributionService.distribution);
  });

  it('should set facility id in scope from route params', function () {
    expect(scope.selectedFacilityId).toEqual(routeParams.facility);
  });

  it('should set child coverage in scope', function () {
    expect(scope.childCoverage).toEqual(childCoverage);
  });

  it('should return false if vaccination present in show cell list', function () {
    var isVisible = scope.hideCell(childCoverage.lineItems[0].vaccination);
    expect(isVisible).toBeFalsy();
  });

  it('should return true if vaccination is not present in show cell list', function () {
    var isVisible = scope.hideCell(childCoverage.lineItems[1].vaccination);
    expect(isVisible).toBeTruthy();
  });

});