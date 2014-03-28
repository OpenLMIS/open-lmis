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
  var facilityDistribution, epiUse, refrigerators, facilityVisit, epiInventory, fullCoverage, childCoverage;

  beforeEach(function () {
    facilityDistribution = new FacilityDistribution({facilityVisit: {id: 1}});
    epiUse = facilityDistribution.epiUse;
    refrigerators = facilityDistribution.refrigerators;
    facilityVisit = facilityDistribution.facilityVisit;
    epiInventory = facilityDistribution.epiInventory;
    fullCoverage = facilityDistribution.fullCoverage;
    childCoverage = facilityDistribution.childCoverage;
  });

  it("should compute status as complete when all the forms for the facility are COMPLETE", function () {
    spyOn(epiUse, "computeStatus").andReturn(DistributionStatus.COMPLETE);
    spyOn(refrigerators, "computeStatus").andReturn(DistributionStatus.COMPLETE);
    spyOn(facilityVisit, "computeStatus").andReturn(DistributionStatus.COMPLETE);
    spyOn(epiInventory, "computeStatus").andReturn(DistributionStatus.COMPLETE);
    spyOn(fullCoverage, "computeStatus").andReturn(DistributionStatus.COMPLETE);
    spyOn(childCoverage, "computeStatus").andReturn(DistributionStatus.COMPLETE);

    expect(facilityDistribution.computeStatus()).toEqual(DistributionStatus.COMPLETE);
  });

  it("should compute status as empty when all the forms for the facility are empty", function () {
    spyOn(epiUse, "computeStatus").andReturn(DistributionStatus.EMPTY);
    spyOn(refrigerators, "computeStatus").andReturn(DistributionStatus.EMPTY);
    spyOn(facilityVisit, "computeStatus").andReturn(DistributionStatus.EMPTY);
    spyOn(epiInventory, "computeStatus").andReturn(DistributionStatus.EMPTY);
    spyOn(fullCoverage, "computeStatus").andReturn(DistributionStatus.EMPTY);
    spyOn(childCoverage, "computeStatus").andReturn(DistributionStatus.EMPTY);

    expect(facilityDistribution.computeStatus()).toEqual(DistributionStatus.EMPTY);
  });

  it("should compute status as incomplete when one of the forms for the facility is either empty or incomplete", function () {
    spyOn(epiUse, "computeStatus").andReturn(DistributionStatus.EMPTY);
    spyOn(refrigerators, "computeStatus").andReturn(DistributionStatus.COMPLETE);
    spyOn(facilityVisit, "computeStatus").andReturn(DistributionStatus.INCOMPLETE);

    expect(facilityDistribution.computeStatus()).toEqual(DistributionStatus.INCOMPLETE);
  });

  it("should compute status as incomplete when one of the forms for the facility is either empty or incomplete (another variant)", function () {
    spyOn(epiUse, "computeStatus").andReturn(DistributionStatus.EMPTY);
    spyOn(refrigerators, "computeStatus").andReturn(DistributionStatus.INCOMPLETE);
    spyOn(facilityVisit, "computeStatus").andReturn(DistributionStatus.INCOMPLETE);

    expect(facilityDistribution.computeStatus()).toEqual(DistributionStatus.INCOMPLETE);
  });

  it("should disable all forms if synced", function () {
    facilityDistribution.status = DistributionStatus.SYNCED;
    expect(facilityDistribution.isDisabled()).toEqual(true);
  });

  it("should disable all forms if already synced", function () {
    facilityDistribution.status = DistributionStatus.DUPLICATE;
    expect(facilityDistribution.isDisabled()).toEqual(true);
  });

  it("should disable refrigerator form if facility is not visited and tab is 'refrigerators'", function () {
    facilityDistribution.facilityVisit.visited = false;
    expect(facilityDistribution.isDisabled('refrigerators')).toEqual(true);
  });

  it("should disable epi-inventory form if facility is not visited and tab is 'epi-inventory'", function () {
    facilityDistribution.facilityVisit.visited = false;
    expect(facilityDistribution.isDisabled('epi-inventory')).toEqual(true);
  });

  it("should not disable refrigerator form if facility is visited and tab is 'refrigerators'", function () {
    facilityDistribution.facilityVisit.visited = true;
    expect(facilityDistribution.isDisabled('refrigerators')).toEqual(false);
  });

  it("should not disable epi-inventory form if facility is visited and tab is 'epi-inventory'", function () {
    facilityDistribution.facilityVisit.visited = true;
    expect(facilityDistribution.isDisabled('epi-inventory')).toEqual(false);
  });

  it("should not disable tabs other than refrigerator, epi-inventory and epi-use if facility is not visited", function () {
    facilityDistribution.facilityVisit.visited = false;
    expect(facilityDistribution.isDisabled('child-coverage')).toEqual(false);
  });

});