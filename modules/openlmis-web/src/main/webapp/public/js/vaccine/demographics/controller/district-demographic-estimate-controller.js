/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function DistrictDemographicEstimateController($scope, categories, years, DistrictDemographicEstimates, SaveDistrictDemographicEstimates) {

  $scope.categories = categories;
  $scope.years = years;
  $scope.year = years[0];

  $scope.OnPopulationChanged = function(population, district, category){
    var pop = $scope.toNumber(population.value);
    if(category.isPrimaryEstimate){
      angular.forEach(district.districtEstimates, function(estimate){
        if(population.demographicEstimateId !== estimate.demographicEstimateId){
          estimate.value = $scope.round(estimate.conversionFactor * pop / 100) ;
        }
      });
    }
  };

  $scope.onParamChanged = function(){
    DistrictDemographicEstimates.get({year: $scope.year}, function(data){
      $scope.form = data.estimates;
      angular.forEach($scope.form.estimateLineItems, function(fe){
        fe.indexedEstimates = _.indexBy( fe.districtEstimates , 'demographicEstimateId' );
      });
    });
  };

  $scope.toNumber = function (val) {
    if (angular.isDefined(val) && val !== null) {
      return parseInt(val, 10);
    }
    return 0;
  };

  $scope.round = function(val){
    return Math.ceil(val);
  };

  $scope.save = function(){
    SaveDistrictDemographicEstimates.update($scope.form, function(data){
      // show the saved message
      //TODO: move this string to the messages property file.
      $scope.message = "District demographic estimates successfully saved.";
    });
  };

  $scope.onParamChanged();

}

DistrictDemographicEstimateController.resolve = {

  categories: function ($q, $timeout, DemographicEstimateCategories) {
    var deferred = $q.defer();
    $timeout(function () {
      DemographicEstimateCategories.get({}, function (data) {
        deferred.resolve(data.estimate_categories);
      }, {});
    }, 100);
    return deferred.promise;
  }, years: function ($q, $timeout, OperationYears) {
    var deferred = $q.defer();

    $timeout(function () {
      OperationYears.get({}, function (data) {
        deferred.resolve(data.years);
      });
    }, 100);

    return deferred.promise;
  }


};
