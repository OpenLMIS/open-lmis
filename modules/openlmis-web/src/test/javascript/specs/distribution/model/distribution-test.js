/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('Distribution', function () {
  var COMPLETE = 'is-complete';
  var INCOMPLETE = 'is-incomplete';
  var EMPTY = 'is-empty';

  var distribution;

  var dispensaryData, level3HospitalData;

  beforeEach(function () {
    distribution = new Distribution({facilityDistributionData: [{}, {}]});
    dispensaryData = distribution.facilityDistributionData[0];
    level3HospitalData = distribution.facilityDistributionData[1];
  });

  it('should return complete if all data available for facilities', function () {
    spyOn(dispensaryData, "computeStatus").andReturn(COMPLETE);
    spyOn(level3HospitalData, "computeStatus").andReturn(COMPLETE);

    expect(distribution.computeStatus()).toEqual(COMPLETE);
  });

  it('should return empty if no data is available for any facilities', function () {
    spyOn(dispensaryData, "computeStatus").andReturn(EMPTY);
    spyOn(level3HospitalData, "computeStatus").andReturn(EMPTY);

    expect(distribution.computeStatus()).toEqual(EMPTY);
  });

  it('should return empty if no facility is available', function () {
    spyOn(dispensaryData, "computeStatus").andReturn(EMPTY);
    spyOn(level3HospitalData, "computeStatus").andReturn(EMPTY);

    expect(distribution.computeStatus()).toEqual(EMPTY);
  });

  it('should return incomplete if data is available for atleast one facility', function () {
    spyOn(dispensaryData, "computeStatus").andReturn(EMPTY);
    spyOn(level3HospitalData, "computeStatus").andReturn(INCOMPLETE);

    expect(distribution.computeStatus()).toEqual(INCOMPLETE);
  });

  it('should return incomplete if data is available for atleast one facility (another case)', function () {
    spyOn(dispensaryData, "computeStatus").andReturn(INCOMPLETE);
    spyOn(level3HospitalData, "computeStatus").andReturn(EMPTY);

    expect(distribution.computeStatus()).toEqual(INCOMPLETE);
  });

  it('should return incomplete if data is available for atleast one facility (one of them is complete)', function () {
    spyOn(dispensaryData, "computeStatus").andReturn(COMPLETE);
    spyOn(level3HospitalData, "computeStatus").andReturn(EMPTY);

    expect(distribution.computeStatus()).toEqual(INCOMPLETE);
  });

  it('should return incomplete if data is available for atleast one facility (one of them is complete, order changed)', function () {
    spyOn(dispensaryData, "computeStatus").andReturn(INCOMPLETE);
    spyOn(level3HospitalData, "computeStatus").andReturn(COMPLETE);

    expect(distribution.computeStatus()).toEqual(INCOMPLETE);
  });
});