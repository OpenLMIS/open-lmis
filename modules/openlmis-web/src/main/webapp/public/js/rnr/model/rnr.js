/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

var Rnr = function (rnr, programRnrColumns) {
  $.extend(true, this, rnr);
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
      errorMessage = "error.rnr.validation";

      return false;
    }

    function validateFormula(lineItem) {
      if (lineItem.formulaValid()) return true;
      errorMessage = "error.rnr.validation";

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
      errorMessage = "error.rnr.validation";

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
        error = 'error.rnr.validation';
        return false;
      }
    });
    return error;
  };

  Rnr.prototype.validateNonFullSupplyForApproval = function () {
    var error = '';
    $(this.nonFullSupplyLineItems).each(function (i, lineItem) {
      if (isUndefined(lineItem.quantityApproved)) {
        error = 'error.rnr.validation';
        return false;
      }
    });
    return error;
  };

  var calculateTotalCost = function (rnrLineItems) {
    if (rnrLineItems == null) return;

    var cost = 0;
    for (var lineItemIndex in rnrLineItems) {
      var lineItem = rnrLineItems[lineItemIndex];
      if (!lineItem || lineItem.cost == null || !utils.isNumber(lineItem.cost)) continue;
      cost += parseFloat(lineItem.cost);
    }
    return cost.toFixed(2);
  };


  Rnr.prototype.calculateFullSupplyItemsSubmittedCost = function () {
    this.fullSupplyItemsSubmittedCost = calculateTotalCost(this.fullSupplyLineItems);
  };

  Rnr.prototype.calculateNonFullSupplyItemsSubmittedCost = function () {
    this.nonFullSupplyItemsSubmittedCost = calculateTotalCost(this.nonFullSupplyLineItems);
  };


  Rnr.prototype.calculateTotalLineItemCost = function () {
   return  parseFloat(parseFloat(this.fullSupplyItemsSubmittedCost) + parseFloat(this.nonFullSupplyItemsSubmittedCost)).toFixed(2);
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


  Rnr.prototype.periodDisplayName = function () {
    var startDate = new Date(this.period.startDate);
    var endDate = new Date(this.period.endDate);
    return utils.getFormattedDate(startDate) + ' - ' + utils.getFormattedDate(endDate);
  };

  Rnr.prototype.reduceForApproval = function() {
    var rnr = _.pick(this, 'id', 'fullSupplyLineItems', 'nonFullSupplyLineItems');
    rnr.fullSupplyLineItems = _.map(rnr.fullSupplyLineItems, function(rnrLineItem){ return rnrLineItem.reduceForApproval() });
    rnr.nonFullSupplyLineItems = _.map(rnr.nonFullSupplyLineItems, function(rnrLineItem){ return rnrLineItem.reduceForApproval() });
    return rnr;
  };

  Rnr.prototype.init = function () {
    var thisRnr = this;

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
    this.nonFullSupplyLineItems.sort(function (lineItem1, lineItem2) {
      if (isUndefined(lineItem1)) return 1;
      return lineItem1.compareTo(lineItem2);
    });
    this.programRnrColumnList = programRnrColumns;

    this.calculateFullSupplyItemsSubmittedCost();
    this.calculateNonFullSupplyItemsSubmittedCost();
  };

  this.init();
};