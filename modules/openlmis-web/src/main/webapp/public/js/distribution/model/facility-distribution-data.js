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

  FacilityDistributionData.prototype.computeStatus = function () {
    var forms = [this.epiUse, this.refrigerators, this.generalObservation];
    var overallStatus;
    $.each(forms, function (index, form) {
      var computedStatus = form.computeStatus();
      if (computedStatus === COMPLETE && (overallStatus === COMPLETE || !overallStatus)) {
        overallStatus = COMPLETE;
      } else if (computedStatus === EMPTY && (!overallStatus || overallStatus == EMPTY)) {
        overallStatus = EMPTY;
      } else if (computedStatus === INCOMPLETE || (computedStatus === EMPTY && overallStatus === COMPLETE) ||
        (computedStatus === COMPLETE && overallStatus === EMPTY)) {
        overallStatus = INCOMPLETE;
        return false;
      }
      return true;
    });

    return overallStatus;
  };
}