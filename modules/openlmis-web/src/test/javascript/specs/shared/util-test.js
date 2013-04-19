/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe("Util", function () {
  it('should get formatted date string', function () {
    var date = new Date(2013, 1, 8, 11, 30, 59, 0);
    expect(utils.getFormattedDate(date)).toEqual('08/02/2013');
  });

  it('should parse an integer with base 10', function () {
    expect(utils.parseIntWithBaseTen('999')).toEqual(999);
  });

  it('should parse an integer beginning with 0 to base 10 equivalent', function () {
    expect(utils.parseIntWithBaseTen('09')).toEqual(9);
  });

  it('should determine if it is a number', function () {
    expect(utils.isNumber('09')).toEqual(true);
    expect(utils.isNumber(09)).toEqual(true);
    expect(utils.isNumber(null)).toEqual(false);
    expect(utils.isNumber(undefined)).toEqual(false);
    expect(utils.isNumber("9a")).toEqual(false);
    expect(utils.isNumber('abc')).toEqual(false);
    expect(utils.isNumber('  ')).toEqual(false);
  });

  it('should get number with base 10 equivalent', function () {
    expect(utils.getValueFor('09')).toEqual(9);
    expect(utils.getValueFor('abc')).toEqual(null);
    expect(utils.getValueFor(null)).toEqual(null);
    expect(utils.getValueFor(NaN)).toEqual(null);
  });
});
