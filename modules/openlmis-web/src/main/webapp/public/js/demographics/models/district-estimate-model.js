/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

function DistrictEstimateModel (){

  DistrictEstimateModel.prototype.getByCategory = function(category, year) {
    var categoryValue = _.findWhere(this.districtEstimates, {
      'demographicEstimateId': category.id
    });
    if (angular.isUndefined(categoryValue)) {
      var programId = (this.districtEstimates !== undefined && this.districtEstimates.length > 0)? this.districtEstimates[0].programId: undefined;
      categoryValue = {
        'demographicEstimateId': category.id,
        'year': year,
        'programId': programId,
        'conversionFactor': category.defaultConverstionFactor,
        'value': 0
      };
      this.districtEstimates.push(categoryValue);
    }
    return categoryValue;
  };

  DistrictEstimateModel.prototype.getFacilityAggregateByCategory = function(category, year) {
    var categoryValue = _.findWhere(this.facilityEstimates, {
      'demographicEstimateId': category.id
    });
    if (angular.isUndefined(categoryValue)) {
      var programId = (this.facilityEstimates !== undefined && this.facilityEstimates.length > 0)? this.facilityEstimates[0].programId: undefined;
      categoryValue = {
        'demographicEstimateId': category.id,
        'year': year,
        'programId': programId,
        'conversionFactor': category.defaultConverstionFactor,
        'value': 0
      };
      this.facilityEstimates.push(categoryValue);
    }
    return categoryValue;
  };

  DistrictEstimateModel.prototype.populationChanged = function(autoCalculate) {
    if (autoCalculate) {
      var population = _.findWhere(this.districtEstimates, {
        'demographicEstimateId': 1
      });
      var pop =  Number(population.value);
      angular.forEach(this.districtEstimates, function(estimate) {
        if (population.demographicEstimateId !== estimate.demographicEstimateId) {
          estimate.value = Math.round(estimate.conversionFactor * pop / 100);
        }
      });
    }
  };

}

function AggregateRegionEstimateModel( districtList ){

  this.indexedList = _.groupBy(districtList, 'parentId');

  AggregateRegionEstimateModel.prototype.getSummary = function(region, category, year){
    var districts = this.indexedList[region];
    var sum = 0;
    angular.forEach(districts, function(district){
      var val = district.getByCategory(category, year);
      sum = sum + Number(val.value);
    });
    return sum;
  };

}

