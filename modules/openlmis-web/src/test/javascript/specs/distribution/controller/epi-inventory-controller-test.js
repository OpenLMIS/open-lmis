/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe('EPI Inventory Controller', function () {
  var scope, routeParams, distributionService, distribution;

  beforeEach(module('distribution'))
  beforeEach(inject(function ($controller, $rootScope, _distributionService_) {
    scope = $rootScope.$new();
    routeParams = {facility: '4'};
    distributionService = _distributionService_;

    $controller(EPIInventoryController, {$scope: scope, $routeParams: routeParams});
  }));

  it('should set distribution in scope', function () {
    expect(scope.distribution).toEqual(distributionService.distribution);
  });

  it('should set facility id from route params', function () {
    expect(scope.selectedFacilityId).toEqual('4');
  });

  it('should apply NR to all epi inventory fields', function () {
    spyOn(distributionService, 'applyNR');

    var epiInventory = jasmine.createSpyObj('Epi Inventory', ['setNotRecorded']);
    scope.distribution = {id: 1, facilityDistributions: {4: {epiInventory: epiInventory}}};

    scope.applyNRAll();

    expect(distributionService.applyNR).toHaveBeenCalled();

    distributionService.applyNR.calls[0].args[0]();

    expect(epiInventory.setNotRecorded).toHaveBeenCalled();
  });

});