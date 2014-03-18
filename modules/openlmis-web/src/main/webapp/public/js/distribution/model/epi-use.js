/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function EpiUse(epiUse) {

  var DATE_REGEXP = /^(0[1-9]|1[012])[/]((2)\d\d\d)$/;
  var fieldList = ['stockAtFirstOfMonth', 'received', 'distributed', 'loss', 'stockAtEndOfMonth', 'expirationDate'];

  function init() {
    $.extend(true, this, epiUse);
    $(this.lineItems).each(function (i, lineItem) {
      $(fieldList).each(function (i, fieldName) {
        lineItem[fieldName] = lineItem[fieldName] || {};
      });
    });
  }

  init.call(this);

  EpiUse.prototype.setNotRecorded = function () {
    $(this.lineItems).each(function (i, lineItem) {
      $(fieldList).each(function (j, fieldName) {
        lineItem[fieldName].notRecorded = true;
      });
    });
  };

  EpiUse.prototype.computeStatus = function (visited) {
    var _this = this;
    var statusClass;

    if (visited === false) {
      return DistributionStatus.COMPLETE;
    }

    function isEmpty(field, obj) {
      if (isUndefined(obj[field])) {
        return true;
      }
      return (isUndefined(obj[field].value) && !obj[field].notRecorded);
    }

    function isValid(field, obj) {
      return (field != 'expirationDate') ? !isEmpty(field, obj) : (obj[field].notRecorded || DATE_REGEXP.test(obj[field].value));
    }

    $(_this.lineItems).each(function (i, lineItem) {
      if (!lineItem) {
        statusClass = DistributionStatus.EMPTY;
        return;
      }
      $(fieldList).each(function (i, fieldName) {
        if (isValid(fieldName, lineItem) && (!statusClass || statusClass == DistributionStatus.COMPLETE)) {
          statusClass = DistributionStatus.COMPLETE;
        } else if (!isValid(fieldName, lineItem) && (!statusClass || statusClass == DistributionStatus.EMPTY)) {
          statusClass = DistributionStatus.EMPTY;
        } else if ((!isValid(fieldName, lineItem) && statusClass == DistributionStatus.COMPLETE) || (isValid(fieldName, lineItem) && statusClass == DistributionStatus.EMPTY)) {
          statusClass = DistributionStatus.INCOMPLETE;
          return false;
        }
        return true;
      });
    });

    _this.status = statusClass || DistributionStatus.COMPLETE;

    return _this.status;
  };
}
