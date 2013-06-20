/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

var RnrLineItem = function (lineItem, numberOfMonths, programRnrColumnList, rnrStatus) {
  $.extend(true, this, lineItem);
  this.numberOfMonths = numberOfMonths;
  this.rnrStatus = rnrStatus;
  this.programRnrColumnList = programRnrColumnList;

  RnrLineItem.prototype.initLossesAndAdjustments = function () {
    var tempLossesAndAdjustments = [];

    _.each(this.lossesAndAdjustments, function (lossAndAdjustmentJson) {
      tempLossesAndAdjustments.push(new LossAndAdjustment(lossAndAdjustmentJson));
    });

    this.lossesAndAdjustments = tempLossesAndAdjustments;
  };

  RnrLineItem.prototype.reduceForApproval = function() {
    return _.pick(this, 'id', 'productCode', 'quantityApproved', 'remarks');
  };

  RnrLineItem.prototype.init = function () {
    this.initLossesAndAdjustments();
    if (this.previousNormalizedConsumptions == undefined || this.previousNormalizedConsumptions == null)
      this.previousNormalizedConsumptions = [];

    this.reEvaluateTotalLossesAndAdjustments();
    this.fillConsumptionOrStockInHand();
    this.calculateCost();
  };

  RnrLineItem.prototype.fillConsumptionOrStockInHand = function () {
    this.beginningBalance = utils.getValueFor(this.beginningBalance);
    this.quantityReceived = utils.getValueFor(this.quantityReceived);
    this.quantityDispensed = utils.getValueFor(this.quantityDispensed);
    this.totalLossesAndAdjustments = utils.getValueFor(this.totalLossesAndAdjustments, 0);
    this.stockInHand = utils.getValueFor(this.stockInHand);

    this.calculateConsumption();
    this.calculateStockInHand();
    this.fillNormalizedConsumption();
    this.calculateTotal();
  };

  function statusBeforeAuthorized() {
    return rnrStatus == 'INITIATED' || rnrStatus == 'SUBMITTED';
  }

  RnrLineItem.prototype.fillPacksToShip = function () {
    this.quantityApproved = utils.getValueFor(this.quantityApproved);
    var orderQuantity;

    if (statusBeforeAuthorized()) orderQuantity = isUndefined(this.quantityRequested) ? this.calculatedOrderQuantity : this.quantityRequested;
    else orderQuantity = this.quantityApproved;

    this.calculatePacksToShip(orderQuantity);
    this.calculateCost();
  };

  RnrLineItem.prototype.fillNormalizedConsumption = function () {
    this.calculateNormalizedConsumption();
    this.fillAMC();
  };

  RnrLineItem.prototype.fillAMC = function () {
    this.calculateAMC();
    this.fillMaxStockQuantity();
  };

  RnrLineItem.prototype.fillMaxStockQuantity = function () {
    this.calculateMaxStockQuantity();
    this.fillCalculatedOrderQuantity();
  };

  RnrLineItem.prototype.fillCalculatedOrderQuantity = function () {
    this.calculateCalculatedOrderQuantity();
    this.fillPacksToShip();
  };

  RnrLineItem.prototype.arithmeticallyInvalid = function () {
    if (this.programRnrColumnList != undefined && this.programRnrColumnList[0].formulaValidationRequired) {
      var beginningBalance = utils.parseIntWithBaseTen(this.beginningBalance);
      var quantityReceived = utils.parseIntWithBaseTen(this.quantityReceived);
      var quantityDispensed = utils.parseIntWithBaseTen(this.quantityDispensed);
      var totalLossesAndAdjustments = utils.parseIntWithBaseTen(this.totalLossesAndAdjustments);
      var stockInHand = utils.parseIntWithBaseTen(this.stockInHand);
      return (utils.isNumber(quantityDispensed) && utils.isNumber(beginningBalance) && utils.isNumber(quantityReceived) &&
        utils.isNumber(totalLossesAndAdjustments) && utils.isNumber(stockInHand)) ?
        quantityDispensed != (beginningBalance + quantityReceived + totalLossesAndAdjustments - stockInHand) : null;
    }
    return false;
  };

  RnrLineItem.prototype.reEvaluateTotalLossesAndAdjustments = function () {
    this.totalLossesAndAdjustments = 0;
    var rnrLineItem = this;
    $(this.lossesAndAdjustments).each(function (index, lossAndAdjustmentObject) {
      rnrLineItem.updateTotalLossesAndAdjustment(lossAndAdjustmentObject.quantity, lossAndAdjustmentObject.type.additive);
    });
  };

  RnrLineItem.prototype.removeLossAndAdjustment = function (lossAndAdjustmentToDelete) {
    this.lossesAndAdjustments = $.grep(this.lossesAndAdjustments, function (lossAndAdjustmentObj) {
      return !(lossAndAdjustmentToDelete.equals(lossAndAdjustmentObj));
    });
    this.updateTotalLossesAndAdjustment(lossAndAdjustmentToDelete.quantity, !lossAndAdjustmentToDelete.type.additive);
  };

  RnrLineItem.prototype.addLossAndAdjustment = function (newLossAndAdjustment) {
    var lossAndAdjustment = new LossAndAdjustment(newLossAndAdjustment);

    newLossAndAdjustment.type = undefined;
    newLossAndAdjustment.quantity = undefined;

    this.lossesAndAdjustments.push(lossAndAdjustment);
    this.updateTotalLossesAndAdjustment(lossAndAdjustment.quantity, lossAndAdjustment.type.additive);
  };

// TODO: This function should encapsulate the logic to calculate packs to ship based on status
  RnrLineItem.prototype.calculatePacksToShip = function (quantity) {
    if (!utils.isNumber(quantity)) {
      this.packsToShip = null;
      return;
    }
    if (quantity == 0) {
      this.packsToShip = 0;
      return;
    }
    this.packsToShip = Math.floor(quantity / utils.parseIntWithBaseTen(this.packSize));
    this.applyRoundingRulesToPacksToShip(quantity);
  };

  RnrLineItem.prototype.applyRoundingRulesToPacksToShip = function (orderQuantity) {
    var remainderQuantity = orderQuantity % utils.parseIntWithBaseTen(this.packSize);

    if (remainderQuantity >= this.packRoundingThreshold)
      this.packsToShip += 1;

    if (this.packsToShip == 0 && !this.roundToZero)
      this.packsToShip = 1;
  };

  RnrLineItem.prototype.calculateCost = function () {
    this.cost = !utils.isNumber(this.packsToShip) ? 0 : parseFloat(this.packsToShip * this.price).toFixed(2);
  };

  RnrLineItem.prototype.calculateTotal = function () {
    if (utils.isNumber(this.beginningBalance) && utils.isNumber(this.quantityReceived)) {
      this.total = this.beginningBalance + this.quantityReceived
    }
    else {
      this.total = null;
    }
  };

  RnrLineItem.prototype.calculateConsumption = function () {
    if (this.getSource('C') != 'CALCULATED') return;

    if (utils.isNumber(this.beginningBalance) && utils.isNumber(this.quantityReceived) && utils.isNumber(this.totalLossesAndAdjustments) && utils.isNumber(this.stockInHand)) {
      this.quantityDispensed = this.beginningBalance + this.quantityReceived + this.totalLossesAndAdjustments - this.stockInHand;
    } else {
      this.quantityDispensed = null;
    }
  };

  RnrLineItem.prototype.calculateStockInHand = function () {
    if (this.getSource('E') != 'CALCULATED') return;

    if (utils.isNumber(this.beginningBalance) && utils.isNumber(this.quantityReceived) && utils.isNumber(this.quantityDispensed)) {
      this.stockInHand = this.beginningBalance + this.quantityReceived + this.totalLossesAndAdjustments - this.quantityDispensed;
    } else {
      this.stockInHand = null;
    }
  };

  RnrLineItem.prototype.calculateNormalizedConsumption = function () {
    var numberOfMonthsInPeriod = 3; // will be picked up from the database in future
    this.stockOutDays = utils.getValueFor(this.stockOutDays);
    this.newPatientCount = utils.getValueFor(this.newPatientCount);
    if (this.getSource('F') == null) this.newPatientCount = 0;

    if (!utils.isNumber(this.quantityDispensed) || !utils.isNumber(this.stockOutDays) || !utils.isNumber(this.newPatientCount)) {
      this.normalizedConsumption = null;
      return;
    }

    this.dosesPerMonth = utils.parseIntWithBaseTen(this.dosesPerMonth);
    var g = utils.parseIntWithBaseTen(this.dosesPerDispensingUnit);
    var consumptionAdjustedWithStockOutDays = ((numberOfMonthsInPeriod * 30) - this.stockOutDays) == 0 ?
      this.quantityDispensed :
      (this.quantityDispensed * ((numberOfMonthsInPeriod * 30) / ((numberOfMonthsInPeriod * 30) - this.stockOutDays)));
    var adjustmentForNewPatients = (this.newPatientCount * Math.ceil(this.dosesPerMonth / g) ) * numberOfMonthsInPeriod;
    this.normalizedConsumption = Math.round(consumptionAdjustedWithStockOutDays + adjustmentForNewPatients);
  };

  RnrLineItem.prototype.calculateAMC = function () {
    if (!utils.isNumber(this.normalizedConsumption)) {
      this.amc = null;
      return;
    }
    var numberOfMonthsInPeriod = numberOfMonths;
    var divider = numberOfMonthsInPeriod * (1 + this.previousNormalizedConsumptions.length);

    this.amc = Math.round((this.normalizedConsumption + this.sumOfPreviousNormalizedConsumptions()) / divider);
  };

  RnrLineItem.prototype.sumOfPreviousNormalizedConsumptions = function () {
    var total = 0;
    this.previousNormalizedConsumptions.forEach(function (normalizedConsumption) {
      total += normalizedConsumption;
    });
    return total;
  };

  RnrLineItem.prototype.calculateMaxStockQuantity = function () {
    if (!utils.isNumber(this.amc)) {
      this.maxStockQuantity = null;
      return;
    }
    this.maxStockQuantity = this.amc * this.maxMonthsOfStock;
  };

  RnrLineItem.prototype.calculateCalculatedOrderQuantity = function () {
    if (!utils.isNumber(this.maxStockQuantity) || !utils.isNumber(this.stockInHand)) {
      this.calculatedOrderQuantity = null;
      return;
    }

    this.stockInHand = utils.getValueFor(this.stockInHand);
    this.calculatedOrderQuantity = this.maxStockQuantity - this.stockInHand;
    if (this.calculatedOrderQuantity < 0) this.calculatedOrderQuantity = 0;
  };


  RnrLineItem.prototype.updateTotalLossesAndAdjustment = function (quantity, additive) {
    quantity = utils.parseIntWithBaseTen(quantity);
    if (utils.isNumber(quantity)) {
      if (additive) {
        this.totalLossesAndAdjustments += quantity;
      } else {
        this.totalLossesAndAdjustments -= quantity;
      }
    }
    this.fillConsumptionOrStockInHand();
  };

  //TODO : Does not belong to RnrLineItem
  RnrLineItem.prototype.getSource = function (indicator) {
    var code = null;
    $(this.programRnrColumnList).each(function (i, column) {
      if (column.indicator == indicator) {
        code = column.source.name;
        return false;
      }
    });
    return code;
  };

  RnrLineItem.prototype.formulaValid = function () {
    return !(this.stockInHand < 0 || this.quantityDispensed < 0 || this.arithmeticallyInvalid());
  };

  RnrLineItem.prototype.validateRequiredFieldsForNonFullSupply = function () {
    if (_.findWhere(programRnrColumnList, {name:'quantityRequested'}).visible) {
      return !(isUndefined(this.quantityRequested) || isUndefined(this.reasonForRequestedQuantity));
    }
    return false;
  };

  RnrLineItem.prototype.validateRequiredFieldsForFullSupply = function () {
    var valid = true;
    var rnrLineItem = this;
    var visibleColumns = _.where(programRnrColumnList, {"visible":true});

    $(visibleColumns).each(function (i, column) {
        var nonMandatoryColumns = ["reasonForRequestedQuantity", "remarks", "lossesAndAdjustments", "quantityApproved"];
        if (column.source.name != 'USER_INPUT' || _.contains(nonMandatoryColumns, column.name)) return;
        if (column.name == 'quantityRequested') {
          valid = isUndefined(rnrLineItem.quantityRequested) || !isUndefined(rnrLineItem.reasonForRequestedQuantity);
        } else if (column.name == 'expirationDate') {
          valid = !rnrLineItem.expirationDateInvalid();
        } else {
          valid = !isUndefined(rnrLineItem[column.name]);
        }
        return valid;
      }
    );

    return valid;
  };

  RnrLineItem.prototype.getErrorMessage = function () {
    if (this.stockInHand < 0) return "error.stock.on.hand.negative";
    if (this.quantityDispensed < 0) return "error.quantity.consumed.negative";
    if (this.arithmeticallyInvalid()) return "error.arithmetically.invalid";

    return "";
  };

  RnrLineItem.prototype.expirationDateInvalid = function () {
    var regExp = /^(0[1-9]|1[012])[/]((2)\d\d\d)$/;
    return !isUndefined(this.expirationDate) && !regExp.test(this.expirationDate);
  }

  RnrLineItem.prototype.validateForApproval = function () {
    return isUndefined(this.quantityApproved) ? false : true;
  };

  RnrLineItem.prototype.valid = function () {
    if (rnrStatus == 'IN_APPROVAL' || rnrStatus == 'AUTHORIZED') return this.validateForApproval();
    if (this.fullSupply) return this.validateRequiredFieldsForFullSupply() && this.formulaValid();
    return this.validateRequiredFieldsForNonFullSupply();
  };

  RnrLineItem.prototype.validateLossesAndAdjustments = function () {
    if (isUndefined(this.lossesAndAdjustments)) return true;

    return !(_.some(this.lossesAndAdjustments, function (lossAndAdjustment) {
      return !lossAndAdjustment.isQuantityValid();
    }));
  };

  RnrLineItem.prototype.compareTo = function (rnrLineItem) {
    function compareStrings(str1, str2) {
      if (str1 < str2) return -1
      if (str1 > str2) return 1;
      return 0;
    }

    if (isUndefined(rnrLineItem)) {
      return -1;
    }

    if (this == rnrLineItem) return 0;

    if (this.productCategoryDisplayOrder == rnrLineItem.productCategoryDisplayOrder) {
      if (this.productCategory == rnrLineItem.productCategory) {
        if(isUndefined(this.productDisplayOrder) && isUndefined(rnrLineItem.productDisplayOrder)) {
          return compareStrings(this.productCode, rnrLineItem.productCode);
        }

        if(this.productDisplayOrder== rnrLineItem.productDisplayOrder) {
          return compareStrings(this.productCode, rnrLineItem.productCode);
        }

        if (isUndefined(rnrLineItem.productDisplayOrder)) return -1;
        if (isUndefined(this.productDisplayOrder)) return 1;

        return this.productDisplayOrder - rnrLineItem.productDisplayOrder;
      }
      return compareStrings(this.productCategory, rnrLineItem.productCategory);
    }

    return this.productCategoryDisplayOrder - rnrLineItem.productCategoryDisplayOrder;
  };

  this.init();

  RnrLineItem.prototype.validateQuantityRequestedAndReason = function () {
    return (isUndefined(this.quantityRequested) || isUndefined(this.reasonForRequestedQuantity));
  };

  if (this.previousNormalizedConsumptions == undefined || this.previousNormalizedConsumptions == null)
    this.previousNormalizedConsumptions = [];

  if (this.lossesAndAdjustments == undefined) this.lossesAndAdjustments = [];

  this.reEvaluateTotalLossesAndAdjustments();
  this.fillConsumptionOrStockInHand();
};

RnrLineItem.visibleForNonFullSupplyColumns = ['product', 'productCode', 'dispensingUnit', 'quantityRequested', 'reasonForRequestedQuantity', 'packsToShip', 'price', 'cost', 'remarks'];

