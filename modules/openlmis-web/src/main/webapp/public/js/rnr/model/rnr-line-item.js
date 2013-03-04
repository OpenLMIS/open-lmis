var RnrLineItem = function (lineItem, rnr, programRnrColumnList) {
  jQuery.extend(true, this, lineItem);
  this.rnr = rnr;
  this.programRnrColumnList = programRnrColumnList;

  RnrLineItem.prototype.fillConsumptionOrStockInHand = function () {
    this.beginningBalance = utils.getValueFor(this.beginningBalance);
    this.quantityReceived = utils.getValueFor(this.quantityReceived);
    this.quantityDispensed = utils.getValueFor(this.quantityDispensed);
    this.totalLossesAndAdjustments = utils.getValueFor(this.totalLossesAndAdjustments, 0);
    this.stockInHand = utils.getValueFor(this.stockInHand);

    this.calculateConsumption();
    this.calculateStockInHand();
    this.fillNormalizedConsumption();
  };

  RnrLineItem.prototype.fillPacksToShipBasedOnCalculatedOrderQuantityOrQuantityRequested = function () {
    var orderQuantity = this.quantityRequested == null ?
      this.calculatedOrderQuantity : this.quantityRequested;
    this.calculatePacksToShip(orderQuantity);
    this.fillCost();
  };

  RnrLineItem.prototype.fillNormalizedConsumption = function () {
    this.calculateNormalizedConsumption();
    this.fillAMC();
  };

  RnrLineItem.prototype.fillCost = function () {
    this.calculateCost();
    if (this.fullSupply)
      this.calculateFullSupplyItemsSubmittedCost();
    else
      this.calculateNonFullSupplyItemsSubmittedCost();
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
    this.fillPacksToShipBasedOnCalculatedOrderQuantityOrQuantityRequested();
  };

  RnrLineItem.prototype.fillPacksToShipBasedOnApprovedQuantity = function () {
    var quantityApproved = !isUndefined(this.quantityApproved) && utils.isPositiveNumber(this.quantityApproved) ? this.quantityApproved : 0;

    this.calculatePacksToShip(quantityApproved);
    this.fillCost();
  };

  RnrLineItem.prototype.updateCostWithApprovedQuantity = function () {
    this.quantityApproved = utils.getValueFor(this.quantityApproved);
    this.fillPacksToShipBasedOnApprovedQuantity();
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
      return lossAndAdjustmentObj != lossAndAdjustmentToDelete;
    });
    this.updateTotalLossesAndAdjustment(lossAndAdjustmentToDelete.quantity, !lossAndAdjustmentToDelete.type.additive);
  };

  RnrLineItem.prototype.addLossAndAdjustment = function (newLossAndAdjustment) {
    var lossAndAdjustment = {"type":newLossAndAdjustment.type, "quantity":newLossAndAdjustment.quantity};

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
    if (quantity == 0 ) {
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

  RnrLineItem.prototype.rnrExists = function () {
    if (this.rnr == undefined || this.rnr == null)return false;
    return true;
  };

  RnrLineItem.prototype.calculateAMC = function () {
    if (!utils.isNumber(this.normalizedConsumption)) return;
    if (!this.rnrExists()) return;
    var numberOfMonthsInPeriod = this.rnr.period.numberOfMonths;
    var divider = numberOfMonthsInPeriod * (1 + this.previousNormalizedConsumptions.length);

    this.amc = Math.round((this.normalizedConsumption + this.sumOfPreviousNormalizedConsumptions()) / divider);
  };

  RnrLineItem.prototype.sumOfPreviousNormalizedConsumptions = function () {
    var total = 0;
    this.previousNormalizedConsumptions.forEach(function (normalizedConsumption) {
      total += normalizedConsumption;
    })
    return total;
  }

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

  RnrLineItem.prototype.calculateNonFullSupplyItemsSubmittedCost = function () {
    if (!this.rnrExists()) return;
    this.rnr.nonFullSupplyItemsSubmittedCost = this.addFloats(this.getTotalLineItemCost(this.rnr.nonFullSupplyLineItems), this.cost);
  };

  RnrLineItem.prototype.addFloats = function (cost1, cost2) {
    return (parseFloat(cost1) + parseFloat(cost2)).toFixed(2);
  };

  RnrLineItem.prototype.calculateFullSupplyItemsSubmittedCost = function () {
    if (!this.rnrExists()) return;
    this.rnr.fullSupplyItemsSubmittedCost = this.addFloats(this.getTotalLineItemCost(this.rnr.lineItems), this.cost);
  };

  RnrLineItem.prototype.getTotalLineItemCost = function (rnrLineItems) {
    if (rnrLineItems == null) return;

    var cost = 0;
    for (var lineItemIndex in rnrLineItems) {

      var lineItem = rnrLineItems[lineItemIndex];
      if (lineItem.productCode == this.productCode) continue;
      if (!lineItem || lineItem.cost == null || !utils.isNumber(lineItem.cost)) continue;
      cost += parseFloat(lineItem.cost);
    }
    return cost.toFixed(2);
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

  RnrLineItem.prototype.getErrorMessage = function () {
    if (this.stockInHand < 0) return 'Stock On Hand is calculated to be negative, please validate entries';
    if (this.quantityDispensed < 0) return 'Total Quantity Consumed is calculated to be negative, please validate entries';
    if (this.arithmeticallyInvalid()) return 'The entries are arithmetically invalid, please recheck';

    return "";
  }

  if (this.previousNormalizedConsumptions == undefined || this.previousNormalizedConsumptions == null)
    this.previousNormalizedConsumptions = [];

  if (this.lossesAndAdjustments == undefined) this.lossesAndAdjustments = [];

  if (this.rnr && this.rnr.status != 'IN_APPROVAL') {
    this.reEvaluateTotalLossesAndAdjustments();
    this.fillConsumptionOrStockInHand();
  }
};
