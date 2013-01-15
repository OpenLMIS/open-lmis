var RnrLineItem = function (lineItem) {

  this.rnrLineItem = lineItem;

  this.arithmeticallyInvalid = function (programRnRColumnList) {

    if (programRnRColumnList != undefined && programRnRColumnList[0].formulaValidated) {
      var beginningBalance = parseIntWithBaseTen(this.rnrLineItem.beginningBalance,10);
      var quantityReceived = parseIntWithBaseTen(this.rnrLineItem.quantityReceived);
      var quantityDispensed = parseIntWithBaseTen(this.rnrLineItem.quantityDispensed);
      var totalLossesAndAdjustments = parseIntWithBaseTen(this.rnrLineItem.totalLossesAndAdjustments);
      var stockInHand = parseIntWithBaseTen(this.rnrLineItem.stockInHand);
      return (isNumber(quantityDispensed) && isNumber(beginningBalance) && isNumber(quantityReceived) &&
              isNumber(totalLossesAndAdjustments) && isNumber(stockInHand)) ?
              quantityDispensed != (beginningBalance + quantityReceived + totalLossesAndAdjustments - stockInHand) : null;
    }
    return false;
  };

  function parseIntWithBaseTen(number){
   return parseInt(number, 10);
  }

  this.reEvaluateTotalLossesAndAdjustments = function () {
    var rnrLineItem = this.rnrLineItem;
    rnrLineItem.totalLossesAndAdjustments = 0;

    $(rnrLineItem.lossesAndAdjustments).each(function (index, lossAndAdjustmentObject) {
      var quantity = parseIntWithBaseTen(lossAndAdjustmentObject.quantity, 10);
      updateTotalLossesAndAdjustment(rnrLineItem, quantity, lossAndAdjustmentObject.type.additive);
    });
  };

  this.removeLossAndAdjustment = function (lossAndAdjustmentToDelete) {
    this.rnrLineItem.lossesAndAdjustments = $.grep(this.rnrLineItem.lossesAndAdjustments, function (lossAndAdjustmentObj) {
      return lossAndAdjustmentObj != lossAndAdjustmentToDelete;
    });
    var quantity = parseIntWithBaseTen(lossAndAdjustmentToDelete.quantity, 10);
    updateTotalLossesAndAdjustment(this.rnrLineItem, quantity, !lossAndAdjustmentToDelete.type.additive);
  };

  this.addLossAndAdjustment = function (newLossAndAdjustment) {
    var lossAndAdjustment = {"type":newLossAndAdjustment.type, "quantity":newLossAndAdjustment.quantity};
    newLossAndAdjustment.type = undefined;
    newLossAndAdjustment.quantity = undefined;
    this.rnrLineItem.lossesAndAdjustments.push(lossAndAdjustment);
    var quantity = parseIntWithBaseTen(lossAndAdjustment.quantity, 10);
    updateTotalLossesAndAdjustment(this.rnrLineItem, quantity, lossAndAdjustment.type.additive);
  };

  this.fill = function (rnr, programRnRColumnList) {
    var rnrLineItem = this.rnrLineItem;

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
      lineItem.normalizedConsumption = Math.round(consumptionAdjustedWithStockOutDays + adjustmentForNewPatients);
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
      if (!isNumber(rnrLineItem.maxStockQuantity) || !isNumber(rnrLineItem.stockInHand)) {
        rnrLineItem.calculatedOrderQuantity = null;
        return;
      }
      rnrLineItem.calculatedOrderQuantity = rnrLineItem.maxStockQuantity - rnrLineItem.stockInHand;
      rnrLineItem.calculatedOrderQuantity < 0 ? (rnrLineItem.calculatedOrderQuantity = 0) : 0;
    }

    function applyRoundingRules(orderQuantity) {
      var remainderQuantity = orderQuantity % parseIntWithBaseTen(rnrLineItem.packSize);
      var packsToShip = rnrLineItem.packsToShip;
      if (remainderQuantity >= rnrLineItem.packRoundingThreshold && packsToShip != 0) {
        packsToShip += 1;
      }

      if (packsToShip == 0 && rnrLineItem.roundToZero == false) {
        packsToShip = 1;
      }
      rnrLineItem.packsToShip = packsToShip;
    }

    function fillPacksToShip() {
      var packSize = parseIntWithBaseTen(rnrLineItem.packSize);
      var orderQuantity = rnrLineItem.quantityRequested == null ?
          rnrLineItem.calculatedOrderQuantity : rnrLineItem.quantityRequested;

      if (orderQuantity == null || !isNumber(orderQuantity)) {
        rnrLineItem.packsToShip = null;
        return;
      }
      rnrLineItem.packsToShip = Math.floor(orderQuantity / packSize);
      applyRoundingRules(orderQuantity);
    }

    function fillCost() {
      if (!isNumber(rnrLineItem.packsToShip)) {
        rnrLineItem.cost = null;
        return;
      }
      rnrLineItem.cost = parseFloat((rnrLineItem.packsToShip * rnrLineItem.price).toFixed(2));
    }

    function fillFullSupplyItemsSubmittedCost() {
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
    fillPacksToShip();
    fillCost();
    fillFullSupplyItemsSubmittedCost();
  };

  var updateTotalLossesAndAdjustment = function (rnrLineItem, quantity, additive) {
    if (!isNaN(quantity)) {
      if (additive) {
        rnrLineItem.totalLossesAndAdjustments += quantity;
      } else {
        rnrLineItem.totalLossesAndAdjustments -= quantity;
      }
    }
  };

  var isNumber = function (number) {
    return !isNaN(parseIntWithBaseTen(number));
  };

  this.getErrorMessage = function(programRnRColumnList){
    if(this.rnrLineItem.stockInHand < 0) return 'Stock On Hand is calculated to be negative, please validate entries';
    if(this.rnrLineItem.quantityDispensed < 0) return 'Total Quantity Consumed is calculated to be negative, please validate entries';
    if(this.arithmeticallyInvalid(programRnRColumnList)) return 'The entries are arithmetically invalid, please recheck';

    return "";
  }
};

