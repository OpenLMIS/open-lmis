var RnrLineItem = function (lineItem) {

  jQuery.extend(true, this, lineItem);

  RnrLineItem.prototype.arithmeticallyInvalid = function (programRnRColumnList) {
    if (programRnRColumnList != undefined && programRnRColumnList[0].formulaValidationRequired) {
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
      var quantity = utils.parseIntWithBaseTen(lossAndAdjustmentObject.quantity);
      rnrLineItem.updateTotalLossesAndAdjustment(quantity, lossAndAdjustmentObject.type.additive);
    });
  };

  RnrLineItem.prototype.removeLossAndAdjustment = function (lossAndAdjustmentToDelete) {
    this.lossesAndAdjustments = $.grep(this.lossesAndAdjustments, function (lossAndAdjustmentObj) {
      return lossAndAdjustmentObj != lossAndAdjustmentToDelete;
    });
    var quantity = utils.parseIntWithBaseTen(lossAndAdjustmentToDelete.quantity);
    this.updateTotalLossesAndAdjustment(quantity, !lossAndAdjustmentToDelete.type.additive);
  };

  RnrLineItem.prototype.addLossAndAdjustment = function (newLossAndAdjustment) {
    var lossAndAdjustment = {"type":newLossAndAdjustment.type, "quantity":newLossAndAdjustment.quantity};

    newLossAndAdjustment.type = undefined;
    newLossAndAdjustment.quantity = undefined;

    this.lossesAndAdjustments.push(lossAndAdjustment);
    var quantity = utils.parseIntWithBaseTen(lossAndAdjustment.quantity);
    this.updateTotalLossesAndAdjustment(quantity, lossAndAdjustment.type.additive);
  };

  RnrLineItem.prototype.fillPacksToShipBasedOnCalculatedOrderQuantityOrQuantityRequested = function () {
    var orderQuantity = this.quantityRequested == null ?
        this.calculatedOrderQuantity : this.quantityRequested;
    this.calculatePacksToShip(orderQuantity);
  };

  // TODO: This function should encapsulate the logic to calculate packs to ship based on status
  RnrLineItem.prototype.calculatePacksToShip = function (quantity) {
    if (!utils.isNumber(quantity)) {
      this.packsToShip = null;
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
    this.cost = !utils.isNumber(this.packsToShip) ? 0 : parseFloat(this.packsToShip * this.price);
  };

  RnrLineItem.prototype.fillPacksToShipBasedOnApprovedQuantity = function () {
    this.calculatePacksToShip(this.quantityApproved);
  };

  RnrLineItem.prototype.updateCostWithApprovedQuantity = function (rnr) {
    this.fillPacksToShipBasedOnApprovedQuantity();
    this.calculateCost();
    rnr.fullSupplyItemsSubmittedCost = this.getTotalLineItemCost(rnr.lineItems);
  };

  RnrLineItem.prototype.calculateConsumption = function () {
    if (utils.isNumber(this.beginningBalance) && utils.isNumber(this.quantityReceived) && utils.isNumber(this.totalLossesAndAdjustments) && utils.isNumber(this.stockInHand)) {
      this.quantityDispensed = this.beginningBalance + this.quantityReceived + this.totalLossesAndAdjustments - this.stockInHand;
    } else {
      this.quantityDispensed = null;
    }
  };

  RnrLineItem.prototype.calculateStockInHand = function () {
    // TODO: check if calculated or user input
    if (utils.isNumber(this.beginningBalance) && utils.isNumber(this.quantityReceived) && utils.isNumber(this.quantityDispensed) && utils.isNumber(this.totalLossesAndAdjustments)) {
      this.stockInHand = this.beginningBalance + this.quantityReceived + this.totalLossesAndAdjustments - this.quantityDispensed;
    } else {
      this.stockInHand = null;
    }
  };

  RnrLineItem.prototype.calculateNormalizedConsumption = function (programRnRColumnList) {
    var numberOfMonthsInPeriod = 3; // will be picked up from the database in future
    this.stockOutDays = utils.getValueFor(this.stockOutDays);
    this.newPatientCount = utils.getValueFor(this.newPatientCount);
    if (this.getSource('F', programRnRColumnList) == null) this.newPatientCount = 0;

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
    this.amc = this.normalizedConsumption;
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

  RnrLineItem.prototype.calculateQuantityDispensedOrStockInHand = function (programRnRColumnList) {
    if (this.getSource('C', programRnRColumnList) == 'CALCULATED') this.fillConsumption();
    if (this.getSource('E', programRnRColumnList) == 'CALCULATED') this.fillStockInHand();
  }

  RnrLineItem.prototype.fillConsumption = function () {
    if (utils.isNumber(this.beginningBalance) && utils.isNumber(this.quantityReceived) && utils.isNumber(this.totalLossesAndAdjustments) && utils.isNumber(this.stockInHand)) {
      this.quantityDispensed = this.beginningBalance + this.quantityReceived + this.totalLossesAndAdjustments - this.stockInHand;
    } else {
      this.quantityDispensed = null;
    }
  };

  RnrLineItem.prototype.fillStockInHand = function () {
    if (utils.isNumber(this.beginningBalance) && utils.isNumber(this.quantityReceived) && utils.isNumber(this.quantityDispensed) && utils.isNumber(this.totalLossesAndAdjustments)) {
      this.stockInHand = this.beginningBalance + this.quantityReceived + this.totalLossesAndAdjustments - this.quantityDispensed;
    } else {
      this.stockInHand = null;
    }
  };

  RnrLineItem.prototype.fillNormalizedConsumption = function (programRnRColumnList) {
    var m = 3; // will be picked up from the database in future
    var x = utils.isNumber(this.stockOutDays) ? utils.parseIntWithBaseTen(this.stockOutDays) : null;
    var f = utils.isNumber(this.newPatientCount) ? utils.parseIntWithBaseTen(this.newPatientCount) : null;
    if (this.getSource('F', programRnRColumnList) == null) f = 0;

    if (!utils.isNumber(this.quantityDispensed) || !utils.isNumber(x) || !utils.isNumber(f)) {
      this.normalizedConsumption = null;
      return;
    }

    this.dosesPerMonth = utils.parseIntWithBaseTen(this.dosesPerMonth);
    var g = utils.parseIntWithBaseTen(this.dosesPerDispensingUnit);
    var consumptionAdjustedWithStockOutDays = ((m * 30) - x) == 0 ? this.quantityDispensed : (this.quantityDispensed * ((m * 30) / ((m * 30) - x)));
    var adjustmentForNewPatients = (f * Math.ceil(this.dosesPerMonth / g) ) * m;
    this.normalizedConsumption = Math.round(consumptionAdjustedWithStockOutDays + adjustmentForNewPatients);
  };

  RnrLineItem.prototype.fillAMC = function () {
    this.amc = this.normalizedConsumption;
  };

  RnrLineItem.prototype.fillMaxStockQuantity = function () {
    if (!utils.isNumber(this.amc)) {
      this.maxStockQuantity = null;
      return;
    }
    this.maxStockQuantity = this.amc * this.maxMonthsOfStock;
  };

  RnrLineItem.prototype.fillCalculatedOrderQuantity = function (programRnRColumnList) {
    if (!utils.isNumber(this.maxStockQuantity)) {
      this.calculatedOrderQuantity = null;
      return;
    }
    this.calculatedOrderQuantity = this.maxStockQuantity - (!utils.isNumber(this.stockInHand) ? 0 : this.stockInHand);
    this.calculatedOrderQuantity < 0 ? (this.calculatedOrderQuantity = 0) : 0;
  };

  RnrLineItem.prototype.fill = function (rnr, programRnRColumnList) {
    this.beginningBalance = utils.parseIntWithBaseTen(this.beginningBalance);
    this.quantityReceived = utils.parseIntWithBaseTen(this.quantityReceived);
    this.quantityDispensed = utils.parseIntWithBaseTen(this.quantityDispensed);
    this.totalLossesAndAdjustments = utils.parseIntWithBaseTen(this.totalLossesAndAdjustments);
    this.stockInHand = utils.parseIntWithBaseTen(this.stockInHand);

    this.calculateQuantityDispensedOrStockInHand(programRnRColumnList);
    this.fillNormalizedConsumption(programRnRColumnList);
    this.fillAMC();
    this.fillMaxStockQuantity();
    this.fillCalculatedOrderQuantity(programRnRColumnList);
    this.fillPacksToShipBasedOnCalculatedOrderQuantityOrQuantityRequested();
    this.calculateCost();
    this.calculateFullSupplyItemsSubmittedCost(rnr);
    this.calculateNonFullSupplyItemsSubmittedCost(rnr);
  };

  RnrLineItem.prototype.calculateNonFullSupplyItemsSubmittedCost = function (rnr) {
    rnr.nonFullSupplyItemsSubmittedCost = this.getTotalLineItemCost(rnr.nonFullSupplyLineItems);
  };

  RnrLineItem.prototype.calculateFullSupplyItemsSubmittedCost = function (rnr) {
    rnr.fullSupplyItemsSubmittedCost = this.getTotalLineItemCost(rnr.lineItems);
  };

  RnrLineItem.prototype.getTotalLineItemCost = function (rnrLineItems) {
    if (rnrLineItems == null) return;

    var cost = 0;
    for (var lineItemIndex in rnrLineItems) {
      var lineItem = rnrLineItems[lineItemIndex];
      if (!lineItem || lineItem.cost == null || !utils.isNumber(lineItem.cost)) continue;
      cost += lineItem.cost;
    }
    return cost;
  };

  RnrLineItem.prototype.updateTotalLossesAndAdjustment = function (quantity, additive) {
    if (utils.isNumber(quantity)) {
      if (additive) {
        this.totalLossesAndAdjustments += quantity;
      } else {
        this.totalLossesAndAdjustments -= quantity;
      }
    }
  };

  RnrLineItem.prototype.getSource = function (indicator, programRnRColumnList) {
    var code = null;
    $(programRnRColumnList).each(function (i, column) {
      if (column.indicator == indicator) {
        code = column.source.name;
        return false;
      }
    });
    return code;
  };

  RnrLineItem.prototype.getErrorMessage = function (programRnRColumnList) {
    if (this.stockInHand < 0) return 'Stock On Hand is calculated to be negative, please validate entries';
    if (this.quantityDispensed < 0) return 'Total Quantity Consumed is calculated to be negative, please validate entries';
    if (this.arithmeticallyInvalid(programRnRColumnList)) return 'The entries are arithmetically invalid, please recheck';

    return "";
  }
};
