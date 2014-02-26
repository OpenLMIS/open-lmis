/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function RefrigeratorReading(facilityVisitId, refrigeratorReading) {

  var fieldList = ['temperature', 'functioningCorrectly', 'lowAlarmEvents', 'highAlarmEvents', 'problemSinceLastTime'];

  RefrigeratorReading.prototype.computeStatus = function () {

    var statusClass = DistributionStatus.COMPLETE;
    var _this = this;

    function isEmpty(field) {
      if (field === "temperature" && !(isUndefined(_this[field].value)) &&
        ( _this[field].value.toString().trim() === "-" || _this[field].value.toString().trim() === ".")) {
        return true;
      }
      if (isUndefined(_this[field])) {
        return true;
      }
      return (isUndefined(_this[field].value) && !_this[field].notRecorded);
    }

    $(fieldList).each(function (index, field) {
      if (isEmpty(field)) {
        statusClass = DistributionStatus.EMPTY;
        return false;
      }
      return true;
    });

    if (statusClass === DistributionStatus.EMPTY) {
      $(fieldList).each(function (index, field) {
        if (!isEmpty(field)) {
          statusClass = DistributionStatus.INCOMPLETE;
          return false;
        }
        return true;
      });
    }

    if (statusClass === DistributionStatus.COMPLETE && _this.functioningCorrectly && _this.functioningCorrectly.value === 'N') {
      if (!_this.problems) statusClass = DistributionStatus.INCOMPLETE;
      else {
        var hasAtLeastOneProblem = _.find(_.values(_this.problems),
          function (problemValue) {
            return problemValue === true;
          });

        if (!_this.problems || !hasAtLeastOneProblem)
          statusClass = DistributionStatus.INCOMPLETE;

        if (hasAtLeastOneProblem && _this.problems.other && !_this.problems.otherProblemExplanation) {
          statusClass = DistributionStatus.INCOMPLETE;
        }
      }
    }

    _this.status = statusClass;

    return statusClass;
  };

  init.call(this);

  function init() {
    this.facilityVisitId = facilityVisitId;
    var _this = this;
    $.extend(true, this, refrigeratorReading);
    $(fieldList).each(function (i, fieldName) {
      _this[fieldName] = _this[fieldName] || {};
    });
    this.computeStatus();
  }
}
