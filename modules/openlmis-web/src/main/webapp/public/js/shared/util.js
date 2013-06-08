/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

var utils = {
  getFormattedDate: function (date) {
    return ('0' + date.getDate()).slice(-2) + '/'
      + ('0' + (date.getMonth() + 1)).slice(-2) + '/'
      + date.getFullYear();
  },

  isNullOrUndefined: function (obj) {
    return obj == undefined || obj == null;
  },

  isNumber: function (numberValue) {
    if (this.isNullOrUndefined(numberValue)) return false;
    var number = numberValue.toString();

    if (number.trim() == '') return false;
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
    return (value == null || value == undefined || value.toString().trim().length == 0);
  }

};

String.prototype.format = function() {
    var formatted = this;
    for (var i = 0; i < arguments.length; i++) {
        var regexp = new RegExp('\\{'+i+'\\}', 'gi');
        formatted = formatted.replace(regexp, arguments[i]);
    }
    return formatted;
};
