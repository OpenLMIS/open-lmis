var RnrLineItem = function () {

  RnrLineItem.prototype.arithmeticallyInvalid = function (programRnRColumnList) {

    if (programRnRColumnList != undefined && programRnRColumnList[0].formulaValidationRequired) {
      var beginningBalance = parseIntWithBaseTen(this.beginningBalance);
      var quantityReceived = parseIntWithBaseTen(this.quantityReceived);
      var quantityDispensed = parseIntWithBaseTen(this.quantityDispensed);
      var totalLossesAndAdjustments = parseIntWithBaseTen(this.totalLossesAndAdjustments);
      var stockInHand = parseIntWithBaseTen(this.stockInHand);
      return (isNumber(quantityDispensed) && isNumber(beginningBalance) && isNumber(quantityReceived) &&
        isNumber(totalLossesAndAdjustments) && isNumber(stockInHand)) ?
        quantityDispensed != (beginningBalance + quantityReceived + totalLossesAndAdjustments - stockInHand) : null;
    }
    return false;
  };

  function parseIntWithBaseTen(number) {
    return parseInt(number, 10);
  }

  RnrLineItem.prototype.reEvaluateTotalLossesAndAdjustments = function () {
    this.totalLossesAndAdjustments = 0;
    var rnrLineItem = this;
    $(this.lossesAndAdjustments).each(function (index, lossAndAdjustmentObject) {
      var quantity = parseIntWithBaseTen(lossAndAdjustmentObject.quantity);
      rnrLineItem.updateTotalLossesAndAdjustment(quantity, lossAndAdjustmentObject.type.additive);
    });
  };

  RnrLineItem.prototype.removeLossAndAdjustment = function (lossAndAdjustmentToDelete) {
    var rnrLineItem = this;
    this.lossesAndAdjustments = $.grep(this.lossesAndAdjustments, function (lossAndAdjustmentObj) {
      return lossAndAdjustmentObj != lossAndAdjustmentToDelete;
    });
    var quantity = parseIntWithBaseTen(lossAndAdjustmentToDelete.quantity);
    rnrLineItem.updateTotalLossesAndAdjustment(quantity, !lossAndAdjustmentToDelete.type.additive);
  };

  RnrLineItem.prototype.addLossAndAdjustment = function (newLossAndAdjustment) {
    var rnrLineItem = this;
    var lossAndAdjustment = {"type":newLossAndAdjustment.type, "quantity":newLossAndAdjustment.quantity};
    newLossAndAdjustment.type = undefined;
    newLossAndAdjustment.quantity = undefined;
    this.lossesAndAdjustments.push(lossAndAdjustment);
    var quantity = parseIntWithBaseTen(lossAndAdjustment.quantity);
    rnrLineItem.updateTotalLossesAndAdjustment(quantity, lossAndAdjustment.type.additive);
  };

  RnrLineItem.prototype.fillPacksToShipBasedOnCalculatedOrderQuantityOrQuantityRequested = function(){
    var orderQuantity = this.quantityRequested == null ?
      this.calculatedOrderQuantity : this.quantityRequested;
    this.calculatePacksToShip(orderQuantity);
  };

  RnrLineItem.prototype.calculatePacksToShip = function(quantity) {
    var packSize = parseIntWithBaseTen(this.packSize);
    if (quantity == null || !isNumber(quantity)) {
      this.packsToShip = null;
      return;
    }
    this.packsToShip = Math.floor(quantity / packSize);
    this.applyRoundingRules(quantity);
  };

  RnrLineItem.prototype.applyRoundingRules= function (orderQuantity) {
    var remainderQuantity = orderQuantity % parseIntWithBaseTen(this.packSize);
    var packsToShip = this.packsToShip;
    if (remainderQuantity >= this.packRoundingThreshold && packsToShip != 0) {
      packsToShip += 1;
    }

    if (packsToShip == 0 && this.roundToZero == false) {
      packsToShip = 1;
    }
    this.packsToShip = packsToShip;
  };

  RnrLineItem.prototype.fillCost = function () {
    this.cost = !isNumber(this.packsToShip) ? 0 : parseFloat(this.packsToShip * this.price);
  };

  RnrLineItem.prototype.fillPacksToShipBasedOnApprovedQuantity = function () {
    this.calculatePacksToShip(this.quantityApproved);
  };

  RnrLineItem.prototype.updateCostWithApprovedQuantity = function (rnr) {
    this.fillPacksToShipBasedOnApprovedQuantity();
    this.fillCost();
    fillFullSupplyItemsSubmittedCost(rnr);
  };

  RnrLineItem.prototype.fill = function (rnr, programRnRColumnList) {
    var rnrLineItem = this;

    function fillConsumption() {
      quantityDispensed = rnrLineItem.quantityDispensed = (isNumber(beginningBalance) && isNumber(quantityReceived) && isNumber(totalLossesAndAdjustments) && isNumber(stockInHand)) ? beginningBalance + quantityReceived + totalLossesAndAdjustments - stockInHand : null;
    }

    function fillStockInHand() {
      stockInHand = rnrLineItem.stockInHand = (isNumber(beginningBalance) && isNumber(quantityReceived) && isNumber(quantityDispensed) && isNumber(totalLossesAndAdjustments)) ? beginningBalance + quantityReceived + totalLossesAndAdjustments - quantityDispensed : null;
    }

    var getSource = function (indicator) {
      var code = null;
      $(programRnRColumnList).each(function (i, column) {
        if (column.indicator == indicator) {
          code = column.source.name;
          return false;
        }
      });
      return code;
    };

    function fillNormalizedConsumption() {
      var m = 3; // will be picked up from the database in future
      var x = isNumber(rnrLineItem.stockOutDays) ? parseIntWithBaseTen(rnrLineItem.stockOutDays) : null;
      var f = isNumber(rnrLineItem.newPatientCount) ? parseIntWithBaseTen(rnrLineItem.newPatientCount) : null;
      if (getSource('F') == null) f = 0;

      if (!isNumber(quantityDispensed) || !isNumber(x) || !isNumber(f)) {
        rnrLineItem.normalizedConsumption = null;
        return;
      }

      var dosesPerMonth = parseIntWithBaseTen(rnrLineItem.dosesPerMonth);
      var g = parseIntWithBaseTen(rnrLineItem.dosesPerDispensingUnit);
      var consumptionAdjustedWithStockOutDays = ((m * 30) - x) == 0 ? quantityDispensed : (quantityDispensed * ((m * 30) / ((m * 30) - x)));
      var adjustmentForNewPatients = (f * Math.ceil(dosesPerMonth / g) ) * m;
      rnrLineItem.normalizedConsumption = Math.round(consumptionAdjustedWithStockOutDays + adjustmentForNewPatients);
    }

    function fillAMC() {
      rnrLineItem.amc = rnrLineItem.normalizedConsumption;
    }

    function fillMaxStockQuantity() {
      if (!isNumber(rnrLineItem.amc)) {
        rnrLineItem.maxStockQuantity = null;
        return;
      }
      rnrLineItem.maxStockQuantity = rnrLineItem.amc * rnrLineItem.maxMonthsOfStock;
    }

    function fillCalculatedOrderQuantity() {
      if (!isNumber(rnrLineItem.maxStockQuantity)) {
        rnrLineItem.calculatedOrderQuantity = null;
        return;
      }
      rnrLineItem.calculatedOrderQuantity = rnrLineItem.maxStockQuantity - (!isNumber(rnrLineItem.stockInHand) ? 0 : rnrLineItem.stockInHand);
      rnrLineItem.calculatedOrderQuantity < 0 ? (rnrLineItem.calculatedOrderQuantity = 0) : 0;
    }

    var beginningBalance = parseIntWithBaseTen(rnrLineItem.beginningBalance);
    var quantityReceived = parseIntWithBaseTen(rnrLineItem.quantityReceived);
    var quantityDispensed = parseIntWithBaseTen(rnrLineItem.quantityDispensed);
    var totalLossesAndAdjustments = parseIntWithBaseTen(rnrLineItem.totalLossesAndAdjustments);
    var stockInHand = parseIntWithBaseTen(rnrLineItem.stockInHand);

    if (getSource('C') == 'CALCULATED') fillConsumption();
    if (getSource('E') == 'CALCULATED') fillStockInHand();
    fillNormalizedConsumption();
    fillAMC();
    fillMaxStockQuantity();
    fillCalculatedOrderQuantity();
    this.fillPacksToShipBasedOnCalculatedOrderQuantityOrQuantityRequested();
    this.fillCost();
    fillFullSupplyItemsSubmittedCost(rnr);
  };

  function fillFullSupplyItemsSubmittedCost(rnr) {
    if (rnr == null || rnr.lineItems == null) return;

    var cost = 0;
    var lineItems = rnr.lineItems;
    for (var lineItemIndex in lineItems) {
      var lineItem = lineItems[lineItemIndex];
      if (!lineItem || lineItem.cost == null || !isNumber(lineItem.cost)) continue;
      cost += lineItem.cost;
    }
    rnr.fullSupplyItemsSubmittedCost = cost;
  }

  RnrLineItem.prototype.updateTotalLossesAndAdjustment = function (quantity, additive) {
    if (!isNaN(quantity)) {
      if (additive) {
        this.totalLossesAndAdjustments += quantity;
      } else {
        this.totalLossesAndAdjustments -= quantity;
      }
    }
  };

  var isNumber = function (number) {
    return !isNaN(parseIntWithBaseTen(number));
  };

  RnrLineItem.prototype.getErrorMessage = function (programRnRColumnList) {
    if (this.stockInHand < 0) return 'Stock On Hand is calculated to be negative, please validate entries';
    if (this.quantityDispensed < 0) return 'Total Quantity Consumed is calculated to be negative, please validate entries';
    if (this.arithmeticallyInvalid(programRnRColumnList)) return 'The entries are arithmetically invalid, please recheck';

    return "";
  }

};

