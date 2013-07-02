var ProgramProductISA = function (isa) {

  $.extend(true, this, isa);

  this.adjustmentValue = this.adjustmentValue || 0;
  this.whoRatio = this.whoRatio || 0;
  this.dosesPerYear = this.dosesPerYear || 0;
  this.wastageRate = this.wastageRate || 0;
  this.bufferPercentage = this.bufferPercentage || 0;

  ProgramProductISA.prototype.init = function (programProductIsa) {
    if (programProductIsa) {
      this.id = programProductIsa.id;
      this.adjustmentValue = programProductIsa.adjustmentValue;
      this.whoRatio = programProductIsa.whoRatio;
      this.dosesPerYear = programProductIsa.dosesPerYear;
      this.wastageRate = programProductIsa.wastageRate;
      this.bufferPercentage = programProductIsa.bufferPercentage;
      this.minimumValue = programProductIsa.minimumValue;
      this.maximumValue = programProductIsa.maximumValue;
    }
  }

  ProgramProductISA.prototype.isPresent = function () {
    return this.isDefined(this.whoRatio) && this.isDefined(this.dosesPerYear) &&
        this.isDefined(this.wastageRate) && this.isDefined(this.bufferPercentage) &&
        this.isDefined(this.adjustmentValue);
  }

  ProgramProductISA.prototype.isDefined = function (value) {
    return !(value == null || value == undefined || value == "-" || value == "." || value[value.length - 1] == '.');
  }

  ProgramProductISA.prototype.isMaxLessThanMinValue = function () {
    return((this.maximumValue && this.minimumValue) != null && utils.parseIntWithBaseTen(this.maximumValue) < utils.parseIntWithBaseTen(this.minimumValue));
  }

  ProgramProductISA.prototype.getIsaFormula = function () {
    var adjustmentVal = utils.parseIntWithBaseTen(this.adjustmentValue);
    adjustmentVal = adjustmentVal > 0 ? adjustmentVal : "(" + adjustmentVal + ")";
    return "(population) * " +
        (this.whoRatio / 100).toFixed(5) +
        " * " + utils.parseIntWithBaseTen(this.dosesPerYear) +
        " * " + (1 + this.wastageRate / 100).toFixed(5) +
        " / 12 * " + (1 + this.bufferPercentage / 100).toFixed(5) +
        " + " + adjustmentVal;
  }

  ProgramProductISA.prototype.calculate = function (population) {
    var isaValue = 0;
    if (population != undefined) {
      isaValue = Math.ceil(parseInt(population, 10) *
          (parseFloat(this.whoRatio) / 100) *
          (utils.parseIntWithBaseTen(this.dosesPerYear)) *
          (1 + parseFloat(this.wastageRate) / 100) / 12 *
          (1 + parseFloat(this.bufferPercentage) / 100) +
          (utils.parseIntWithBaseTen(this.adjustmentValue)));

      if (this.minimumValue != null && isaValue < this.minimumValue)
        return this.minimumValue;
      if (this.maximumValue != null && isaValue > this.maximumValue)
        return this.maximumValue;

      isaValue = isaValue < 0 ? 0 : isaValue;
    }
    return isaValue;


    }

}