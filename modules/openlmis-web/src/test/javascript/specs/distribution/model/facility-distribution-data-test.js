/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */
describe('Facility Distribution data', function() {
  var facilityDistributionData, epiUse, refrigerators, generalObservations;
  var COMPLETE = 'is-complete';
  var INCOMPLETE = 'is-incomplete';
  var EMPTY = 'is-empty';

  beforeEach(function() {
    facilityDistributionData = new FacilityDistributionData({});
    epiUse = facilityDistributionData.epiUse;
    refrigerators = facilityDistributionData.refrigerators;
    generalObservations = facilityDistributionData.generalObservations;
  });

  it("should compute status as complete when all the forms for the facility are COMPLETE",function(){
    spyOn(epiUse, "computeStatus").andReturn(COMPLETE);
    spyOn(refrigerators, "computeStatus").andReturn(COMPLETE);
    spyOn(generalObservations, "computeStatus").andReturn(COMPLETE);

    expect(facilityDistributionData.computeStatus()).toEqual(COMPLETE);
  });

  it("should compute status as empty when all the forms for the facility are empty",function(){
    spyOn(epiUse, "computeStatus").andReturn(EMPTY);
    spyOn(refrigerators, "computeStatus").andReturn(EMPTY);
    spyOn(generalObservations, "computeStatus").andReturn(EMPTY);

    expect(facilityDistributionData.computeStatus()).toEqual(EMPTY);
  });

  it("should compute status as incomplete when one of the forms for the facility is either empty or incomplete",function(){
    spyOn(epiUse, "computeStatus").andReturn(EMPTY);
    spyOn(refrigerators, "computeStatus").andReturn(COMPLETE);
    spyOn(generalObservations, "computeStatus").andReturn(INCOMPLETE);

    expect(facilityDistributionData.computeStatus()).toEqual(INCOMPLETE);
  });

  it("should compute status as incomplete when one of the forms for the facility is either empty or incomplete (another variant)",function(){
    spyOn(epiUse, "computeStatus").andReturn(EMPTY);
    spyOn(refrigerators, "computeStatus").andReturn(INCOMPLETE);
    spyOn(generalObservations, "computeStatus").andReturn(INCOMPLETE);

    expect(facilityDistributionData.computeStatus()).toEqual(INCOMPLETE);
  });
});