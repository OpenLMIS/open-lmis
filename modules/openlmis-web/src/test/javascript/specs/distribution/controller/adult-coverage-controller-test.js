/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe('Adult Coverage Controller', function () {

  var scope, distributionService, routeParams, adultCoverageJSON,
    adultCoverageLineItem1, adultCoverageLineItem2, adultCoverageLineItem3;

  beforeEach(module('distribution'));
  beforeEach(inject(function ($controller, $rootScope, _distributionService_) {

    scope = $rootScope.$new();
    distributionService = _distributionService_;

    adultCoverageLineItem1 = {"id": 5, "facilityVisitId": 3, "demographicGroup": "Pregnant Women"};
    adultCoverageLineItem2 = {"id": 26, "facilityVisitId": 3, "demographicGroup": "Students not MIF"};
    adultCoverageLineItem3 = {"id": 27, "facilityVisitId": 3, "demographicGroup": "Workers not MIF"};

    adultCoverageJSON = {facilityVisitId: 234,
      adultCoverageLineItems: [
        adultCoverageLineItem1,
        adultCoverageLineItem2,
        adultCoverageLineItem3
      ]};

    distributionService.distribution = {facilityDistributions: {1: {adultCoverage: adultCoverageJSON}, 2: {}}};
    routeParams = {facility: 1};
    $controller(AdultCoverageController, {$scope: scope, $routeParams: routeParams, distributionService: distributionService});
  }));

  it('should set distribution, facilityId and adultCoverageJSON in scope', function(){
    expect(scope.distribution).toEqual({facilityDistributions: {1: {adultCoverage: adultCoverageJSON}, 2: {}}});
    expect(scope.selectedFacilityId).toEqual(1);
    expect(scope.adultCoverage).toEqual(adultCoverageJSON);
  });

  it('should convert adult coverage line items to map', function() {
    expect(scope.adultCoverageTargetGroupMap).toBeDefined();
    expect(scope.adultCoverageTargetGroupMap['Pregnant Women']).toEqual(adultCoverageLineItem1);
    expect(scope.adultCoverageTargetGroupMap['Students not MIF']).toEqual(adultCoverageLineItem2);
    expect(scope.adultCoverageTargetGroupMap['Workers not MIF']).toEqual(adultCoverageLineItem3);
  });

  it('should applyNR to all readings', function () {
    scope.adultCoverage = new AdultCoverage(234, adultCoverageJSON);
    spyOn(distributionService, 'applyNR');
    spyOn(scope.adultCoverage, 'setNotRecorded');

    scope.applyNRAll();

    expect(distributionService.applyNR).toHaveBeenCalled();

    distributionService.applyNR.calls[0].args[0]();

    expect(scope.adultCoverage.setNotRecorded).toHaveBeenCalled();
  });

});