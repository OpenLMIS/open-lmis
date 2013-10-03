/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
describe('Facility Distribution data', function() {
  var facilityDistributionData, epiUse, refrigerators, generalObservation;
  var COMPLETE = 'is-complete';
  var INCOMPLETE = 'is-incomplete';
  var EMPTY = 'is-empty';

  beforeEach(function() {
    facilityDistributionData = new FacilityDistributionData({});
    epiUse = facilityDistributionData.epiUse;
    refrigerators = facilityDistributionData.refrigerators;
    generalObservation = facilityDistributionData.generalObservation;
  });

  it("should compute status as complete when all the forms for the facility are COMPLETE",function(){
    spyOn(epiUse, "computeStatus").andReturn(COMPLETE);
    spyOn(refrigerators, "computeStatus").andReturn(COMPLETE);
    spyOn(generalObservation, "computeStatus").andReturn(COMPLETE);

    expect(facilityDistributionData.computeStatus()).toEqual(COMPLETE);
  });

  it("should compute status as empty when all the forms for the facility are empty",function(){
    spyOn(epiUse, "computeStatus").andReturn(EMPTY);
    spyOn(refrigerators, "computeStatus").andReturn(EMPTY);
    spyOn(generalObservation, "computeStatus").andReturn(EMPTY);

    expect(facilityDistributionData.computeStatus()).toEqual(EMPTY);
  });

  it("should compute status as incomplete when one of the forms for the facility is either empty or incomplete",function(){
    spyOn(epiUse, "computeStatus").andReturn(EMPTY);
    spyOn(refrigerators, "computeStatus").andReturn(COMPLETE);
    spyOn(generalObservation, "computeStatus").andReturn(INCOMPLETE);

    expect(facilityDistributionData.computeStatus()).toEqual(INCOMPLETE);
  });

  it("should compute status as incomplete when one of the forms for the facility is either empty or incomplete (another variant)",function(){
    spyOn(epiUse, "computeStatus").andReturn(EMPTY);
    spyOn(refrigerators, "computeStatus").andReturn(INCOMPLETE);
    spyOn(generalObservation, "computeStatus").andReturn(INCOMPLETE);

    expect(facilityDistributionData.computeStatus()).toEqual(INCOMPLETE);
  });
});