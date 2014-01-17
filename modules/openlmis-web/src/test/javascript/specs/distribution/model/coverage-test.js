/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *  Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe('FullCoverage', function () {
  var facilityVisitId = 1;
  it('should set status as empty if fullCoverage form is empty', function () {
    var fullCoverage = new FullCoverage(facilityVisitId, {});

    var status = fullCoverage.computeStatus();

    expect(status).toEqual(DistributionStatus.EMPTY);
  });

  it('should set status as complete if fullCoverage form is valid and filled', function () {
    var fullCoverage = new FullCoverage(facilityVisitId, {
      femaleHealthCenterReading: {value: 123}, femaleMobileBrigadeReading: {value: 5432}, maleHealthCenterReading: {value: 3}, maleMobileBrigadeReading: {value: 23}
    });

    var status = fullCoverage.computeStatus();

    expect(status).toEqual(DistributionStatus.COMPLETE);
  });

  it('should set status as complete if the form is valid', function () {
    var fullCoverage = new FullCoverage(facilityVisitId, {
      femaleHealthCenterReading: {notRecorded: true}, femaleMobileBrigadeReading: {value: 5432}, maleHealthCenterReading: {value: 3}, maleMobileBrigadeReading: {value: 23}
    });

    var status = fullCoverage.computeStatus();

    expect(status).toEqual(DistributionStatus.COMPLETE);
  });

  it('should set status as incomplete if the only last form field valid', function () {
    var fullCoverage = new FullCoverage(facilityVisitId, {
      femaleHealthCenterReading: {value: 1210}, femaleMobileBrigadeReading: {value: 5432}
    });

    var status = fullCoverage.computeStatus();

    expect(status).toEqual(DistributionStatus.INCOMPLETE);
  });

  it('should set not recorded checkbox for epi use', function () {
    var fullCoverage = new FullCoverage(facilityVisitId, {
      femaleHealthCenterReading: {notRecorded: true}, femaleMobileBrigadeReading: {value: 5432}, maleHealthCenterReading: {value: 3}, maleMobileBrigadeReading: {value: 23}
    });

    fullCoverage.setNotRecorded();

    expect(fullCoverage.femaleHealthCenterReading.notRecorded).toBeTruthy();
    expect(fullCoverage.femaleMobileBrigadeReading.notRecorded).toBeTruthy();
    expect(fullCoverage.maleHealthCenterReading.notRecorded).toBeTruthy();
    expect(fullCoverage.maleMobileBrigadeReading.notRecorded).toBeTruthy();
  });
});