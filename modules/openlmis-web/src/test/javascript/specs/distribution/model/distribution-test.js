/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe('Distribution', function () {

  it('should create a distribution object filled with facility distributions', function () {
    spyOn(window, 'FacilityDistribution');
    var distributionJson = {facilityDistributions: {1: {name: 'facilityDistribution1'}, 2: {name: 'facilityDistribution2'}}};

    new Distribution(distributionJson);

    expect(window.FacilityDistribution.calls[0].args[0]).toEqual(distributionJson.facilityDistributions[1]);
    expect(window.FacilityDistribution.calls[1].args[0]).toEqual(distributionJson.facilityDistributions[2]);
  });

  it('should create a distribution object filled with a copy of all distribution data', function () {
    spyOn($, 'extend');
    spyOn(window, 'FacilityDistribution');
    var distributionJson = {id: 2, periodId: 3, facilityDistributions: {1: {name: 'facilityDistribution1'}, 2: {name: 'facilityDistribution2'}}};

    var distribution = new Distribution(distributionJson);

    expect($.extend).toHaveBeenCalledWith(true, distribution, distributionJson);
  });

});
