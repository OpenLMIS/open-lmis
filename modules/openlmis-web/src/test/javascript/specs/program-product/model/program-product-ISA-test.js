/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('ProgramProductIsa', function () {
  var isa, programProductIsa;
  beforeEach(function () {
    isa = {
      id: 1,
      whoRatio: 1,
      adjustmentValue: 33,
      dosesPerYear: 45,
      wastageFactor: 1,
      bufferPercentage: 7,
      minimumValue: 3
    }

    programProductIsa = new ProgramProductISA();
  });

  it('should create programProductIsa with default values', function () {

    expect(programProductIsa.whoRatio).toEqual(0);
    expect(programProductIsa.adjustmentValue).toEqual(0);
    expect(programProductIsa.dosesPerYear).toEqual(0);
    expect(programProductIsa.wastageFactor).toEqual(0);
    expect(programProductIsa.bufferPercentage).toEqual(0);
    expect(programProductIsa.minimumValue).toEqual(undefined);
    expect(programProductIsa.maximumValue).toEqual(undefined);
    expect(programProductIsa.id).toEqual(undefined);
  });

  it('should initialize programProductIsa with provided values', function () {

    programProductIsa.init(isa);

    expect(programProductIsa.whoRatio).toEqual(1);
    expect(programProductIsa.adjustmentValue).toEqual(33);
    expect(programProductIsa.dosesPerYear).toEqual(45);
    expect(programProductIsa.wastageFactor).toEqual(1);
    expect(programProductIsa.bufferPercentage).toEqual(7);
    expect(programProductIsa.minimumValue).toEqual(3);
    expect(programProductIsa.maximumValue).toEqual(undefined);
    expect(programProductIsa.id).toEqual(1);
  });

  it('should return true if all mandatory fields are defined for program product isa', function () {
    var isPresent = programProductIsa.isPresent();

    expect(isPresent).toBeTruthy();
  });

  it('should return false if at least one mandatory field is not defined for program product isa', function () {
    programProductIsa.whoRatio = undefined;
    var isPresent = programProductIsa.isPresent();

    expect(isPresent).toBeFalsy();
  });

  it('should return true if max value is less than min value', function () {
    programProductIsa.maximumValue = 5;
    programProductIsa.minimumValue = 50;
    var result = programProductIsa.isMaxLessThanMinValue();

    expect(result).toBeTruthy();
  });

  it('should return false if max value is not less than min value', function () {
    programProductIsa.maximumValue = 50;
    programProductIsa.minimumValue = 50;
    var result = programProductIsa.isMaxLessThanMinValue();

    expect(result).toBeFalsy();
  });

  it('should return calculated isa value as zero if population is undefined', function () {
    var population = undefined;
    var isaValue = programProductIsa.calculate(population);

    expect(isaValue).toEqual(0);
  });

  it('should return calculated isa value if population is not undefined and calculated value is in {min,max} range', function () {
    var population = 4;
    programProductIsa.init(isa);

    var isaValue = programProductIsa.calculate(population);

    expect(isaValue).toEqual(34);
  });

  it('should return minimum isa value if calculated isa value is less than minimum isa value', function () {
    var population = 4;
    isa.minimumValue = 50;
    programProductIsa.init(isa);

    var isaValue = programProductIsa.calculate(population);

    expect(isaValue).toEqual(50);
  });

  it('should return maximum isa value if calculated isa value is more than maximum isa value', function () {
    var population = 4;
    isa.maximumValue = 30;
    isa.minimumValue = 3;
    programProductIsa.init(isa);

    var isaValue = programProductIsa.calculate(population);

    expect(isaValue).toEqual(30);
  });

  it('should return isa value as zero if calculated isa value is less than zero and min and max values are undefined ', function () {
    var population = 4;
    isa.minimumValue = undefined;
    isa.maximumValue = undefined;
    isa.adjustmentValue = -50;
    programProductIsa.init(isa);

    var isaValue = programProductIsa.calculate(population);

    expect(isaValue).toEqual(0);
  });

  it('should return isa formula for adjustment value less than or equal to zero', function () {
    programProductIsa.adjustmentValue = -10;
    var isaFormula = programProductIsa.getIsaFormula();

    expect(isaFormula).toEqual("(population) * 0.00000 * 0 * 1.00000 / 12 * 1.00000 + (-10)")
  });

  it('should return isa formula for adjustment value greater than zero', function () {
    programProductIsa.adjustmentValue = 4;
    var isaFormula = programProductIsa.getIsaFormula();

    expect(isaFormula).toEqual("(population) * 0.00000 * 0 * 1.00000 / 12 * 1.00000 + 4")
  });

})
