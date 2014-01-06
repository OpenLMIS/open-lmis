/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('Facility Visit', function () {
  it('should return empty if no fields filled', function () {
    var facilityVisit = new FacilityVisit({});

    var status = facilityVisit.computeStatus();

    expect(status).toEqual(DistributionStatus.EMPTY);
  });

  it('should return incomplete if visit details not present', function () {
    var facilityVisit = new FacilityVisit({verifiedBy: {name: 'something', title: 'title'}, confirmedBy: {name: 'something', title: 'title'}});

    var status = facilityVisit.computeStatus();

    expect(status).toEqual(DistributionStatus.INCOMPLETE);
  });

  it('should return incomplete if verified By Name not present', function () {
    var facilityVisit = new FacilityVisit({observations: "blah blah blah", verifiedBy: {name: '', title: 'title'}, confirmedBy: {name: 'something', title: 'title'}});

    var status = facilityVisit.computeStatus();

    expect(status).toEqual(DistributionStatus.INCOMPLETE);
  });

  it('should return empty if visit details is undefined', function () {
    var facilityVisit = new FacilityVisit();

    var status = facilityVisit.computeStatus();

    expect(status).toEqual(DistributionStatus.EMPTY);
  });

  it('should return complete if visit details valid', function () {
    var facilityVisit = new FacilityVisit({observations: "blah blah blah", verifiedBy: {name: 'Pintu', title: 'title'}, confirmedBy: {name: 'something', title: 'title'}});

    var status = facilityVisit.computeStatus();

    expect(status).toEqual(DistributionStatus.COMPLETE);
  });

  it('should retain its status', function () {
    var facilityVisit = new FacilityVisit({observations: "blah blah blah", verifiedBy: {name: 'Pintu', title: 'title'}, confirmedBy: {name: 'something', title: 'title'}});

    facilityVisit.computeStatus();

    expect(facilityVisit.status).toEqual(DistributionStatus.COMPLETE);
  });
});