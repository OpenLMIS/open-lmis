/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *  Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function AdultCoverage(facilityVisitId, adultCoverageJSON) {
  $.extend(true, this, adultCoverageJSON);
  this.facilityVisitId = facilityVisitId;
  var _this = this;

  $(this.adultCoverageLineItems).each(function (i, lineItem) {
    _this.adultCoverageLineItems[i] = new AdultCoverageLineItem(lineItem);
  });

}
AdultCoverage.prototype.wastageRate = function (openedVialLineItem) {
  if (isUndefined(openedVialLineItem) || isUndefined(openedVialLineItem.value) || isUndefined(openedVialLineItem.packSize)) return null;
  var totalDosesConsumed = openedVialLineItem.value * openedVialLineItem.packSize;
  if (totalDosesConsumed === 0) return null;
  return Math.round((totalDosesConsumed - this.totalTetanus()) / totalDosesConsumed * 100);
};

AdultCoverage.prototype.setNotRecorded = function () {
  $(this.adultCoverageLineItems).each(function (i, lineItem) {
    lineItem.setNotRecorded();
  });

  $(this.openedVialLineItems).each(function (i, lineItem) {
    lineItem.openedVial = {notRecorded: true};
  });
};

AdultCoverage.prototype.totalHealthCenterTetanus1 = function () {
  return this.sumOfAttributes('healthCenterTetanus1');
};
AdultCoverage.prototype.totalOutreachTetanus1 = function () {
  return this.sumOfAttributes('outreachTetanus1');
};
AdultCoverage.prototype.totalHealthCenterTetanus2To5 = function () {
  return this.sumOfAttributes('healthCenterTetanus2To5');
};
AdultCoverage.prototype.totalOutreachTetanus2To5 = function () {
  return this.sumOfAttributes('outreachTetanus2To5');
};

AdultCoverage.prototype.totalTetanus1 = function () {
  return this.totalHealthCenterTetanus1() + this.totalOutreachTetanus1();
};

AdultCoverage.prototype.totalTetanus2To5 = function () {
  return this.totalHealthCenterTetanus2To5() + this.totalOutreachTetanus2To5();
};

AdultCoverage.prototype.totalTetanus = function () {
  return this.totalTetanus1() + this.totalTetanus2To5();
};

AdultCoverage.prototype.sumOfAttributes = function (attribute) {
  var total = 0;
  $(this.adultCoverageLineItems).each(function (i, lineItem) {
    var currentValue = isUndefined(lineItem[attribute]) ? 0 : lineItem[attribute].value;
    total = utils.sum(total, currentValue);
  });
  return total;
};

function AdultCoverageLineItem(lineItem) {
  $.extend(true, this, lineItem);
}

AdultCoverageLineItem.prototype.setNotRecorded = function () {
  this.healthCenterTetanus1 = {notRecorded : true};
  this.outreachTetanus1= {notRecorded : true};
  this.healthCenterTetanus2To5= {notRecorded : true};
  this.outreachTetanus2To5= {notRecorded : true};
};

AdultCoverageLineItem.prototype.totalTetanus1 = function () {
  var value1 = isUndefined(this.healthCenterTetanus1) ? 0 : this.healthCenterTetanus1.value;
  var value2 = isUndefined(this.outreachTetanus1) ? 0 : this.outreachTetanus1.value;
  return utils.sum(value1, value2);
};

AdultCoverageLineItem.prototype.totalTetanus2To5 = function () {
  var value1 = isUndefined(this.healthCenterTetanus2To5) ? 0 : this.healthCenterTetanus2To5.value;
  var value2 = isUndefined(this.outreachTetanus2To5) ? 0 : this.outreachTetanus2To5.value;
  return utils.sum(value1, value2);
};

AdultCoverageLineItem.prototype.totalTetanus = function () {
  return this.totalTetanus1() + this.totalTetanus2To5();
};

AdultCoverageLineItem.prototype.coverageRate = function () {
  if (isUndefined(this.targetGroup) || this.targetGroup === 0) return null;
  return Math.round((this.totalTetanus() / this.targetGroup) * 100);
};
