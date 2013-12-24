/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function Refrigerators(refrigerators) {

  $.extend(true, this, refrigerators);

  var _this = this;

  $(this.readings).each(function (i, value) {
    _this.readings[i] = new RefrigeratorReading(value);
  });

  Refrigerators.prototype.computeStatus = function () {
    if (_.findWhere(this.readings, {status: 'is-incomplete'})) {
      return 'is-incomplete';
    }
    if (_.findWhere(this.readings, {status: 'is-empty'})) {
      if (_.findWhere(this.readings, {status: 'is-complete'})) {
        return 'is-incomplete';
      }
      return 'is-empty';
    }
    return 'is-complete';
  };

  Refrigerators.prototype.addReading = function (reading) {
    if (!this.readings) {
      this.readings = [];
    }
    this.readings.push(new RefrigeratorReading(reading));
  };
}