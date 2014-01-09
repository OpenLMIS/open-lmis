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

  it('should set all Not Recorded flags to true for Epi Use', function () {
    var facilityId = '4';
    var epiUse = jasmine.createSpyObj('EpiUse', ['setNotRecorded']);
    var distribution = new Distribution({facilityDistributions: {4: {epiUse: epiUse, facilityVisit: {id: 1}}}});

    distribution.setEpiUseNotRecorded(facilityId);

    expect(epiUse.setNotRecorded).toHaveBeenCalled();
  });


  it('should set all Not Recorded flags to true for Epi Inventory', function () {
    var facilityId = '4';
    var epiInventory = jasmine.createSpyObj('EpiInventory', ['setNotRecorded']);
    var distribution = new Distribution({facilityDistributions: {4: {epiInventory: epiInventory, facilityVisit: {id: 1}}}});

    distribution.setEpiInventoryNotRecorded(facilityId);

    expect(epiInventory.setNotRecorded).toHaveBeenCalled();
  });

  it('should set all Not Recorded flags to true for Coverage', function () {
    var facilityId = '4';
    var coverage = jasmine.createSpyObj('Coverage', ['setNotRecorded']);
    var distribution = new Distribution({facilityDistributions: {4: {coverage: coverage, facilityVisit: {id: 1}}}});

    distribution.setCoverageNotRecorded(facilityId);

    expect(coverage.setNotRecorded).toHaveBeenCalled();
  });

});
