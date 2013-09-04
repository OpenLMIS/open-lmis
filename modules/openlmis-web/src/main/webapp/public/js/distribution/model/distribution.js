/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function Distribution(distributionJson) {
  $.extend(true, this, distributionJson);

  if (this.facilityDistributionData) {
    $.each(this.facilityDistributionData, function (key, value) {
      value.epiUse = new EpiUse(value.epiUse);
      value.refrigerators = new Refrigerators(value.refrigerators);
    });
  }

  Distribution.prototype.setEpiNotRecorded = function(facilityId) {
    this.facilityDistributionData[facilityId].epiUse.setNotRecorded();
  };

  return this;
}


