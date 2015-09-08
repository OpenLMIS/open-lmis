var FacilityEstimateModel = function() {

  FacilityEstimateModel.prototype.getByCategory = function(category, year) {
    var categoryValue = _.findWhere(this.facilityEstimates, {
      'demographicEstimateId': category.id
    });
    if (angular.isUndefined(categoryValue)) {
      categoryValue = {
        'demographicEstimateId': category.id,
        'year': year,
        'conversionFactor': category.defaultConverstionFactor,
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

};
