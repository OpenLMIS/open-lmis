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
function FacilityDemographicEstimateController($scope, categories, programs, years, FacilityDemographicEstimates, SaveFacilityDemographicEstimates) {

  $scope.categories = categories;
  $scope.programs = programs;
  $scope.years = years;
  $scope.year = years[0];

  $scope.OnPopulationChanged = function(population, facility, category){
    var pop = $scope.toNumber(population.value);
    if(category.isPrimaryEstimate){
      angular.forEach(facility.facilityEstimates, function(estimate){
        if(population.demographicEstimateId !== estimate.demographicEstimateId){
          estimate.value = $scope.round(estimate.conversionFactor * pop / 100) ;
        }
      });
    }
  };

  $scope.onParamChanged = function(){
    FacilityDemographicEstimates.get({programId : programs[0].id, year: $scope.year}, function(data){
      $scope.form = data.estimates;
      angular.forEach($scope.form.estimateLineItems, function(fe){
        fe.indexedEstimates = _.indexBy( fe.facilityEstimates , 'demographicEstimateId');
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
    SaveFacilityDemographicEstimates.update($scope.form, function(data){
      // show the saved message      $scope.message = "Facility demographic estimates successfully saved.";
    });
  };
  $scope.onParamChanged();

}

FacilityDemographicEstimateController.resolve = {

  categories: function ($q, $timeout, DemographicEstimateCategories) {
    var deferred = $q.defer();
    $timeout(function () {
      DemographicEstimateCategories.get({}, function (data) {
        deferred.resolve(data.estimate_categories);
      }, {});
    }, 100);
    return deferred.promise;
  }, programs: function ($q, $timeout, VaccineSupervisedIvdPrograms) {
    var deferred = $q.defer();

    $timeout(function () {
      VaccineSupervisedIvdPrograms.get({}, function (data) {
        deferred.resolve(data.programs);
      });
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
