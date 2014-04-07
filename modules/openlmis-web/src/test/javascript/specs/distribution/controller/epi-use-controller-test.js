/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('EPI Use row controller', function () {

  var scope, controller;

  beforeEach(inject(function ($rootScope, $controller) {
    scope = $rootScope.$new();
    controller = $controller(EpiUseRowController, {$scope: scope});
  }));

  it("should compute total of 'stockAtFirstOfMonth' and 'received' fields", function () {
    scope.lineItem = { stockAtFirstOfMonth: {value: 50}, received: {value: 75} };

    var total = scope.getTotal();

    expect(total).toEqual(125);
  });

  it("should ignore not recorded 'stockAtFirstOfMonth' or 'received' fields in total calculation", function () {
    scope.lineItem = {stockAtFirstOfMonth: {value: 50}, received: {notRecorded: true} };

    var total = scope.getTotal();

    expect(total).toEqual(50);
  });

  it("should return total as zero if reading object is not available", function () {
    scope.lineItem = { };

    var total = scope.getTotal();

    expect(total).toEqual(0);
  });

  it("should return total as zero if group reading object is not available", function () {

    var total = scope.getTotal();

    expect(total).toEqual(0);
  });
});

describe('Epi use controller', function () {

  var scope, controller, distributionService;

  beforeEach(module('distribution'));
  beforeEach(inject(function ($rootScope, $controller, _distributionService_, $routeParams) {
    scope = $rootScope.$new();
    $routeParams.facility = 3;
    distributionService = _distributionService_;
    controller = $controller(EPIUseController, {$scope: scope});
  }));


  it('should apply NR to all epi Use fields', function () {
    spyOn(distributionService, 'applyNR');

    var epiUse = jasmine.createSpyObj('Epi Use', ['setNotRecorded']);
    scope.distribution = {id: 1, facilityDistributions: {3: {epiUse: epiUse}}};

    scope.applyNRAll();

    expect(distributionService.applyNR).toHaveBeenCalled();

    distributionService.applyNR.calls[0].args[0]();

    expect(epiUse.setNotRecorded).toHaveBeenCalled();
  });

});