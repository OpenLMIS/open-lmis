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

  if (distributionJson.facilityDistributionData) {
    var _this = this;
    this.facilityDistributionData = {};
    $.each(distributionJson.facilityDistributionData, function (key, value) {
      _this.facilityDistributionData[key] = new FacilityDistributionData(value);
    });
  }

  Distribution.prototype.setEpiNotRecorded = function (facilityId) {
    this.facilityDistributionData[facilityId].epiUse.setNotRecorded();
  };

  Distribution.prototype.computeStatus = function () {
    var overallStatus;
    $.each(this.facilityDistributionData, function (index, facilityDistributionData) {
      var computedStatus = facilityDistributionData.computeStatus();
      if (computedStatus === COMPLETE && (overallStatus == COMPLETE || !overallStatus)) {
        overallStatus = COMPLETE;
      } else if (computedStatus === EMPTY && (!overallStatus || overallStatus == EMPTY)) {
        overallStatus = EMPTY;
      } else if (computedStatus === INCOMPLETE ||
        (computedStatus === EMPTY && overallStatus === COMPLETE) ||
        (computedStatus === COMPLETE && overallStatus === EMPTY)) {
        overallStatus = INCOMPLETE;
        return false;
      }
      return true;
    });
    return overallStatus;
  };
  return this;
}


