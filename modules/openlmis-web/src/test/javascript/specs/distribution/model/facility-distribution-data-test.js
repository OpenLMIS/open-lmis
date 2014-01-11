/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
describe('Facility Distribution data', function () {
  var facilityDistributions, epiUse, refrigerators, facilityVisit, epiInventory, coverage;

  beforeEach(function () {
    facilityDistributions = new FacilityDistribution({facilityVisit: {id: 1}});
    epiUse = facilityDistributions.epiUse;
    refrigerators = facilityDistributions.refrigerators;
    facilityVisit = facilityDistributions.facilityVisit;
    epiInventory = facilityDistributions.epiInventory;
    coverage = facilityDistributions.coverage;
  });

  it("should compute status as complete when all the forms for the facility are COMPLETE", function () {
    spyOn(epiUse, "computeStatus").andReturn(DistributionStatus.COMPLETE);
    spyOn(refrigerators, "computeStatus").andReturn(DistributionStatus.COMPLETE);
    spyOn(facilityVisit, "computeStatus").andReturn(DistributionStatus.COMPLETE);
    spyOn(epiInventory, "computeStatus").andReturn(DistributionStatus.COMPLETE);
    spyOn(coverage, "computeStatus").andReturn(DistributionStatus.COMPLETE);

    expect(facilityDistributions.computeStatus()).toEqual(DistributionStatus.COMPLETE);
  });

  it("should compute status as empty when all the forms for the facility are empty", function () {
    spyOn(epiUse, "computeStatus").andReturn(DistributionStatus.EMPTY);
    spyOn(refrigerators, "computeStatus").andReturn(DistributionStatus.EMPTY);
    spyOn(facilityVisit, "computeStatus").andReturn(DistributionStatus.EMPTY);
    spyOn(epiInventory, "computeStatus").andReturn(DistributionStatus.EMPTY);

    expect(facilityDistributions.computeStatus()).toEqual(DistributionStatus.EMPTY);
  });

  it("should compute status as incomplete when one of the forms for the facility is either empty or incomplete", function () {
    spyOn(epiUse, "computeStatus").andReturn(DistributionStatus.EMPTY);
    spyOn(refrigerators, "computeStatus").andReturn(DistributionStatus.COMPLETE);
    spyOn(facilityVisit, "computeStatus").andReturn(DistributionStatus.INCOMPLETE);

    expect(facilityDistributions.computeStatus()).toEqual(DistributionStatus.INCOMPLETE);
  });

  it("should compute status as incomplete when one of the forms for the facility is either empty or incomplete (another variant)", function () {
    spyOn(epiUse, "computeStatus").andReturn(DistributionStatus.EMPTY);
    spyOn(refrigerators, "computeStatus").andReturn(DistributionStatus.INCOMPLETE);
    spyOn(facilityVisit, "computeStatus").andReturn(DistributionStatus.INCOMPLETE);

    expect(facilityDistributions.computeStatus()).toEqual(DistributionStatus.INCOMPLETE);
  });
});