var Rnr = function (rnr, programRnrColumns) {
  $.extend(true, this, rnr);
  this.programRnrColumns = programRnrColumns;
  var thisRnr = this;

  var getInvalidLineItemIndexes = function (lineItems) {
    var errorLineItems = [];
    $(lineItems).each(function (i, lineItem) {
      if (!lineItem.valid()) errorLineItems.push(i);
    });
    return errorLineItems;
  };

  Rnr.prototype.getFullSupplyErrorLineItemIndexes = function () {
    return getInvalidLineItemIndexes(this.fullSupplyLineItems);
  };

  Rnr.prototype.getNonFullSupplyErrorLineItemIndexes = function () {
    return getInvalidLineItemIndexes(this.nonFullSupplyLineItems);
  };

  Rnr.prototype.getErrorPages = function (pageSize) {
      function getErrorPages(lineItems) {
        var pagesWithErrors = [];
        $(lineItems).each(function (i, lineItem) {
          pagesWithErrors.push(Math.ceil((lineItem + 1) / pageSize));
        });
        return _.uniq(pagesWithErrors, true);
      }

      function getFullSupplyPagesWithError() {
        var fullSupplyErrorLIneItems = thisRnr.getFullSupplyErrorLineItemIndexes();
        return getErrorPages(fullSupplyErrorLIneItems);
      }

      function getNonFullSupplyPagesWithError() {
        var nonFullSupplyErrorLIneItems = thisRnr.getNonFullSupplyErrorLineItemIndexes();
        return getErrorPages(nonFullSupplyErrorLIneItems)
      }

      var errorPages = {};
      errorPages.fullSupply = getFullSupplyPagesWithError();
      errorPages.nonFullSupply = getNonFullSupplyPagesWithError();
      return errorPages;
  };

  Rnr.prototype.validateFullSupply = function () {
    var errorMessage = "";

    function validateRequiredFields(lineItem) {
      if (lineItem.validateRequiredFieldsForFullSupply()) return true;
      errorMessage = "rnr.validation.error";

      return false;
    }

    function validateFormula(lineItem) {
      if (lineItem.formulaValid()) return true;
      errorMessage = "rnr.validation.error";

      return false;
    }

    $(this.fullSupplyLineItems).each(function (i, lineItem) {
      if (!validateRequiredFields(lineItem)) return false;
      if (!validateFormula(lineItem)) return false;
    });
    return errorMessage;
  };

  Rnr.prototype.validateNonFullSupply = function () {
    var errorMessage = "";

    var validateRequiredFields = function (lineItem) {
      if (lineItem.validateRequiredFieldsForNonFullSupply()) return true;
      errorMessage = "rnr.validation.error";

      return false;
    };

    $(this.nonFullSupplyLineItems).each(function (i, lineItem) {
      if (!validateRequiredFields(lineItem)) return false;
    });
    return errorMessage;
  };

  Rnr.prototype.validateFullSupplyForApproval = function () {
    var error = '';
    $(this.fullSupplyLineItems).each(function (i, lineItem) {
      if (isUndefined(lineItem.quantityApproved)) {
        error = 'rnr.validation.error';
        return false;
      }
    });
    return error;
  };

  Rnr.prototype.validateNonFullSupplyForApproval = function () {
    var error = '';
    $(this.nonFullSupplyLineItems).each(function (i, lineItem) {
      if (isUndefined(lineItem.quantityApproved)) {
        error = 'rnr.validation.error';
        return false;
      }
    });
    return error;
  };

  Rnr.prototype.calculateFullSupplyItemsSubmittedCost = function () {
    this.fullSupplyItemsSubmittedCost = this.getTotalLineItemsCost(this.fullSupplyLineItems);
  };

  Rnr.prototype.calculateNonFullSupplyItemsSubmittedCost = function () {
    this.nonFullSupplyItemsSubmittedCost = this.getTotalLineItemsCost(this.nonFullSupplyLineItems);
  };

  Rnr.prototype.getTotalLineItemsCost = function (rnrLineItems) {
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

  Rnr.prototype.fillCost = function (isFullSupply) {
    if (isFullSupply)
      this.calculateFullSupplyItemsSubmittedCost();
    else
      this.calculateNonFullSupplyItemsSubmittedCost();
  };

  Rnr.prototype.fillConsumptionOrStockInHand = function (rnrLineItem) {
    rnrLineItem.fillConsumptionOrStockInHand();
    this.fillCost(rnrLineItem.fullSupply);
  };

  Rnr.prototype.fillNormalizedConsumption = function (rnrLineItem) {
    rnrLineItem.fillNormalizedConsumption();
    this.fillCost(rnrLineItem.fullSupply);
  };

  Rnr.prototype.fillPacksToShip = function (rnrLineItem) {
    rnrLineItem.fillPacksToShip();
    this.fillCost(rnrLineItem.fullSupply);
  };

  Rnr.prototype.init = function () {
    function prepareLineItems(lineItems) {
      var lineItemsJson = lineItems;
      lineItems = [];
      $(lineItemsJson).each(function (i, lineItem) {
        lineItems.push(new RnrLineItem(lineItem, thisRnr.period.numberOfMonths, programRnrColumns, thisRnr.status))
      });

      return lineItems;
    }

    this.fullSupplyLineItems = prepareLineItems(this.fullSupplyLineItems);
    this.nonFullSupplyLineItems = prepareLineItems(this.nonFullSupplyLineItems);
    this.programRnrColumnList = programRnrColumns;

    this.calculateFullSupplyItemsSubmittedCost();
    this.calculateNonFullSupplyItemsSubmittedCost();
  };

  this.init();
};