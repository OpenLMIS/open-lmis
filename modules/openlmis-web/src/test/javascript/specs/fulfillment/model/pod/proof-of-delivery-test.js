/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe('Proof of delivery', function () {
  it('should return no errors if all line items have quantity received', function () {
    var error = new ProofOfDelivery({podLineItems: [
      {id: 1, quantityReceived: 34},
      {id: 2, quantityReceived: 56}
    ]}).error(1);

    expect(error).toEqual({errorPages: null});
  });

  it('should return page numbers as array if it contains line items with undefined or null quantity received', function () {
    var error = new ProofOfDelivery({podLineItems: [
      {id: 1, quantityReceived: null},
      {id: 2, quantityReceived: 12},
      {id: 3, quantityReceived: undefined},
      {id: 4, quantityReceived: undefined}
    ]}).error(3);

    expect(error).toEqual({errorPages: [1, 2]});
  });
});