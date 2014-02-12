/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe('Child coverage', function () {
  var childCoverage;

  beforeEach(function () {
    var childCoverageLineItem1, childCoverageLineItem2, openedVialLineItem1, openedVialLineItem2;

    childCoverageLineItem1 = {"id": 5, "facilityVisitId": 3, "vaccination": "BCG", "healthCenter11Months": {value: 1},
      "outReach11Months": {value: undefined}, "healthCenter23Months": {value: 3}, "outReach23Months": {value: undefined}};
    childCoverageLineItem2 = {"id": 26, "facilityVisitId": 3, "vaccination": "Polio (Newborn)", "healthCenter11Months": {value: undefined},
      "outReach11Months": {value: undefined}, "healthCenter23Months": {value: undefined}, "outReach23Months": {value: undefined}};

    openedVialLineItem1 = {"id": 15, "facilityVisitId": 3, "productVialName": "BCG", "packSize": 10, openedVial: {value: null, notRecorded: true}};
    openedVialLineItem2 = {"id": 16, "facilityVisitId": 3, "productVialName": "Polio10", "packSize": 10, openedVial: {value: null, notRecorded: true}};

    childCoverage = new ChildCoverage(123, {
      childCoverageLineItems: [childCoverageLineItem1, childCoverageLineItem2],
      openedVialLineItems: [openedVialLineItem1, openedVialLineItem2]
    });

  });

  describe('Apply NR to all', function () {

    function verifyCoverageLineItemNotRecordedSet(lineItem) {
      expect(lineItem.healthCenter11Months.notRecorded).toBeTruthy();
      expect(lineItem.outReach11Months.notRecorded).toBeTruthy();
      expect(lineItem.healthCenter23Months.notRecorded).toBeTruthy();
      expect(lineItem.outReach23Months.notRecorded).toBeTruthy();
    }

    it('should set all NR flags to true', function () {
      childCoverage.setNotRecorded();

      verifyCoverageLineItemNotRecordedSet(childCoverage.childCoverageLineItems[0]);
      verifyCoverageLineItemNotRecordedSet(childCoverage.childCoverageLineItems[1]);

      expect(childCoverage.openedVialLineItems[0].openedVial.notRecorded).toBeTruthy();
      expect(childCoverage.openedVialLineItems[1].openedVial.notRecorded).toBeTruthy();
    });

    it('should create child coverage object from JSON', function () {
      spyOn($, 'extend');
      var childCoverageJSON = {blah: 'blah blah'};

      var childCoverage = new ChildCoverage(123, childCoverageJSON);

      expect($.extend).toHaveBeenCalledWith(true, childCoverage, childCoverageJSON);
    });
  });

  describe('compute status', function () {
    it('should set status as is-empty if form is blank', function () {
      var emptyChildCoverage = new ChildCoverage(12, {
        childCoverageLineItems: [
          {"id": 5, "facilityVisitId": 3, "vaccination": "BCG"},
          {"id": 26, "facilityVisitId": 3, "vaccination": "Polio (Newborn)"}
        ],
        openedVialLineItems: [
          {"id": 15, "facilityVisitId": 3, "productVialName": "BCG", "packSize": 10},
          {"id": 16, "facilityVisitId": 3, "productVialName": "Polio10", "packSize": 10}
        ]
      });
      var status = emptyChildCoverage.computeStatus();
      expect(status).toEqual(DistributionStatus.EMPTY);
    });

    it('should set status as is-empty if no fields filled', function () {
      var unfilledChildCoverage = new ChildCoverage(12, {
        childCoverageLineItems: [
          {"id": 5, "facilityVisitId": 3, "vaccination": "BCG", healthCenter11Months: {value: undefined}, outReach11Months: {value: undefined}, healthCenter23Months: {value: undefined}, outReach23Months: {value: undefined}},
          {"id": 26, "facilityVisitId": 3, "vaccination": "Polio (Newborn)", healthCenter11Months: {value: undefined}, outReach11Months: {value: undefined}, healthCenter23Months: {value: undefined}, outReach23Months: {value: undefined}}
        ],
        openedVialLineItems: [
          {"id": 15, "facilityVisitId": 3, "productVialName": "BCG", "packSize": 10,  openedVial: {value: undefined}},
          {"id": 16, "facilityVisitId": 3, "productVialName": "Polio10", "packSize": 10,  openedVial: {value: undefined}}
        ]
      });
      var status = unfilledChildCoverage.computeStatus();
      expect(status).toEqual(DistributionStatus.EMPTY);
    });

    it('should set status as is-incomplete if some child coverage fields filled', function () {
      var partiallyFilledChildCoverage = new ChildCoverage(12, {
        childCoverageLineItems: [
          {"id": 5, "facilityVisitId": 3, "vaccination": "BCG", healthCenter11Months: {value: 56}, outReach11Months: {value: undefined}, healthCenter23Months: {value: undefined}, outReach23Months: {value: undefined}},
          {"id": 26, "facilityVisitId": 3, "vaccination": "Polio (Newborn)", healthCenter11Months: {value: undefined}, outReach11Months: {value: undefined}, healthCenter23Months: {value: undefined}, outReach23Months: {value: undefined}}
        ],
        openedVialLineItems: [
          {"id": 15, "facilityVisitId": 3, "productVialName": "BCG", "packSize": 10,  openedVial: {value: undefined}},
          {"id": 16, "facilityVisitId": 3, "productVialName": "Polio10", "packSize": 10,  openedVial: {value: undefined}}
        ]
      });
      var status = partiallyFilledChildCoverage.computeStatus();
      expect(status).toEqual(DistributionStatus.INCOMPLETE);
    });

  });
});