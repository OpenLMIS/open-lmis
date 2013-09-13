/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

function FacilityDistributionData(data){
  var COMPLETE = 'is-complete';
  var EMPTY = 'is-empty';
  var INCOMPLETE = 'is-incomplete';

  $.extend(true, this, data);

  FacilityDistributionData.prototype.computeStatus = function() {
    var status;
    $.each(data,function(index, form){
      if (form.computeStatus() === COMPLETE && (status == COMPLETE || !status)) {
        status = COMPLETE;
      } else if (form.computeStatus() === EMPTY && (!status || status == EMPTY)) {
        status = EMPTY;
      } else if (form.computeStatus() === INCOMPLETE ||
        (form.computeStatus() === EMPTY && status === COMPLETE) ||
        (form.computeStatus() === COMPLETE && status === EMPTY))
      {
        status = INCOMPLETE;
        return false;
      }

      return true;
    });
    return status;
  };
}