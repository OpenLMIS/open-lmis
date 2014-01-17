/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *  Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */


function FullCoverage(facilityVisitId, fullCoverage) {
  $.extend(true, this, fullCoverage);
  this.facilityVisitId = facilityVisitId;

  var fieldList = ['femaleHealthCenterReading', 'femaleMobileBrigadeReading', 'maleHealthCenterReading', 'maleMobileBrigadeReading'];

  function init() {
    var _this = this;
    $(fieldList).each(function (i, fieldName) {
      if (!isUndefined(fullCoverage)) {
        _this[fieldName] = fullCoverage[fieldName] || {};
      }
      else {
        _this[fieldName] = {fieldName: {}};
      }
    });
  }

  init.call(this);

  FullCoverage.prototype.computeStatus = function () {
    var _this = this;
    var status;

    function isEmpty(fieldName) {
      return (isUndefined(_this[fieldName]) || (isUndefined(_this[fieldName].value) && !_this[fieldName].notRecorded));
    }

    $(fieldList).each(function (i, fieldName) {
      if (!isEmpty(fieldName) && (status == DistributionStatus.COMPLETE || !status)) {
        status = DistributionStatus.COMPLETE;
      } else if (isEmpty(fieldName) && (!status || status == DistributionStatus.EMPTY)) {
        status = DistributionStatus.EMPTY;
      } else if ((isEmpty(fieldName) && status === DistributionStatus.COMPLETE) || (!isEmpty(fieldName) && status === DistributionStatus.EMPTY) || (!isEmpty(fieldName))) {
        status = DistributionStatus.INCOMPLETE;
        return false;
      }
      return true;
    });

    _this.status = status;

    return status;
  };

  FullCoverage.prototype.setNotRecorded = function () {
    var _this = this;
    $(fieldList).each(function (j, fieldName) {
      _this[fieldName].notRecorded = true;
    });
  };
}