/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *  Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe('Coverage', function () {
  var facilityVisitId = 1;
  it('should set status as empty if coverage form is empty', function () {
    var coverage = new Coverage(facilityVisitId, {fullCoverage: {}});

    var status = coverage.computeStatus();

    expect(status).toEqual(DistributionStatus.EMPTY);
  });

  it('should set status as complete if coverage form is valid and filled', function () {
    var coverage = new Coverage(facilityVisitId, {fullCoverage: {
      femaleHealthCenterReading: {value: 123}, femaleMobileBrigadeReading: {value: 5432}, maleHealthCenterReading: {value: 3}, maleMobileBrigadeReading: {value: 23}
    }});

    var status = coverage.computeStatus();

    expect(status).toEqual(DistributionStatus.COMPLETE);
  });

  it('should set status as complete if the form is valid', function () {
    var coverage = new Coverage(facilityVisitId, {fullCoverage: {
      femaleHealthCenterReading: {notRecorded: true}, femaleMobileBrigadeReading: {value: 5432}, maleHealthCenterReading: {value: 3}, maleMobileBrigadeReading: {value: 23}
    }});

    var status = coverage.computeStatus();

    expect(status).toEqual(DistributionStatus.COMPLETE);
  });

  it('should set status as incomplete if the only last form field valid', function () {
    var coverage = new Coverage(facilityVisitId, {fullCoverage: {
      femaleHealthCenterReading: {value: 1210}, femaleMobileBrigadeReading: {value: 5432}
    }});

    var status = coverage.computeStatus();

    expect(status).toEqual(DistributionStatus.INCOMPLETE);
  });

  it('should set not recorded checkbox for epi use', function () {
    var coverage = new Coverage(facilityVisitId, {fullCoverage: {
      femaleHealthCenterReading: {notRecorded: true}, femaleMobileBrigadeReading: {value: 5432}, maleHealthCenterReading: {value: 3}, maleMobileBrigadeReading: {value: 23}
    }});

    coverage.setNotRecorded();

    expect(coverage.fullCoverage.femaleHealthCenterReading.notRecorded).toBeTruthy();
    expect(coverage.fullCoverage.femaleMobileBrigadeReading.notRecorded).toBeTruthy();
    expect(coverage.fullCoverage.maleHealthCenterReading.notRecorded).toBeTruthy();
    expect(coverage.fullCoverage.maleMobileBrigadeReading.notRecorded).toBeTruthy();
  });
});