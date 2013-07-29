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
  },

 formatNumber : function(value, format){
    var numericObject = new Number(value);
    return numericObject.format(format);
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

/*
 * Formats the number according to the <q>format</q> string; adherses to the american number standard where a comma is inserted after every 3 digits.
 *  note: there should be only 1 contiguous number in the format, where a number consists of digits, period, and commas
 *        any other characters can be wrapped around this number, including <q>$</q>, <q>%</q>, or text
 *        examples (123456.789):
 *          <q>0</q> - (123456) show only digits, no precision
 *          <q>0.00</q> - (123456.78) show only digits, 2 precision
 *          <q>0.0000</q> - (123456.7890) show only digits, 4 precision
 *          <q>0,000</q> - (123,456) show comma and digits, no precision
 *          <q>0,000.00</q> - (123,456.78) show comma and digits, 2 precision
 *          <q>0,0.00</q> - (123,456.78) shortcut method, show comma and digits, 2 precision
 *
 * @method format
 * @param format {string} the way you would like to format this text
 * @return {string} the formatted number
 * @public
 */

Number.prototype.format = function(format) {

    // if (! isType(format, 'string')) {return ;} // sanity check
    stripNonNumeric = function ( str ){

        str += '';

        var rgx = /^\d|\.|-$/;
        var out = '';
        for( var i = 0; i < str.length; i++ )
        {
            if( rgx.test( str.charAt(i) ) ){

                if( !( ( str.charAt(i) == '.' && out.indexOf( '.' ) != -1 ) ||

                    ( str.charAt(i) == '-' && out.length != 0 ) ) ){

                    out += str.charAt(i);

                }
            }
        }
        return out;
    }


    var hasComma = -1 < format.indexOf(','),

        psplit = stripNonNumeric(format).split('.'),

        that = this;

    // compute precision

    if (1 < psplit.length) {

        // fix number precision

        that = that.toFixed(psplit[1].length);

    }
    // error: too many periods

    else if (2 < psplit.length) {

        throw('NumberFormatException: invalid format, formats should have no more than 1 period: ' + format);

    }
    // remove precision

    else {

        that = that.toFixed(0);

    }
    // get the string now that precision is correct

    var fnum = that.toString();

    // format has comma, then compute commas

    if (hasComma) {

        // remove precision for computation

        psplit = fnum.split('.');

        var cnum = psplit[0],

            parr = [],

            j = cnum.length,

            m = Math.floor(j / 3),

            n = cnum.length % 3 || 3; // n cannot be ZERO or causes infinite loop

        // break the number into chunks of 3 digits; first chunk may be less than 3

        for (var i = 0; i < j; i += n) {

            if (i != 0) {n = 3;}

            parr[parr.length] = cnum.substr(i, n);

            m -= 1;

        }
        // put chunks back together, separated by comma

        fnum = parr.join(',');

        // add the precision back in

        if (psplit[1]) {fnum += '.' + psplit[1];}

    }

    // replace the number portion of the format with fnum
    return format.replace(/[\d,?\.?]+/, fnum);

};
