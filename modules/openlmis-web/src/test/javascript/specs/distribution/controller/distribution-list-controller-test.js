/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('DistributionListController', function () {

  var scope, location;
  var sharedDistribution;

  beforeEach(module('distribution'));
  beforeEach(module('IndexedDB'));

  beforeEach(inject(function ($rootScope, $location, $controller) {
    scope = $rootScope.$new();
    location = $location;
    sharedDistribution = {update: function () {
    }};

    spyOn(sharedDistribution, 'update');

    $controller(DistributionListController, {$scope: scope, $location: location, SharedDistributions: sharedDistribution })
  }));

  xit('should set distributions in scope', function() {
    expect(scope.sharedDistributions).toBe(sharedDistribution);
  });

  xit('should refresh shared distributions on load', function() {
    expect(sharedDistribution.update).toHaveBeenCalled();
  });
});