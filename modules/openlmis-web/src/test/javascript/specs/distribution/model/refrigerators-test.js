/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
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