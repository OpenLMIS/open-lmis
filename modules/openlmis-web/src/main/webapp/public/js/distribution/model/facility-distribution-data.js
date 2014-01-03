/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function FacilityDistribution(facilityDistribution) {

  var COMPLETE = 'is-complete';
  var EMPTY = 'is-empty';
  var INCOMPLETE = 'is-incomplete';
  var SYNCED = 'is-synced';
  var DUPLICATE = 'is-duplicate';

  this.epiUse = new EpiUse(facilityDistribution.epiUse);
  this.epiInventory = new EpiInventory(facilityDistribution.epiInventory);
  this.refrigerators = new Refrigerators(facilityDistribution.refrigerators);
  this.facilityVisit = new FacilityVisit(facilityDistribution.facilityVisit);
  this.coverage = new Coverage(facilityDistribution.coverage);
  this.status = facilityDistribution.status;

  FacilityDistribution.prototype.computeStatus = function () {

    var forms = [this.epiUse, this.refrigerators, this.facilityVisit, this.epiInventory, this.coverage];
    var overallStatus;
    if(this.status === SYNCED || this.status === DUPLICATE) {
      return this.status;
    }
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

    this.status = overallStatus;
    return overallStatus;

  };

  FacilityDistribution.prototype.isDisabled = function () {
    return [SYNCED, DUPLICATE].indexOf(this.status) != -1;
  };

}