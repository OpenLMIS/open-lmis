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
