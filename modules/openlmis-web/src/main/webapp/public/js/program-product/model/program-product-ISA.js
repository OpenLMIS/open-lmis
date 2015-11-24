/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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

      //For Tanzania
      this.populationSource = programProductIsa.populationSource;
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

  function getNumericDisplayVal(val)
  {
    if( val === undefined || isNaN(val) )
      return '#';
    else
      return val;
  }

  ProgramProductISA.prototype.getIsaFormula = function() {
    var adjustmentVal = utils.parseIntWithBaseTen(this.adjustmentValue);
    adjustmentVal = adjustmentVal > 0 ? getNumericDisplayVal(adjustmentVal) : "(" + getNumericDisplayVal(adjustmentVal) + ")";

    var whoRatioVal = (this.whoRatio / 100).toFixed(5);


    return "(population) * " +
        getNumericDisplayVal( (this.whoRatio / 100).toFixed(5) ) +
      " * " + getNumericDisplayVal( utils.parseIntWithBaseTen(this.dosesPerYear) ) +
      " * " + getNumericDisplayVal( parseFloat(this.wastageFactor).toFixed(3) ) +
      " / 12 * " + getNumericDisplayVal( (1 + this.bufferPercentage / 100).toFixed(5) ) +
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