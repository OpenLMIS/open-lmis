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
function FacilityDemographicEstimateController($scope, categories, years, FacilityDemographicEstimates, SaveFacilityDemographicEstimates) {

  $scope.currentPage = 1;
  $scope.pageSize = 15;

  $scope.categories = categories;
  $scope.years = years;
  $scope.year = years[0];

  $scope.onParamChanged = function(){
    FacilityDemographicEstimates.get({year: $scope.year}, function(data){

      $scope.lineItems = [];
      // initiate all objects.
      for(var i = 0; i < data.estimates.estimateLineItems.length; i ++){
        $.extend(data.estimates.estimateLineItems[i], new FacilityEstimateModel());
        $scope.lineItems.push(data.estimates.estimateLineItems[i]);
      }

      $scope.pageCount = $scope.lineItems.length / $scope.pageSize;
      data.estimates.estimateLineItems = [];//'data.estimates.estimateLineItems.slice(0, $scope.pageSize);'
      $scope.form = data.estimates;
      $scope.currentPage = 1;
      $scope.form.estimateLineItems = $scope.lineItems.slice( $scope.pageSize * ($scope.currentPage - 1), $scope.pageSize * $scope.currentPage);
    });
  };

  $scope.$watch('currentPage', function(){
    $scope.save();

    if(angular.isDefined($scope.lineItems)){
        $scope.form.estimateLineItems = $scope.lineItems.slice( $scope.pageSize * ($scope.currentPage - 1), $scope.pageSize * $scope.currentPage);
    }
  });

  $scope.save = function(){
    SaveFacilityDemographicEstimates.update($scope.form, function(data){
      $scope.message = "message.facility.demographic.estimates.saved";
    }, function(e){
      $scope.error = e.error;
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
