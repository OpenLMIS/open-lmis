/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

var ProgramProduct = function (programProduct) {
  $.extend(true, this, programProduct);

  ProgramProduct.prototype.calculateISA = function (facility, period) {
    if (isUndefined(programProduct.programProductIsa) && isUndefined(programProduct.overriddenIsa)) {
      programProduct.isaAmount = "--";
    } else {
      programProduct.programProductIsa = new ProgramProductISA(programProduct.programProductIsa);
      programProduct.isaAmount = (!isUndefined(programProduct.overriddenIsa)) ? programProduct.overriddenIsa : programProduct.programProductIsa.calculate(facility.catchmentPopulation);
//          TODO important need validation on packSize to be more than 0
      programProduct.isaAmount = programProduct.isaAmount ? Math.ceil((programProduct.isaAmount * period.numberOfMonths) / programProduct.product.packSize) : 0;
    }

    this.isaAmount = programProduct.isaAmount;
  };


};

ProgramProduct.groupProductsMapByName = function (facility, otherGroupName) {
  var programProductsMap = _.groupBy(facility.supportedPrograms[0].programProducts, function (programProduct) {
    return programProduct.product.productGroup ? programProduct.product.productGroup.name : otherGroupName;
  });
  return programProductsMap;
};

ProgramProduct.calculateProductIsaTotal = function (aggregateProduct, productTotal) {
  if (!isNaN(utils.parseIntWithBaseTen(aggregateProduct.isaAmount))) {
    if (productTotal.isaAmount == "--") {
      productTotal.isaAmount = aggregateProduct.isaAmount;
    } else {
      productTotal.isaAmount = productTotal.isaAmount + aggregateProduct.isaAmount;
    }
  }
};