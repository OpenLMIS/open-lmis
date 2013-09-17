/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

function GeneralObservation(generalObservationJson) {
  $.extend(true, this, generalObservationJson);
  var mandatoryList = ['observations', 'verifiedBy', 'confirmedBy'];
  var COMPLETE = 'is-complete';
  var EMPTY = 'is-empty';
  var INCOMPLETE = 'is-incomplete';

  GeneralObservation.prototype.computeStatus = function () {
    var _this = this;
    var status;

    function isValid(fieldName) {
      if(!_this[fieldName]) return false;

      if (fieldName === 'observations') return !isUndefined(_this[fieldName]);

      return !(isUndefined(_this[fieldName].name) || isUndefined(_this[fieldName].title));
    }

    function isEmpty(fieldName) {
      if(!_this[fieldName]) return true;

      if (fieldName === 'observations') return isUndefined(_this[fieldName]);

      return isUndefined(_this[fieldName].name) && isUndefined(_this[fieldName].title);
    }

    $(mandatoryList).each(function (i, fieldName) {
      if (isValid(fieldName) && (status == COMPLETE || !status)) {
        status = COMPLETE;
      } else if (!isValid(fieldName) && isEmpty(fieldName) && (!status || status == EMPTY)) {
        status = EMPTY;
      } else if ((!isValid(fieldName) && status === COMPLETE) || (isValid(fieldName) && status === EMPTY) || (!isEmpty(fieldName))) {
        status = INCOMPLETE;
        return false;
      }
      return true;
    });

    _this.status = status;

    return status;
  }
}