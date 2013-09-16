/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
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