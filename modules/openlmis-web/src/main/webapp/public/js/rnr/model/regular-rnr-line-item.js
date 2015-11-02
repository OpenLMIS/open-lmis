/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

var RegularRnrLineItem = base2.Base.extend({
  numberOfMonths: undefined,
  programRnrColumnList: undefined,
  rnrStatus: undefined,

  constructor: function (lineItem, numberOfMonths, programRnrColumnList, rnrStatus) {
    $.extend(true, this, lineItem);
    this.numberOfMonths = numberOfMonths;
    this.rnrStatus = rnrStatus;
    this.programRnrColumnList = programRnrColumnList;
    this.init();
    if (this.previousNormalizedConsumptions === undefined || this.previousNormalizedConsumptions === null)
      this.previousNormalizedConsumptions = [];

    if (this.lossesAndAdjustments === undefined) this.lossesAndAdjustments = [];

    this.reEvaluateTotalLossesAndAdjustments();
    this.fillConsumptionOrStockInHand();
  },

  initLossesAndAdjustments: function () {
    var tempLossesAndAdjustments = [];

    _.each(this.lossesAndAdjustments, function (lossAndAdjustmentJson) {
      tempLossesAndAdjustments.push(new LossAndAdjustment(lossAndAdjustmentJson));
    });

    this.lossesAndAdjustments = tempLossesAndAdjustments;
  },

  reduceForApproval: function () {
    return _.pick(this, 'id', 'skipped' , 'productCode', 'quantityApproved', 'remarks');
  },

  init: function () {
    this.initLossesAndAdjustments();
    if (this.previousNormalizedConsumptions === undefined || this.previousNormalizedConsumptions === null)
      this.previousNormalizedConsumptions = [];

    this.reEvaluateTotalLossesAndAdjustments();
    this.fillConsumptionOrStockInHand();
    this.calculateCost();
  },

  fillConsumptionOrStockInHand: function () {
    this.beginningBalance = utils.getValueFor(this.beginningBalance);
    this.quantityReceived = utils.getValueFor(this.quantityReceived);
    this.quantityDispensed = utils.getValueFor(this.quantityDispensed);
    this.totalLossesAndAdjustments = utils.getValueFor(this.totalLossesAndAdjustments, 0);
    this.stockInHand = utils.getValueFor(this.stockInHand);

    this.calculateConsumption();
    this.calculateStockInHand();
    this.fillNormalizedConsumption();
    this.calculateTotal();
  },

  statusBeforeAuthorized: function () {
    return this.rnrStatus === 'INITIATED' || this.rnrStatus === 'SUBMITTED';
  },

  fillPacksToShip: function () {
    this.quantityApproved = utils.getValueFor(this.quantityApproved);
    var orderQuantity;

    if (this.statusBeforeAuthorized()) orderQuantity =
        isUndefined(this.quantityRequested) ? this.calculatedOrderQuantity : this.quantityRequested;
    else orderQuantity = this.quantityApproved;

    this.calculatePacksToShip(orderQuantity);
    this.calculateCost();
  },

  fillNormalizedConsumption: function () {
    this.calculateNormalizedConsumption();
    this.fillPeriodNormalizedConsumption();
    this.fillAMC();
  },

  fillPeriodNormalizedConsumption: function () {
    if (isUndefined(this.normalizedConsumption)) {
      this.periodNormalizedConsumption = null;
      return;
    }
    this.periodNormalizedConsumption = this.normalizedConsumption * this.numberOfMonths;
  },

  fillAMC: function () {
    this.calculateAMC();
    this.fillMaxStockQuantity();
  },

  fillMaxStockQuantity: function () {
    this.calculateMaxStockQuantity();
    this.fillCalculatedOrderQuantity();
  },

  fillCalculatedOrderQuantity: function () {
    this.calculateCalculatedOrderQuantity();
    this.fillPacksToShip();
  },

  arithmeticallyInvalid: function () {
    if (this.programRnrColumnList !== undefined && this.programRnrColumnList[0].formulaValidationRequired) {
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
  },

  reEvaluateTotalLossesAndAdjustments: function () {
    this.totalLossesAndAdjustments = 0;
    var rnrLineItem = this;
    $(this.lossesAndAdjustments).each(function (index, lossAndAdjustmentObject) {
      rnrLineItem.updateTotalLossesAndAdjustment(lossAndAdjustmentObject.quantity,
          lossAndAdjustmentObject.type.additive);
    });
  },

  removeLossAndAdjustment: function (lossAndAdjustmentToDelete) {
    this.lossesAndAdjustments = $.grep(this.lossesAndAdjustments, function (lossAndAdjustmentObj) {
      return !(lossAndAdjustmentToDelete.equals(lossAndAdjustmentObj));
    });
    this.updateTotalLossesAndAdjustment(lossAndAdjustmentToDelete.quantity, !lossAndAdjustmentToDelete.type.additive);
  },

  addLossAndAdjustment: function (newLossAndAdjustment) {
    var lossAndAdjustment = new LossAndAdjustment(newLossAndAdjustment);

    newLossAndAdjustment.type = undefined;
    newLossAndAdjustment.quantity = undefined;

    this.lossesAndAdjustments.push(lossAndAdjustment);
    this.updateTotalLossesAndAdjustment(lossAndAdjustment.quantity, lossAndAdjustment.type.additive);
  },

  calculatePacksToShip: function (quantity) {
    if (!utils.isNumber(quantity)) {
      this.packsToShip = null;
      return;
    }
    if (quantity === 0) {
      this.packsToShip = this.roundToZero ? 0 : 1;
      return;
    }
    this.packsToShip = Math.floor(quantity / utils.parseIntWithBaseTen(this.packSize));
    this.applyRoundingRulesToPacksToShip(quantity);
  },

  applyRoundingRulesToPacksToShip: function (orderQuantity) {
    var remainderQuantity = orderQuantity % utils.parseIntWithBaseTen(this.packSize);

    if (remainderQuantity >= this.packRoundingThreshold)
      this.packsToShip += 1;

    if (this.packsToShip === 0 && !this.roundToZero)
      this.packsToShip = 1;
  },

  calculateCost: function () {
    this.cost = !utils.isNumber(this.packsToShip) ? 0 : parseFloat(this.packsToShip * this.price).toFixed(2);
  },

  calculateTotal: function () {
    if (utils.isNumber(this.beginningBalance) && utils.isNumber(this.quantityReceived)) {
      this.total = this.beginningBalance + this.quantityReceived;
    }
    else {
      this.total = null;
    }
  },

  calculateConsumption: function () {
    if (this.rnrStatus === 'AUTHORIZED' || this.rnrStatus === 'IN_APPROVAL' || this.rnrStatus === 'APPROVED' || this.rnrStatus === 'RELEASED') {
      return;
    }
    if (this.getSource('quantityDispensed') !== 'CALCULATED') return;

    if (utils.isNumber(this.beginningBalance) && utils.isNumber(this.quantityReceived) && utils.isNumber(this.totalLossesAndAdjustments) && utils.isNumber(this.stockInHand)) {
      this.quantityDispensed = this.beginningBalance + this.quantityReceived + this.totalLossesAndAdjustments - this.stockInHand;
    } else {
      this.quantityDispensed = null;
    }
  },

  calculateStockInHand: function () {
    if (this.rnrStatus === 'AUTHORIZED' || this.rnrStatus === 'IN_APPROVAL' || this.rnrStatus === 'APPROVED' || this.rnrStatus === 'RELEASED') {
      return;
    }
    if (this.getSource('stockInHand') !== 'CALCULATED') return;

    if (utils.isNumber(this.beginningBalance) && utils.isNumber(this.quantityReceived) && utils.isNumber(this.quantityDispensed)) {
      this.stockInHand = this.beginningBalance + this.quantityReceived + this.totalLossesAndAdjustments - this.quantityDispensed;
    } else {
      this.stockInHand = null;
    }
  },

  getCalcOption:function(columnName){
      var colOption = null;
      angular.forEach(this.programRnrColumnList, function(item){
          if(item.name == columnName) {
              colOption = item.calculationOption;
          }
      });
      return colOption;
  },

  calculateNormalizedConsumption: function () {
    if (this.rnrStatus === 'AUTHORIZED' || this.rnrStatus === 'IN_APPROVAL' || this.rnrStatus === 'APPROVED' || this.rnrStatus === 'RELEASED') {
      return;
    }
    this.reportingDays = utils.getValueFor(this.reportingDays);
    this.stockOutDays = utils.getValueFor(this.stockOutDays);
    this.newPatientCount = utils.getValueFor(this.newPatientCount);
    if (this.getSource('newPatientCount') === null) this.newPatientCount = 0;

    
    // find the calculation option
    var normalizedConsumptionCalcOption = this.getCalcOption("normalizedConsumption");
    if(normalizedConsumptionCalcOption === 'DISPENSED_PLUS_NEW_PATIENTS'){
      this.normalizedConsumption = this.quantityDispensed  + this.newPatientCount;
    } else if(normalizedConsumptionCalcOption === "DISPENSED_X_90") {
      if(this.stockOutDays < 90){
        this.normalizedConsumption = Math.round((90 * this.quantityDispensed) / (90 - this.stockOutDays));
      }else{
        this.normalizedConsumption = Math.round(90 * this.quantityDispensed);
      }
    } else {
      // this is the default behavior
      if (!utils.isNumber(this.quantityDispensed) || !utils.isNumber(this.stockOutDays) || !utils.isNumber(this.newPatientCount)) {
        this.normalizedConsumption = null;
        return;
      }
      this.dosesPerMonth = utils.parseIntWithBaseTen(this.dosesPerMonth);
      var dosesPerDispensingUnit = utils.parseIntWithBaseTen(this.dosesPerDispensingUnit);
      dosesPerDispensingUnit = Math.max(dosesPerDispensingUnit, 1);
      var consumptionAdjustedWithStockOutDays = ((this.reportingDays) - this.stockOutDays) <= 0 ?
          this.quantityDispensed :
          ((this.quantityDispensed * 30) / ((this.reportingDays) - this.stockOutDays));
      var adjustmentForNewPatients;
      if (_.findWhere(this.programRnrColumnList, {name: 'newPatientCount'}).configuredOption.name === "newPatientCount") {
        adjustmentForNewPatients = (this.newPatientCount * Math.round(this.dosesPerMonth / dosesPerDispensingUnit) );
      } else {
        adjustmentForNewPatients = this.newPatientCount;
      }
      this.normalizedConsumption = Math.round(consumptionAdjustedWithStockOutDays + adjustmentForNewPatients);
    }
  },

  calculateAMC: function () {
    if (!utils.isNumber(this.normalizedConsumption)) {
      this.amc = null;
      return;
    }
    var divider = (1 + this.previousNormalizedConsumptions.length);

    this.amc = Math.round((this.normalizedConsumption + this.sumOfPreviousNormalizedConsumptions()) / divider);
  },

  sumOfPreviousNormalizedConsumptions: function () {
    var total = 0;
    this.previousNormalizedConsumptions.forEach(function (normalizedConsumption) {
      total += normalizedConsumption;
    });
    return total;
  },

  calculateMaxStockQuantity: function () {
    if (!utils.isNumber(this.amc)) {
      this.maxStockQuantity = null;
      return;
    }
    // find the calculation option
    var maxStockColumnCalculationOption = this.getCalcOption('maxStockQuantity');

    // if not default, apply the formula
    if( maxStockColumnCalculationOption === 'CONSUMPTION_X_2'){
      this.maxStockQuantity = this.normalizedConsumption * 2;
    }else if( maxStockColumnCalculationOption === 'DISPENSED_X_2'){
      this.maxStockQuantity = this.quantityDispensed * 2;
    }
    else {
      // if default, do what you used to do
      this.maxStockQuantity = this.amc * this.maxMonthsOfStock;
    }
  },

  calculateCalculatedOrderQuantity: function () {
    if (!utils.isNumber(this.maxStockQuantity) || !utils.isNumber(this.stockInHand)) {
      this.calculatedOrderQuantity = null;
      return;
    }

    this.stockInHand = utils.getValueFor(this.stockInHand);
    this.calculatedOrderQuantity = this.maxStockQuantity - this.stockInHand;
    if (this.calculatedOrderQuantity < 0) this.calculatedOrderQuantity = 0;
  },


  updateTotalLossesAndAdjustment: function (quantity, additive) {
    quantity = utils.parseIntWithBaseTen(quantity);
    if (utils.isNumber(quantity)) {
      if (additive) {
        this.totalLossesAndAdjustments += quantity;
      } else {
        this.totalLossesAndAdjustments -= quantity;
      }
    }
    this.fillConsumptionOrStockInHand();
  },

//TODO : Does not belong to RnrLineItem
  getSource: function (name) {
    var code = null;
    $(this.programRnrColumnList).each(function (i, column) {
      if (column.name == name) {
        code = column.source.name;
        return false;
      }
    });
    return code;
  },

  formulaValid: function () {
    return !(this.stockInHand < 0 || this.quantityDispensed < 0 || this.arithmeticallyInvalid());
  },
  canSkip : function(){
    var rnrLineItem = this;
    var visibleColumns = ['beginningBalance','quantityReceived','quantityDispensed','stockInHand','quantityRequested'];
    var skip = true;
    $(visibleColumns).each(function (i, column) {
      if(!isUndefined(rnrLineItem[column]) && rnrLineItem[column] !== 0 ){
        skip = false;
      }
    });
    return skip;
  },

  validateRequiredFieldsForNonFullSupply: function () {
    if (_.findWhere(this.programRnrColumnList, {name: 'quantityRequested'}).visible) {
      return !(isUndefined(this.quantityRequested) || isUndefined(this.reasonForRequestedQuantity));
    }
    return false;
  },

  validateRequiredFieldsForFullSupply: function () {
    var valid = true;
    var rnrLineItem = this;
    var visibleColumns = _.where(this.programRnrColumnList, {"visible": true});

    $(visibleColumns).each(function (i, column) {
          var nonMandatoryColumns = ["reasonForRequestedQuantity", "remarks", "lossesAndAdjustments", "quantityApproved", "skipped"];
          if (column.source.name != 'USER_INPUT' || _.contains(nonMandatoryColumns, column.name)) return;
          if (column.name === 'quantityRequested') {
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
  },

  getErrorMessage: function () {
    if (this.skipped) return "";
    if (this.stockInHand < 0) return "error.stock.on.hand.negative";
    if (this.quantityDispensed < 0) return "error.quantity.consumed.negative";
    if (this.arithmeticallyInvalid()) return "error.arithmetically.invalid";

    return "";
  },

  expirationDateInvalid: function () {
    var regExp = /^(0[1-9]|1[012])[/]((2)\d\d\d)$/;
    return !isUndefined(this.expirationDate) && !regExp.test(this.expirationDate);
  },

  validateForApproval: function () {
    return isUndefined(this.quantityApproved) ? false : true;
  },

  valid: function () {
    if (this.skipped) return true;
    if (this.rnrStatus == 'IN_APPROVAL' || this.rnrStatus == 'AUTHORIZED') return this.validateForApproval();
    if (this.fullSupply) return this.validateRequiredFieldsForFullSupply() && this.formulaValid();
    return this.validateRequiredFieldsForNonFullSupply();
  },

  validateLossesAndAdjustments: function () {
    if (isUndefined(this.lossesAndAdjustments)) return true;

    return !(_.some(this.lossesAndAdjustments, function (lossAndAdjustment) {
      return !lossAndAdjustment.isQuantityValid();
    }));
  },

  compareTo: function (rnrLineItem) {
    function compareStrings(str1, str2) {
      if (str1 < str2) return -1;
      if (str1 > str2) return 1;
      return 0;
    }

    if (isUndefined(rnrLineItem)) {
      return -1;
    }

    if (this === rnrLineItem) return 0;

    if (this.productCategoryDisplayOrder == rnrLineItem.productCategoryDisplayOrder) {
      if (this.productCategory === rnrLineItem.productCategory) {
        if (isUndefined(this.productDisplayOrder) && isUndefined(rnrLineItem.productDisplayOrder)) {
          return compareStrings(this.productCode, rnrLineItem.productCode);
        }

        if (this.productDisplayOrder === rnrLineItem.productDisplayOrder) {
          return compareStrings(this.productCode, rnrLineItem.productCode);
        }

        if (isUndefined(rnrLineItem.productDisplayOrder)) return -1;
        if (isUndefined(this.productDisplayOrder)) return 1;

        return this.productDisplayOrder - rnrLineItem.productDisplayOrder;
      }
      return compareStrings(this.productCategory, rnrLineItem.productCategory);
    }

    return this.productCategoryDisplayOrder - rnrLineItem.productCategoryDisplayOrder;
  },

  validateQuantityRequestedAndReason: function () {
    return (isUndefined(this.quantityRequested) || isUndefined(this.reasonForRequestedQuantity));
  }
}, {
  visibleForNonFullSupplyColumns: ['dispensingUnit', 'quantityRequested', 'quantityApproved', 'reasonForRequestedQuantity', 'packsToShip', 'price', 'cost', 'remarks'],
  frozenColumns: ['skipped', 'product', 'productCode']
});

