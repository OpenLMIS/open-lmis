/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

function Refrigerators(refrigerators) {

  $.extend(true, this, refrigerators);

  var _this = this;

  $(this.refrigeratorReadings).each(function (i, value) {
    _this.refrigeratorReadings[i] = new RefrigeratorReading(value);
  });

  Refrigerators.prototype.computeStatus = function () {
    if (_.findWhere(this.refrigeratorReadings, {status: undefined})) {
      return 'is-incomplete';
    }
    if (_.findWhere(this.refrigeratorReadings, {status: 'is-incomplete'})) {
      return 'is-incomplete';
    }
    if (_.findWhere(this.refrigeratorReadings, {status: 'is-empty'})) {
      if (_.findWhere(this.refrigeratorReadings, {status: 'is-complete'})) {
        return 'is-incomplete';
      }
      return 'is-empty';
    }
    return 'is-complete';
  };

  Refrigerators.prototype.addRefrigerator = function(reading) {
    this.refrigeratorReadings.push(new RefrigeratorReading(reading));
  }
}