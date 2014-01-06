/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */


describe('epi inventory', function () {

  it('should set status empty if form is empty', function () {
    var epiInventory = new EpiInventory({lineItems: [
      {existingQuantity: undefined, deliveredQuantity: undefined, spoiledQuantity: ""}
    ]});

    var status = epiInventory.computeStatus();

    expect(status).toEqual(DistributionStatus.EMPTY);
  });

  it('should set status as incomplete if only deliveredQuantity is valid', function () {
    var epiInventory = new EpiInventory({lineItems: [
      {existingQuantity: undefined, deliveredQuantity: 2, spoiledQuantity: undefined}
    ]});

    var status = epiInventory.computeStatus();

    expect(status).toEqual(DistributionStatus.INCOMPLETE);
  });

  it('should set status as incomplete if only existingQuantity is valid', function () {
    var epiInventory = new EpiInventory({lineItems: [
      {existingQuantity: {value: 1}, deliveredQuantity: undefined, spoiledQuantity: undefined}
    ]});

    var status = epiInventory.computeStatus();

    expect(status).toEqual(DistributionStatus.INCOMPLETE);
  });

  it('should set status as incomplete if some fields are not recorded and form is partially filled', function() {
    var epiInventory = new EpiInventory({lineItems: [
      {existingQuantity: {notRecorded: true}, deliveredQuantity: undefined, spoiledQuantity: undefined}
    ]});

    var status = epiInventory.computeStatus();

    expect(status).toEqual(DistributionStatus.INCOMPLETE);
  });

  it('should set status as complete if form is completely filled and all fields valid', function() {
    var epiInventory = new EpiInventory({lineItems: [
      {existingQuantity: {notRecorded: true}, deliveredQuantity: 1, spoiledQuantity: {value: 1, notRecorded: false}}
    ]});

    var status = epiInventory.computeStatus();

    expect(status).toEqual(DistributionStatus.COMPLETE);
  });

  it('should set all NR flags to true', function() {
    var epiInventory = new EpiInventory({lineItems: [
      {existingQuantity: {notRecorded: false}, deliveredQuantity: 1, spoiledQuantity: {value: 1, notRecorded: false}}
    ]});

    var status = epiInventory.setNotRecorded();

    expect(epiInventory.lineItems[0].existingQuantity.notRecorded).toBeTruthy();
    expect(epiInventory.lineItems[0].spoiledQuantity.notRecorded).toBeTruthy();
  });

});
