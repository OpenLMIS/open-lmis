 function FacilityEstimateModel (){

  FacilityEstimateModel.prototype.getByCategory = function(category, year) {
    var categoryValue = _.findWhere(this.facilityEstimates, {
      'demographicEstimateId': category.id
    });

    if (angular.isUndefined(categoryValue)) {
      var programId = (this.facilityEstimates !== undefined && this.facilityEstimates.length > 0)? this.facilityEstimates[0].programId: undefined;
      categoryValue = {
        'demographicEstimateId': category.id,
        'programId': programId,
        'year': year,
        'conversionFactor': category.defaultConversionFactor,
        'value': 0
      };
      this.facilityEstimates.push(categoryValue);
    }
    return categoryValue;
  };

  FacilityEstimateModel.prototype.populationChanged = function(autoCalculate) {
    if (autoCalculate) {
      var population = _.findWhere(this.facilityEstimates, {
        'demographicEstimateId': 1
      });
      var pop =  Number(population.value);
      angular.forEach(this.facilityEstimates, function(estimate) {
        if (population.demographicEstimateId !== estimate.demographicEstimateId) {
          estimate.value = Math.round(estimate.conversionFactor * pop / 100);
        }
      });
    }
  };

}


function AggregateFacilityEstimateModel( facilityList ){

  this.indexedList = _.groupBy(facilityList, 'parentId');

  AggregateFacilityEstimateModel.prototype.getSummary = function(district, category, year){
    var facilities = this.indexedList[district];
    var sum = 0;
    angular.forEach(facilities, function(facility){
      var val = facility.getByCategory(category, year);
      sum = sum + Number(val.value);
    });
    return sum;
  };

}
