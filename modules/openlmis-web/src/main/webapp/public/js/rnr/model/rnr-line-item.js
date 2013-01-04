var RnrLineItem = function (lineItem) {

    this.rnrLineItem = lineItem;

    this.arithmeticallyInvalid = function (programRnRColumnList) {

        if (programRnRColumnList!= undefined && programRnRColumnList[0].formulaValidated) {
            var a = parseInt(this.rnrLineItem.beginningBalance);
            var b = parseInt(this.rnrLineItem.quantityReceived);
            var c = parseInt(this.rnrLineItem.quantityDispensed);
            var d = parseInt(this.rnrLineItem.totalLossesAndAdjustments);
            var e = parseInt(this.rnrLineItem.stockInHand);
            return (isNumber(c) && isNumber(a) && isNumber(b) && isNumber(d) && isNumber(e)) ? c != (a + b - d - e) : null;
        }
        return false;
    }

    this.reEvaluateTotalLossesAndAdjustments = function () {
        var rnrLineItem = this.rnrLineItem;
        rnrLineItem.totalLossesAndAdjustments = 0;

        $(rnrLineItem.lossesAndAdjustments).each(function (index, lossAndAdjustmentObject) {
            var quantity = parseInt(lossAndAdjustmentObject.quantity, 10);
            updateTotalLossesAndAdjustment(rnrLineItem,quantity, lossAndAdjustmentObject.type.additive);
        });
    }

    this.removeLossAndAdjustment = function (lossAndAdjustmentToDelete) {
        this.rnrLineItem.lossesAndAdjustments = $.grep(this.rnrLineItem.lossesAndAdjustments, function (lossAndAdjustmentObj) {
            return lossAndAdjustmentObj != lossAndAdjustmentToDelete;
        });
        var quantity = parseInt(lossAndAdjustmentToDelete.quantity, 10);
        updateTotalLossesAndAdjustment(this.rnrLineItem, quantity, !lossAndAdjustmentToDelete.type.additive);
    };

    this.addLossAndAdjustment = function (newLossAndAdjustment) {
        var lossAndAdjustment = {"type":newLossAndAdjustment.type, "quantity":newLossAndAdjustment.quantity};
        newLossAndAdjustment.type = undefined;
        newLossAndAdjustment.quantity = undefined;
        this.rnrLineItem.lossesAndAdjustments.push(lossAndAdjustment);
        var quantity = parseInt(lossAndAdjustment.quantity, 10);
        updateTotalLossesAndAdjustment(this.rnrLineItem, quantity, lossAndAdjustment.type.additive);
    };

    this.fill = function (rnr, programRnRColumnList) {
        var rnrLineItem = this.rnrLineItem;

        function fillConsumption() {
            c = rnrLineItem.quantityDispensed = (isNumber(a) && isNumber(b) && isNumber(d) && isNumber(e)) ? a + b - d - e : null;
        }

        function fillStockInHand() {
            e = rnrLineItem.stockInHand = (isNumber(a) && isNumber(b) && isNumber(c) && isNumber(d)) ? a + b - d - c : null;
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
            var x = isNumber(rnrLineItem.stockOutDays) ? parseInt(rnrLineItem.stockOutDays) : null;
            var f = isNumber(rnrLineItem.newPatientCount) ? parseInt(rnrLineItem.newPatientCount) : null;
            if (getSource('F') == null) f = 0;

            if (!isNumber(c) || !isNumber(x) || !isNumber(f)) {
                rnrLineItem.normalizedConsumption = null;
                return;
            }

            var dosesPerMonth = parseInt(rnrLineItem.dosesPerMonth);
            var g = parseInt(rnrLineItem.dosesPerDispensingUnit);
            var consumptionAdjustedWithStockOutDays = ((m * 30) - x) == 0 ? c : (c * ((m * 30) / ((m * 30) - x)));
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
            var remainderQuantity = orderQuantity % parseInt(rnrLineItem.packSize);
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
            var packSize = parseInt(rnrLineItem.packSize);
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
            rnrLineItem.cost = rnrLineItem.packsToShip * rnrLineItem.price;
        }

        function fillFullSupplyItemsSubmittedCost() {
            if (rnr == null || rnr.lineItems == null) return;

            var cost = 0;
            var lineItems = rnr.lineItems;
            for (var lineItemIndex in lineItems) {
                var lineItem = lineItems[lineItemIndex];
                if (lineItem == null || lineItem.cost == null || !isNumber(lineItem.cost)) continue;
                cost += lineItem.cost;
            }
            rnr.fullSupplyItemsSubmittedCost = cost;
        }

        function fillTotalSubmittedCost() {
            if (rnr == null) return;

            var cost = 0;
            if (rnr.fullSupplyItemsSubmittedCost != null && isNumber(rnr.fullSupplyItemsSubmittedCost))
                cost += rnr.fullSupplyItemsSubmittedCost;
            if (rnr.nonFullSupplyItemsSubmittedCost != null && isNumber(rnr.nonFullSupplyItemsSubmittedCost))
                cost += rnr.nonFullSupplyItemsSubmittedCost;

            rnr.totalSubmittedCost = cost;
        }

        var a = parseInt(rnrLineItem.beginningBalance);
        var b = parseInt(rnrLineItem.quantityReceived);
        var c = parseInt(rnrLineItem.quantityDispensed);
        var d = parseInt(rnrLineItem.totalLossesAndAdjustments);
        var e = parseInt(rnrLineItem.stockInHand);

        if (getSource('C') == 'CALCULATED') fillConsumption();
        if (getSource('E') == 'CALCULATED') fillStockInHand();
        fillNormalizedConsumption();
        fillAMC();
        fillMaxStockQuantity();
        fillCalculatedOrderQuantity();
        fillPacksToShip();
        fillCost();
        fillFullSupplyItemsSubmittedCost();
        fillTotalSubmittedCost();
    }

    var updateTotalLossesAndAdjustment = function(rnrLineItem, quantity, additive) {
        if (!isNaN(quantity)) {
            if (additive) {
                rnrLineItem.totalLossesAndAdjustments += quantity;
            } else {
                rnrLineItem.totalLossesAndAdjustments -= quantity;
            }
        }
    }

    var isNumber = function (number) {
        return !isNaN(parseInt(number));
    };


};

