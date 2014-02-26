/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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

  it("should add all arguments after parsing to int with base ten ignoring undefined, non parsable values", function () {
    expect(utils.sum(1, 2, 3, "100")).toEqual(106);
    expect(utils.sum(undefined, undefined)).toEqual(0);
    expect(utils.sum(undefined, 100, "100")).toEqual(200);
  });
});
