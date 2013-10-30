/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

var ProgramProductISA = function (isa) {

  $.extend(true, this, isa);

  this.adjustmentValue = this.adjustmentValue || 0;
  this.whoRatio = this.whoRatio || 0;
  this.dosesPerYear = this.dosesPerYear || 0;
  this.wastageFactor = this.wastageFactor || 0;
  this.bufferPercentage = this.bufferPercentage || 0;

  ProgramProductISA.prototype.init = function (programProductIsa) {
    if (programProductIsa) {
      this.id = programProductIsa.id;
      this.adjustmentValue = programProductIsa.adjustmentValue;
      this.whoRatio = programProductIsa.whoRatio;
      this.dosesPerYear = programProductIsa.dosesPerYear;
      this.wastageFactor = programProductIsa.wastageFactor;
      this.bufferPercentage = programProductIsa.bufferPercentage;
      this.minimumValue = programProductIsa.minimumValue;
      this.maximumValue = programProductIsa.maximumValue;
    }
  };

  ProgramProductISA.prototype.isPresent = function () {
    return this.isDefined(this.whoRatio) && this.isDefined(this.dosesPerYear) &&
      this.isDefined(this.wastageFactor) && this.isDefined(this.bufferPercentage) &&
      this.isDefined(this.adjustmentValue);
  };

  ProgramProductISA.prototype.isDefined = function (value) {
    return !(value === null || value === undefined || value === "-" || value === "." || value[value.length - 1] === '.');
  };

  ProgramProductISA.prototype.isMaxLessThanMinValue = function () {
    return((this.maximumValue && this.minimumValue) !== null && utils.parseIntWithBaseTen(this.maximumValue) < utils.parseIntWithBaseTen(this.minimumValue));
  };

  ProgramProductISA.prototype.getIsaFormula = function () {
    var adjustmentVal = utils.parseIntWithBaseTen(this.adjustmentValue);
    adjustmentVal = adjustmentVal > 0 ? adjustmentVal : "(" + adjustmentVal + ")";
    return "(population) * " +
      (this.whoRatio / 100).toFixed(5) +
      " * " + utils.parseIntWithBaseTen(this.dosesPerYear) +
      " * " + parseFloat(this.wastageFactor).toFixed(3) +
      " / 12 * " + (1 + this.bufferPercentage / 100).toFixed(5) +
      " + " + adjustmentVal;
  };

  ProgramProductISA.prototype.calculate = function (population) {
    var isaValue = 0;
    if (population !== undefined) {
      isaValue = Math.ceil(parseInt(population, 10) *
        (parseFloat(this.whoRatio) / 100) *
        (utils.parseIntWithBaseTen(this.dosesPerYear)) *
        (parseFloat(this.wastageFactor) / 12) *
        (1 + parseFloat(this.bufferPercentage) / 100) +
        (utils.parseIntWithBaseTen(this.adjustmentValue)));

      if (this.minimumValue !== null && isaValue < this.minimumValue)
        return this.minimumValue;
      if (this.maximumValue !== null && isaValue > this.maximumValue)
        return this.maximumValue;

      isaValue = isaValue < 0 ? 0 : isaValue;
    }
    return isaValue;
  };
};