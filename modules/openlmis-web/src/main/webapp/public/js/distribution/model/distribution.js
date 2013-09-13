/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function Distribution(distributionJson) {
  var COMPLETE = 'is-complete';
  var EMPTY = 'is-empty';
  var INCOMPLETE = 'is-incomplete';

  $.extend(true, this, distributionJson);

  if (this.facilityDistributionData) {
    $.each(this.facilityDistributionData, function (key, value) {
      value.epiUse = new EpiUse(value.epiUse);
      value.refrigerators = new Refrigerators(value.refrigerators);
      value.generalObservation = new GeneralObservation(value.generalObservation);
    });
  }

  Distribution.prototype.setEpiNotRecorded = function (facilityId) {
    this.facilityDistributionData[facilityId].epiUse.setNotRecorded();
  };

  Distribution.prototype.computeStatus = function () {
    var status;
    $.each(this.facilityDistributionData, function (index, facilityDistributionData) {
      if (facilityDistributionData.computeStatus() === COMPLETE && (status == COMPLETE || !status)) {
        status = COMPLETE;
      } else if (facilityDistributionData.computeStatus() === EMPTY && (!status || status == EMPTY)) {
        status = EMPTY;
      } else if (facilityDistributionData.computeStatus() === INCOMPLETE ||
        (facilityDistributionData.computeStatus() === EMPTY && status === COMPLETE) ||
        (facilityDistributionData.computeStatus() === COMPLETE && status === EMPTY))
      {
        status = INCOMPLETE;
        return false;
      }
      return true;
    });

    return status;
  };
  return this;
}


