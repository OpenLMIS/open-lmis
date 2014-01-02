/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function EpiInventory(epiInventory) {
  $.extend(true, this, epiInventory);

  var complete = 'is-complete';
  var incomplete = 'is-incomplete';
  var empty = 'is-empty';
  var mandatoryFields = ['existingQuantity', 'deliveredQuantity', 'spoiledQuantity'];

  function isValid(lineItem, field) {
    if (isUndefined(lineItem[field]) || field === 'deliveredQuantity')
      return !isUndefined(lineItem[field]);
    return (!isUndefined(lineItem[field].value) || lineItem[field].notRecorded);
  }

  EpiInventory.prototype.computeStatus = function () {
    var statusClass;
    $(this.lineItems).each(function (index, lineItem) {
      $(mandatoryFields).each(function (index, field) {
        if (isValid(lineItem, field) && (!statusClass || statusClass === complete)) {
          statusClass = complete;
        } else if (!isValid(lineItem, field) && (!statusClass || statusClass === empty)) {
          statusClass = empty;
        } else if ((!isValid(lineItem, field) && statusClass === complete) || (isValid(lineItem, field) && statusClass === empty)) {
          statusClass = incomplete;
          return false;
        }
        return true;
      });
    });
    return statusClass;
  };

}
