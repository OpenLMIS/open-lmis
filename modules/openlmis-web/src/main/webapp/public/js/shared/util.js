/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

var utils = {
  getFormattedDate: function (date) {
    return ('0' + date.getDate()).slice(-2) + '/' + ('0' + (date.getMonth() + 1)).slice(-2) +
      '/' + date.getFullYear();
  },

  isNullOrUndefined: function (obj) {
    return obj === undefined || obj === null;
  },

  isNumber: function (numberValue) {
    if (this.isNullOrUndefined(numberValue)) return false;
    var number = numberValue.toString();

    if (number.trim() === '') return false;
    return !isNaN(number);
  },

  parseIntWithBaseTen: function (number) {
    return parseInt(number, 10);
  },

  getValueFor: function (number, defaultValue) {
    if (!utils.isNumber(number)) return defaultValue ? defaultValue : null;
    return utils.parseIntWithBaseTen(number);
  },

  isValidPage: function (pageNumber, totalPages) {
    pageNumber = parseInt(pageNumber, 10);
    return !!pageNumber && pageNumber > 0 && pageNumber <= totalPages;
  },

  isEmpty: function (value) {
    return (value === null || value === undefined || value.toString().trim().length === 0);
  },

  sum: function () {
    var values = Array.prototype.slice.call(arguments), sum = 0;

    values.forEach(function (value) {
      if (!isUndefined(value)) {
        sum += utils.parseIntWithBaseTen(value);
      }
    });
    return sum;
  },

  getFormattedPercent: function(number) {
    return (number === null) ? '' : number + '%';
  }

};

String.prototype.format = function () {
  var formatted = this;
  for (var i = 0; i < arguments.length; i++) {
    var regexp = new RegExp('\\{' + i + '\\}', 'gi');
    formatted = formatted.replace(regexp, arguments[i]);
  }
  return formatted;
};

String.prototype.endsWith = function (searchString) {
  var position = this.length - searchString.length;
  if (position >= 0 && position < length)
    return false;
  return this.indexOf(searchString, position) !== -1;
};
