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
    $(this.productGroups).each(function (i, group) {
      group.reading = group.reading || {};
      $(fieldList).each(function (i, fieldName) {
        group.reading[fieldName] = group.reading[fieldName] || {};
      });
    });
  }

  init.call(this);

  EpiUse.prototype.setNotRecorded = function () {
    $(this.productGroups).each(function (i, group) {
      $.each(group.reading, function (key) {
        group.reading[key].notRecorded = true;
      });
    });
  };

  EpiUse.prototype.computeStatus = function () {
    var _this = this;
    var complete = 'is-complete';
    var incomplete = 'is-incomplete';
    var empty = 'is-empty';

    var statusClass;

    function isEmpty(field, obj) {
      if (isUndefined(obj[field])) {
        return true;
      }
      return (isUndefined(obj[field].value) && !obj[field].notRecorded);
    }

    function isValid(field, obj) {
      return (field != 'expirationDate') ? !isEmpty(field, obj) : (obj[field].notRecorded || DATE_REGEXP.test(obj[field].value));
    }

    $(_this.productGroups).each(function (i, productGroup) {
      if (!productGroup.reading) {
        statusClass = empty;
        return;
      }
      $(fieldList).each(function (i, fieldName) {
        if (isValid(fieldName, productGroup.reading) && (!statusClass || statusClass == complete)) {
          statusClass = complete;
        } else if (!isValid(fieldName, productGroup.reading) && (!statusClass || statusClass == empty)) {
          statusClass = empty;
        } else if ((!isValid(fieldName, productGroup.reading) && statusClass == complete) || (isValid(fieldName, productGroup.reading) && statusClass == empty)) {
          statusClass = incomplete;
          return false;
        }
        return true;
      })
    });

    _this.status = statusClass || complete;

    return _this.status;
  }
}
