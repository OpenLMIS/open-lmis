var rnrModule = angular.module('rnr', ['openlmis']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/init-rnr', {controller:InitiateRnrController, templateUrl:'partials/init.html', resolve:InitiateRnrController.resolve}).
        when('/supervised/init-rnr', {controller:InitiateRnrController, templateUrl:'partials/supervised-init.html', resolve:InitiateSupervisedRnrController.resolve}).
        when('/create-rnr/:facility/:program', {templateUrl:'partials/create.html'}).
        otherwise({redirectTo:'/init-rnr'});
}]).directive('rnrValidator', function () {
        return {
            require:'?ngModel',
            link:function (scope, element, attrs, ctrl) {
                var validationFunction = rnrModule[attrs.rnrValidator];
                ctrl.$parsers.unshift(function (viewValue) {
                    if (validationFunction(viewValue, element.attr('name'))) {
                        ctrl.$setValidity(element.attr('name'), true);
                        ctrl.$setValidity('rnrError', true);
                        if (viewValue == "") viewValue = undefined;
                        return viewValue;
                    } else {
                        ctrl.$setValidity(element.attr('name'), false);
                        ctrl.$setValidity('rnrError', false);
                        return undefined;
                    }
                });
            }
        };
    });

rnrModule.positiveInteger = function (value, errorHolder) {
    var toggleErrorMessageDisplay = function (valid, errorHolder) {
        if (valid) {
            document.getElementById(errorHolder).style.display = 'none';
        } else {
            document.getElementById(errorHolder).style.display = 'block';
        }
    };

    var INTEGER_REGEXP = /^\d*$/;
    var valid = INTEGER_REGEXP.test(value);

    if (errorHolder != undefined) toggleErrorMessageDisplay(valid, errorHolder)

    return valid;
};

rnrModule.fill = function (lineItem, programRnrColumnList, rnr){

    function isNumber(number) {
            return !isNaN(parseInt(number));
    }

    function fillConsumption() {
        c = lineItem.quantityDispensed = (isNumber(a) && isNumber(b) && isNumber(d) && isNumber(e)) ? a + b - d - e : null;
    }

    function fillStockInHand() {
        e = lineItem.stockInHand = (isNumber(a) && isNumber(b) && isNumber(c) && isNumber(d)) ? a + b - d - c : null;
    }

    var getSource = function (indicator) {
        var code = null;
        $(programRnrColumnList).each(function (i, column) {
            if (column.indicator == indicator) {
                code = column.source.name;
                return false;
            }
        });
        return code;
    };

    function fillNormalizedConsumption() {
        var m = 3; // will be picked up from the database in future
        var x = isNumber(lineItem.stockOutDays) ? parseInt(lineItem.stockOutDays) : null;
        var f = isNumber(lineItem.newPatientCount) ? parseInt(lineItem.newPatientCount) : null;
        if (getSource('F') == null) f = 0;

        if (!isNumber(c) || !isNumber(x) || !isNumber(f)) {
            lineItem.normalizedConsumption = null;
            return;
        }

        var dosesPerMonth = parseInt(lineItem.dosesPerMonth);
        var g = parseInt(lineItem.dosesPerDispensingUnit);
        var consumptionAdjustedWithStockOutDays = ((m * 30) - x) == 0 ? c : (c * ((m * 30) / ((m * 30) - x)));
        var adjustmentForNewPatients = (f * Math.ceil(dosesPerMonth / g) ) * m;
        lineItem.normalizedConsumption = Math.round(consumptionAdjustedWithStockOutDays + adjustmentForNewPatients);
    }

    function fillAMC() {
        lineItem.amc = lineItem.normalizedConsumption;
    }

    function fillMaxStockQuantity() {
        if (!isNumber(lineItem.amc)) {
            lineItem.maxStockQuantity = null;
            return;
        }
        lineItem.maxStockQuantity = lineItem.amc * lineItem.maxMonthsOfStock;
    }

    function fillCalculatedOrderQuantity() {
        if (!isNumber(lineItem.maxStockQuantity) || !isNumber(lineItem.stockInHand)) {
            lineItem.calculatedOrderQuantity = null;
            return;
        }
        lineItem.calculatedOrderQuantity = lineItem.maxStockQuantity - lineItem.stockInHand;
        lineItem.calculatedOrderQuantity < 0 ? (lineItem.calculatedOrderQuantity = 0) : 0;
    }

    function applyRoundingRules(){
        var packsToShip = Math.round(lineItem.packsToShip);
        if(lineItem.packsToShip < 1 && lineItem.packsToShip > 0 && packsToShip == 0 && lineItem.roundToZero == false)
            packsToShip = 1;

        lineItem.packsToShip = packsToShip;
    }

    function fillPacksToShip() {
        var packSize = parseInt(lineItem.packSize);
        var orderQuantity = lineItem.quantityRequested == null?
                                lineItem.calculatedOrderQuantity : lineItem.quantityRequested;

        if(orderQuantity == null || !isNumber(orderQuantity)) {
            lineItem.packsToShip = null;
            return;
        }
        lineItem.packsToShip = orderQuantity/packSize;
        applyRoundingRules();
    }

    function fillCost() {
        if(!isNumber(lineItem.packsToShip)) {
            lineItem.cost = null;
            return;
        }
        lineItem.cost = lineItem.packsToShip * lineItem.price;
    }

    function fillFullSupplyItemsSubmittedCost() {
        if(rnr == null ||  rnr.lineItems == null) return;

        var cost = 0;
        var lineItems = rnr.lineItems;
        for(var lineItemIndex in lineItems){
            var lineItem = lineItems[lineItemIndex];
            if(lineItem == null || lineItem.cost == null || !isNumber(lineItem.cost)) continue;
            cost += lineItem.cost;
        }
        rnr.fullSupplyItemsSubmittedCost = cost;
    }

    function fillTotalSubmittedCost(){
        if(rnr == null) return;

        var cost = 0;
        if(rnr.fullSupplyItemsSubmittedCost != null && isNumber(rnr.fullSupplyItemsSubmittedCost))
            cost += rnr.fullSupplyItemsSubmittedCost;
        if(rnr.nonFullSupplyItemsSubmittedCost != null && isNumber(rnr.nonFullSupplyItemsSubmittedCost))
            cost += rnr.nonFullSupplyItemsSubmittedCost;

        rnr.totalSubmittedCost = cost;
    }

    var a = parseInt(lineItem.beginningBalance);
    var b = parseInt(lineItem.quantityReceived);
    var c = parseInt(lineItem.quantityDispensed);
    var d = parseInt(lineItem.lossesAndAdjustments);
    var e = parseInt(lineItem.stockInHand);

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
