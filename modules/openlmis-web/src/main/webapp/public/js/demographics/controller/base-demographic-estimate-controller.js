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


function BaseDemographicEstimateController($scope, rights, categories, programs , years, $filter) {

  $scope.currentPage = 1;
  $scope.pageSize = 10;

  $scope.categories = categories;
  $scope.rights = rights;
  $scope.years = years;
  $scope.programs = programs;


  $scope.isDirty = function () {
    return $scope.$dirty;
  };

  $scope.hasPermission = function (permission) {
    return ($scope.rights.indexOf(permission) >= 0);
  };

  $scope.showParent = function (index) {
    if (index > 0) {
      return ($scope.form.estimateLineItems[index].parentName !== $scope.form.estimateLineItems[index - 1].parentName);
    }
    return true;
  };


  $scope.init = function(){
    // default to the current year
    $scope.year = Number( $filter('date')(new Date(), 'yyyy') );
    // when the available program is only 1, default to this program.
    if(programs.length === 1){
      $scope.program = programs[0].id;
    }
    $scope.onParamChanged();
  };

}

BaseDemographicEstimateController.resolve = {

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
  }, programs: function($q, $timeout, DemographicEstimatePrograms){
    var deferred = $q.defer();
    $timeout(function(){
      DemographicEstimatePrograms.get({}, function(data){
        deferred.resolve(data.programs);
      });
    },100);
    return deferred.promise;
  }, rights: function ($q, $timeout, UserSupervisoryRights) {
    var deferred = $q.defer();
    $timeout(function () {
      UserSupervisoryRights.get({}, function (data) {
        deferred.resolve(data.rights);
      }, {});
    }, 100);
    return deferred.promise;
  }


};
