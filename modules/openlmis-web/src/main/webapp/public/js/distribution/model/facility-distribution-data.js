/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

function FacilityDistributionData(facilityDistributionData) {
  var COMPLETE = 'is-complete';
  var EMPTY = 'is-empty';
  var INCOMPLETE = 'is-incomplete';

  this.epiUse = new EpiUse(facilityDistributionData.epiUse);
  this.refrigerators = new Refrigerators(facilityDistributionData.refrigerators);
  this.generalObservation = new GeneralObservation(facilityDistributionData.generalObservation);

  var _this = this;
  FacilityDistributionData.prototype.computeStatus = function () {
    var forms = [_this.epiUse, _this.refrigerators, _this.generalObservation];
    var status;
    $.each(forms, function (index, form) {
      if (form.computeStatus() === COMPLETE && (status == COMPLETE || !status)) {
        status = COMPLETE;
      } else if (form.computeStatus() === EMPTY && (!status || status == EMPTY)) {
        status = EMPTY;
      } else if (form.computeStatus() === INCOMPLETE ||
        (form.computeStatus() === EMPTY && status === COMPLETE) ||
        (form.computeStatus() === COMPLETE && status === EMPTY)) {
        status = INCOMPLETE;
        return false;
      }

      return true;
    });
    return status;
  };
}