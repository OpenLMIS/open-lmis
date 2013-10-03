/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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
    isa.adjustmentValue = -10;
    programProductIsa.init(isa);
    var isaFormula = programProductIsa.getIsaFormula();

    expect(isaFormula).toEqual("(population) * 0.01000 * 45 * 1.000 / 12 * 1.07000 + (-10)")
  });

  it('should return isa formula for adjustment value greater than zero', function () {
    isa.adjustmentValue = 4;
    programProductIsa.init(isa);
    var isaFormula = programProductIsa.getIsaFormula();

    expect(isaFormula).toEqual("(population) * 0.01000 * 45 * 1.000 / 12 * 1.07000 + 4")
  });

})
