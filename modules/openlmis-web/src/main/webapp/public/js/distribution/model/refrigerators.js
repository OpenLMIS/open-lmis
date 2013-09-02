/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

function Refrigerators(readings) {

  this.readings = readings;
  var _this = this;

  Refrigerators.prototype.computeStatus = function () {
    if (_.findWhere(_this.readings, {status: undefined})) {
      return 'is-incomplete';
    }
    if (_.findWhere(_this.readings, {status: 'is-incomplete'})) {
      return 'is-incomplete';
    }
    if (_.findWhere(_this.readings, {status: 'is-empty'})) {
      if (_.findWhere(_this.readings, {status: 'is-complete'})) {
        return 'is-incomplete';
      }
      return 'is-empty';
    }
    return 'is-complete';
  }
}