/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function RefrigeratorReading(temperature, functioningCorrectly,
                             lowAlarmEvents, highAlarmEvents, problemSinceLastTime,
                             problemList, notes, refrigeratorId, distributionId) {

  this.temperature = temperature;
  this.functioningCorrectly = functioningCorrectly;
  this.lowAlarmEvents = lowAlarmEvents;
  this.highAlarmEvents = highAlarmEvents;
  this.problemSinceLastTime = problemSinceLastTime;
  this.problemList = problemList;
  this.notes = notes;
  this.refrigeratorId = refrigeratorId;
  this.distributionId = distributionId;

  return this;
}
