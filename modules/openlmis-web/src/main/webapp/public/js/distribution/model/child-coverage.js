/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function ChildCoverage(facilityVisitId, childCoverageJSON) {
  $.extend(true, this, childCoverageJSON);
  this.facilityVisitId = facilityVisitId;

  $(this.childCoverageLineItems).each(function (index, lineItem) {
    lineItem.healthCenter23Months = lineItem.healthCenter23Months || {};
    lineItem.outreach23Months = lineItem.outreach23Months || {};
  });

  $(this.openedVialLineItems).each(function (index, lineItem) {
    lineItem.openedVial = lineItem.openedVial || {};
  });
}

ChildCoverage.prototype.computeStatus = function () {
  var status;

  var isValid = function (field) {
    if (!field)
      return false;
    return !(isUndefined(field.value) && !field.notRecorded);
  };

  function validateLineItems(lineItems, mandatoryFields) {
    $(lineItems).each(function (index, lineItem) {
      $(mandatoryFields).each(function (index, field) {
        if (lineItem.vaccination === 'Polio (Newborn)' && ['healthCenter23Months', 'outreach23Months'].indexOf(field) !== -1)
          return true;
        if ((status === DistributionStatus.COMPLETE || !status) && isValid(lineItem[field])) {
          status = DistributionStatus.COMPLETE;
          return true;
        } else if ((status === DistributionStatus.EMPTY || !status) && !isValid(lineItem[field])) {
          status = DistributionStatus.EMPTY;
          return true;
        } else if ((status === DistributionStatus.EMPTY && isValid(lineItem[field])) || (status === DistributionStatus.COMPLETE && !isValid(lineItem[field]))) {
          status = DistributionStatus.INCOMPLETE;
          return false;
        }
        return true;
      });
      return status !== DistributionStatus.INCOMPLETE;
    });
  }

  validateLineItems(this.childCoverageLineItems, ['healthCenter11Months', 'outreach11Months', 'healthCenter23Months', 'outreach23Months']);
  validateLineItems(this.openedVialLineItems, ['openedVial']);

  this.status = status;
  return this.status;
};

ChildCoverage.prototype.setNotRecorded = function () {
  this.childCoverageLineItems.forEach(function (lineItem) {
    lineItem.healthCenter11Months = {notRecorded: true};
    lineItem.healthCenter23Months = {notRecorded: true};
    lineItem.outreach11Months = {notRecorded: true};
    lineItem.outreach23Months = {notRecorded: true};
  });
  this.openedVialLineItems.forEach(function (lineItem) {
    lineItem.openedVial = {notRecorded: true};
  });
};
