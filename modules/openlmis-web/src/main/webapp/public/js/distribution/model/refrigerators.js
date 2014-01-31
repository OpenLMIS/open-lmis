/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function Refrigerators(facilityVisitId, refrigerators) {

  $.extend(true, this, refrigerators);

  var _this = this;
  this.facilityVisitId = facilityVisitId;
  $(this.readings).each(function (i, value) {
    _this.readings[i] = new RefrigeratorReading(facilityVisitId, value);
  });

  Refrigerators.prototype.computeStatus = function (visited) {
    if (visited === false) {
      return DistributionStatus.COMPLETE;
    }
    if (_.findWhere(this.readings, {status: DistributionStatus.INCOMPLETE})) {
      return DistributionStatus.INCOMPLETE;
    }
    if (_.findWhere(this.readings, {status: DistributionStatus.EMPTY})) {
      if (_.findWhere(this.readings, {status: DistributionStatus.COMPLETE})) {
        return DistributionStatus.INCOMPLETE;
      }
      return DistributionStatus.EMPTY;
    }
    return DistributionStatus.COMPLETE;
  };

  Refrigerators.prototype.addReading = function (reading) {
    if (!this.readings) {
      this.readings = [];
    }

    this.readings.push(new RefrigeratorReading(facilityVisitId, reading));
  };
}