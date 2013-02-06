var RnrLineItem = function (lineItem) {
  jQuery.extend(true, this, lineItem);

  RnrLineItem.prototype.fillConsumptionOrStockInHand = function (rnr, programRnrColumnList) {
    this.beginningBalance = utils.parseIntWithBaseTen(this.beginningBalance);
    this.quantityReceived = utils.parseIntWithBaseTen(this.quantityReceived);
    this.quantityDispensed = utils.parseIntWithBaseTen(this.quantityDispensed);
    this.totalLossesAndAdjustments = utils.parseIntWithBaseTen(this.totalLossesAndAdjustments);
    this.stockInHand = utils.parseIntWithBaseTen(this.stockInHand);

    this.calculateConsumption(programRnrColumnList);
    this.calculateStockInHand(programRnrColumnList);
    this.fillNormalizedConsumption(rnr, programRnrColumnList);
  };

  RnrLineItem.prototype.fillPacksToShipBasedOnCalculatedOrderQuantityOrQuantityRequested = function (rnr) {
    var orderQuantity = this.quantityRequested == null ?
      this.calculatedOrderQuantity : this.quantityRequested;
    this.calculatePacksToShip(orderQuantity);
    this.fillCost(rnr);
  };

  RnrLineItem.prototype.fillNormalizedConsumption = function (rnr, programRnrColumnList) {
    this.calculateNormalizedConsumption(programRnrColumnList);
    this.fillAMC(rnr);
  };

  RnrLineItem.prototype.fillCost = function (rnr) {
    this.calculateCost();
    if (this.fullSupply)
      this.calculateFullSupplyItemsSubmittedCost(rnr);
    else
      this.calculateNonFullSupplyItemsSubmittedCost(rnr);
  };

  RnrLineItem.prototype.fillAMC = function (rnr) {
    this.calculateAMC(rnr);
    this.fillMaxStockQuantity(rnr);
  };

  RnrLineItem.prototype.fillMaxStockQuantity = function (rnr) {
    this.calculateMaxStockQuantity();
    this.fillCalculatedOrderQuantity(rnr);
  };

  RnrLineItem.prototype.fillCalculatedOrderQuantity = function (rnr) {
    this.calculateCalculatedOrderQuantity();
    this.fillPacksToShipBasedOnCalculatedOrderQuantityOrQuantityRequested(rnr);
  };

  RnrLineItem.prototype.fillPacksToShipBasedOnApprovedQuantity = function (rnr) {
    this.calculatePacksToShip(this.quantityApproved);
    this.fillCost(rnr);
  };

  RnrLineItem.prototype.updateCostWithApprovedQuantity = function (rnr) {
    this.fillPacksToShipBasedOnApprovedQuantity(rnr);
    this.fillCost(rnr);
    rnr.fullSupplyItemsSubmittedCost = this.getTotalLineItemCost(rnr.lineItems);
  };

  RnrLineItem.prototype.arithmeticallyInvalid = function (programRnrColumnList) {
    if (programRnrColumnList != undefined && programRnrColumnList[0].formulaValidationRequired) {
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

  RnrLineItem.prototype.reEvaluateTotalLossesAndAdjustments = function (rnr, programRnrColumnList) {
    this.totalLossesAndAdjustments = 0;
    var rnrLineItem = this;
    $(this.lossesAndAdjustments).each(function (index, lossAndAdjustmentObject) {
      rnrLineItem.updateTotalLossesAndAdjustment(lossAndAdjustmentObject.quantity, lossAndAdjustmentObject.type.additive, rnr, programRnrColumnList);
    });
  };

  RnrLineItem.prototype.removeLossAndAdjustment = function (lossAndAdjustmentToDelete, rnr, programRnrColumnList) {
    this.lossesAndAdjustments = $.grep(this.lossesAndAdjustments, function (lossAndAdjustmentObj) {
      return lossAndAdjustmentObj != lossAndAdjustmentToDelete;
    });
    this.updateTotalLossesAndAdjustment(lossAndAdjustmentToDelete.quantity, !lossAndAdjustmentToDelete.type.additive, rnr, programRnrColumnList);
  };

  RnrLineItem.prototype.addLossAndAdjustment = function (newLossAndAdjustment, rnr, programRnrColumnList) {
    var lossAndAdjustment = {"type":newLossAndAdjustment.type, "quantity":newLossAndAdjustment.quantity};

    newLossAndAdjustment.type = undefined;
    newLossAndAdjustment.quantity = undefined;

    this.lossesAndAdjustments.push(lossAndAdjustment);
    this.updateTotalLossesAndAdjustment(lossAndAdjustment.quantity, lossAndAdjustment.type.additive, rnr, programRnrColumnList);
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

  RnrLineItem.prototype.calculateConsumption = function (programRnrColumnList) {
    if (this.getSource('C', programRnrColumnList) != 'CALCULATED') return;

    if (utils.isNumber(this.beginningBalance) && utils.isNumber(this.quantityReceived) && utils.isNumber(this.totalLossesAndAdjustments) && utils.isNumber(this.stockInHand)) {
      this.quantityDispensed = this.beginningBalance + this.quantityReceived + this.totalLossesAndAdjustments - this.stockInHand;
    } else {
      this.quantityDispensed = null;
    }
  };

  RnrLineItem.prototype.calculateStockInHand = function (programRnrColumnList) {
    if (this.getSource('E', programRnrColumnList) != 'CALCULATED') return;

    if (utils.isNumber(this.beginningBalance) && utils.isNumber(this.quantityReceived) && utils.isNumber(this.quantityDispensed) && utils.isNumber(this.totalLossesAndAdjustments)) {
      this.stockInHand = this.beginningBalance + this.quantityReceived + this.totalLossesAndAdjustments - this.quantityDispensed;
    } else {
      this.stockInHand = null;
    }
  };

  RnrLineItem.prototype.calculateNormalizedConsumption = function (programRnrColumnList) {
    var numberOfMonthsInPeriod = 3; // will be picked up from the database in future
    this.stockOutDays = utils.getValueFor(this.stockOutDays);
    this.newPatientCount = utils.getValueFor(this.newPatientCount);
    if (this.getSource('F', programRnrColumnList) == null) this.newPatientCount = 0;

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

  RnrLineItem.prototype.calculateAMC = function (rnr) {
    if (!utils.isNumber(this.normalizedConsumption)) return;

    var numberOfMonthsInPeriod = rnr.period.numberOfMonths;
    var divider = numberOfMonthsInPeriod*(1+this.previousNormalizedConsumptions.length);

    this.amc = Math.round((this.normalizedConsumption+this.sumOfPreviousNormalizedConsumptions())/divider);
  };

  RnrLineItem.prototype.sumOfPreviousNormalizedConsumptions = function() {
    if(this.previousNormalizedConsumptions == null || this.previousNormalizedConsumptions.length == 0) return 0;
    var total =0;
    this.previousNormalizedConsumptions.forEach(function(normalizedConsumption){
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

  RnrLineItem.prototype.updateTotalLossesAndAdjustment = function (quantity, additive, rnr, programRnrColumnList) {
    quantity = utils.parseIntWithBaseTen(quantity);
    if (utils.isNumber(quantity)) {
      if (additive) {
        this.totalLossesAndAdjustments += quantity;
      } else {
        this.totalLossesAndAdjustments -= quantity;
      }
    }
    this.fillConsumptionOrStockInHand(rnr, programRnrColumnList);
  };

  //TODO : Does not belong to RnrLineItem
  RnrLineItem.prototype.getSource = function (indicator, programRnrColumnList) {
    var code = null;
    $(programRnrColumnList).each(function (i, column) {
      if (column.indicator == indicator) {
        code = column.source.name;
        return false;
      }
    });
    return code;
  };

  RnrLineItem.prototype.getErrorMessage = function (programRnrColumnList) {
    if (this.stockInHand < 0) return 'Stock On Hand is calculated to be negative, please validate entries';
    if (this.quantityDispensed < 0) return 'Total Quantity Consumed is calculated to be negative, please validate entries';
    if (this.arithmeticallyInvalid(programRnrColumnList)) return 'The entries are arithmetically invalid, please recheck';

    return "";
  }
};
