/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

var Rnr = function (rnr, programRnrColumns, numberOfMonths, operationalStatuses) {
  
  // separate the skipped products from the not so skipped. 
  rnr.allSupplyLineItems = rnr.fullSupplyLineItems;
  if(rnr.program && rnr.program.hideSkippedProducts){
    rnr.skippedLineItems = _.where(rnr.allSupplyLineItems, { skipped:true});
    rnr.fullSupplyLineItems =  _.where(rnr.allSupplyLineItems, {skipped: false});
  }
  rnr.operationalStatusList = operationalStatuses;

  $.extend(true, this, rnr);
  var thisRnr = this;
  this.skipAll = false;
  this.numberOfMonths = numberOfMonths;

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

  Rnr.prototype.getRegimenErrorLineItemIndexes = function () {

    var errorLineItems = [];
    $(this.regimenLineItems).each(function (i, lineItem) {
      if(lineItem.hasError){
        errorLineItems.push(i);
      }
    });
    return errorLineItems;
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
      return getErrorPages(nonFullSupplyErrorLIneItems);
    }

    function getRegimenPagesWithError(){
      var regimenErrorLineItems = thisRnr.getRegimenErrorLineItemIndexes();
      return getErrorPages(regimenErrorLineItems);
    }

    var errorPages = {};
    errorPages.fullSupply = getFullSupplyPagesWithError();
    errorPages.nonFullSupply = getNonFullSupplyPagesWithError();
    errorPages.regimen = getRegimenPagesWithError();
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

    function validateEquipmentStatus(lineItem){
      lineItem.isEquipmentValid = true;
      if(lineItem.equipments !== undefined && ((lineItem.calculatedOrderQuantity > 0 && lineItem.quantityRequested !== 0) || lineItem.quantityRequested > 0 )){
        for(var i = 0; i < lineItem.equipments.length; i++){
          var status = _.findWhere(this.operationalStatusList, {'id': lineItem.equipments[i].operationalStatusId});
          if( statis !== undefined && status.isBad === true && (lineItem.equipments[i].remarks === '' || lineItem.equipments[i].remarks === undefined)){
            lineItem.isEquipmentValid = false;
          }
        }
      }
      return true;
    }
    this.equipmentErrorMessage = "";
    $(this.fullSupplyLineItems).each(function (i, lineItem) {
      if (lineItem.skipped)
        return;
      if (!validateRequiredFields(lineItem))
        return false;
      if (!validateFormula(lineItem))
        return false;
      if (!validateEquipmentStatus(lineItem))
        return false;
    });
    return errorMessage;
  };

  Rnr.prototype.validateEquipments = function(){
    var errorMessage = null;
    $(this.equipmentLineItems).each(function(i,lineItem){
      if(lineItem.operationalStatusId === 3 && lineItem.remarks === undefined || lineItem.remarks === ''){
        lineItem.IsRemarkRequired = true;
        errorMessage = 'Remarks are required for equipments that are not operational';
      }
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
      if (!lineItem.skipped && isUndefined(lineItem.quantityApproved)) {
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
    if (rnrLineItems === null) return;

    var cost = 0;
    for (var lineItemIndex in rnrLineItems) {
      var lineItem = rnrLineItems[lineItemIndex];
      if (utils.isNumber(lineItem.cost) && !lineItem.skipped) {
        cost += parseFloat(lineItem.cost);
      }
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
    var cost = parseFloat(parseFloat(this.fullSupplyItemsSubmittedCost) + parseFloat(this.nonFullSupplyItemsSubmittedCost)).toFixed(2);
    if (this.allocatedBudget && this.program.budgetingApplies) {
      this.costExceedsBudget = this.allocatedBudget < cost;
    }
    return cost;
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
    return this.period.stringStartDate + ' - ' + this.period.stringEndDate;
  };

  Rnr.prototype.reduceForApproval = function () {
    var rnr = _.pick(this, 'id', 'fullSupplyLineItems', 'nonFullSupplyLineItems');
    rnr.fullSupplyLineItems = _.map(rnr.fullSupplyLineItems, function (rnrLineItem) {
      return rnrLineItem.reduceForApproval();
    });
    rnr.nonFullSupplyLineItems = _.map(rnr.nonFullSupplyLineItems, function (rnrLineItem) {
      return rnrLineItem.reduceForApproval();
    });
    return rnr;
  };

  Rnr.prototype.initEquipments = function(){

    for(var i= 0; this.equipmentLineItems !== undefined && i < this.equipmentLineItems.length; i++){
      var eqli = this.equipmentLineItems[i];
      for(var j = 0;eqli.relatedProducts !== undefined && j < eqli.relatedProducts.length;j++){
        var prod = eqli.relatedProducts[j];
        var lineItem = _.findWhere(this.fullSupplyLineItems, {productCode: prod.code});
        if(lineItem !== null && lineItem.equipments === undefined){
            lineItem.equipments = [];
        }else if(lineItem !== null){
          lineItem.equipments.push(eqli);
        }
      }
    }
  };

  Rnr.prototype.init = function () {
    var thisRnr = this;

    function prepareLineItems(lineItems) {
      var regularLineItems = [];
      $(lineItems).each(function (i, lineItem) {
        var regularLineItem = new RegularRnrLineItem(lineItem, thisRnr.numberOfMonths, programRnrColumns, thisRnr.status);
        regularLineItems.push(regularLineItem);
      });
      return regularLineItems;
    }

    this.fullSupplyLineItems = prepareLineItems(this.fullSupplyLineItems);
    this.nonFullSupplyLineItems = prepareLineItems(this.nonFullSupplyLineItems);
    this.nonFullSupplyLineItems.sort(function (lineItem1, lineItem2) {
      if (isUndefined(lineItem1))
        return 1;
      return lineItem1.compareTo(lineItem2);
    });
    this.programRnrColumnList = programRnrColumns;

    this.calculateFullSupplyItemsSubmittedCost();
    this.calculateNonFullSupplyItemsSubmittedCost();
  };

  this.init();
  this.initEquipments();
};
