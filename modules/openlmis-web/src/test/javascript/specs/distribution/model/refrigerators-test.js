/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("Refrigerators", function () {

  var refrigerators;

  beforeEach(function () {
    refrigerators = new Refrigerators({
      refrigeratorReadings: [
        {refrigerator: {serialNumber: "abc"}},
        {refrigerator: {serialNumber: "XYZ"}}
      ]
    })
  });


  it('should set status indicator to complete if all refrigeratorReadings are complete', function () {
    refrigerators.refrigeratorReadings = [
      {status: 'is-complete'}
    ];

    expect(refrigerators.computeStatus()).toEqual('is-complete');
  });

  it('should set status indicator to empty if all refrigeratorReadings are empty', function () {
    refrigerators.refrigeratorReadings = [
      {status: 'is-empty'},
      {status: 'is-empty'}
    ];

    expect(refrigerators.computeStatus()).toEqual('is-empty');
  });

  it('should set status indicator to incomplete if at least one refrigeratorReading is incomplete', function () {
    refrigerators.refrigeratorReadings = [
      {status: 'is-incomplete'}
    ];

    expect(refrigerators.computeStatus()).toEqual('is-incomplete');
  });

  it('should set status indicator to incomplete if at least one refrigeratorReading is complete and rest are empty',
    function () {
      refrigerators.refrigeratorReadings = [
        {status: 'is-complete'},
        {status: 'is-complete'},
        {status: 'is-empty'}
      ];

      expect(refrigerators.computeStatus()).toEqual('is-incomplete');
    });

  it('should set status indicator to complete if no refrigeratorReading exists', function () {
    refrigerators.refrigeratorReadings = [];

    expect(refrigerators.computeStatus()).toEqual('is-complete');
  });

});