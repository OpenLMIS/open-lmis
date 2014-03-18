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

  $.extend(true, this, facilityDistribution);

  this.facilityVisitId = facilityDistribution.facilityVisit.id;
  this.epiUse = new EpiUse(facilityDistribution.epiUse);
  this.epiInventory = new EpiInventory(facilityDistribution.epiInventory);
  this.refrigerators = new Refrigerators(this.facilityVisitId, facilityDistribution.refrigerators);
  this.facilityVisit = new FacilityVisit(facilityDistribution.facilityVisit);
  this.fullCoverage = new FullCoverage(this.facilityVisitId, facilityDistribution.fullCoverage);
  this.childCoverage = new ChildCoverage(this.facilityVisitId, facilityDistribution.childCoverage);
  this.adultCoverage = new AdultCoverage(this.facilityVisitId, facilityDistribution.adultCoverage);

  this.status = facilityDistribution.status;

  FacilityDistribution.prototype.computeStatus = function () {

    var forms = [this.epiUse, this.refrigerators, this.facilityVisit, this.epiInventory, this.fullCoverage, this.childCoverage, this.adultCoverage];
    var overallStatus;
    if (this.status === DistributionStatus.SYNCED || this.status === DistributionStatus.DUPLICATE) {
      return this.status;
    }
    var that = this;
    $.each(forms, function (index, form) {
      var computedStatus = form.computeStatus(that.facilityVisit.visited);
      if (computedStatus === DistributionStatus.COMPLETE && (overallStatus === DistributionStatus.COMPLETE || !overallStatus)) {
        overallStatus = DistributionStatus.COMPLETE;
      } else if (computedStatus === DistributionStatus.EMPTY && (!overallStatus || overallStatus == DistributionStatus.EMPTY)) {
        overallStatus = DistributionStatus.EMPTY;
      } else if (computedStatus === DistributionStatus.INCOMPLETE || (computedStatus === DistributionStatus.EMPTY && overallStatus === DistributionStatus.COMPLETE) ||
        (computedStatus === DistributionStatus.COMPLETE && overallStatus === DistributionStatus.EMPTY)) {
        overallStatus = DistributionStatus.INCOMPLETE;
        return false;
      }
      return true;
    });

    this.status = overallStatus;
    return overallStatus;

  };

  FacilityDistribution.prototype.isDisabled = function (tabName) {
    if ([DistributionStatus.SYNCED, DistributionStatus.DUPLICATE].indexOf(this.status) != -1) {
      return true;
    }
    return (this.facilityVisit.visited === false && ["refrigerators", "epi-inventory", "epi-use"].indexOf(tabName) != -1);
  };

}