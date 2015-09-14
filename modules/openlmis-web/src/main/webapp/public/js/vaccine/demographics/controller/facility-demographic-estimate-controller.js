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
function FacilityDemographicEstimateController($scope, $dialog, $filter, rights, categories, years, programs, FacilityDemographicEstimates, SaveFacilityDemographicEstimates, FinalizeFacilityDemographicEstimates, UndoFinalizeFacilityDemographicEstimates) {

  $scope.currentPage = 1;
  $scope.pageSize = 15;

  $scope.categories = categories;
  $scope.rights = rights;
  $scope.years = years;
  $scope.programs = programs;


  $scope.isDirty = function(){
    return $scope.$dirty;
  };

  $scope.hasPermission = function (permission) {
    return ($scope.rights.indexOf(permission) >= 0);
  };

  $scope.showParent = function(index){
    if(index > 0){
      return ($scope.form.estimateLineItems[index].parentName != $scope.form.estimateLineItems[index - 1].parentName);
    }
    return true;
  };

  $scope.bindEstimates = function(data){
    $scope.lineItems = [];
    // initiate all objects.
    for(var i = 0; i < data.estimates.estimateLineItems.length; i ++){
      $.extend(data.estimates.estimateLineItems[i], new FacilityEstimateModel());
      $scope.lineItems.push(data.estimates.estimateLineItems[i]);
    }

    $scope.pageCount = $scope.lineItems.length / $scope.pageSize;
    data.estimates.estimateLineItems = [];
    $scope.form = data.estimates;
    $scope.currentPage = 1;
    $scope.form.estimateLineItems = $scope.lineItems.slice( $scope.pageSize * ($scope.currentPage - 1), $scope.pageSize * $scope.currentPage);
    // todo - check if the list is partially final or not?
    // the list can be partially finalized if the rivo or civo are the once that see whatever is in their respective facilities.

    $scope.isFinalized = data.estimates.estimateLineItems[0].facilityEstimates[0].isFinal;

    $scope.districtSummary = new DistrictEstimateModel($scope.lineItems);

  };

  $scope.onParamChanged = function(){
    if(angular.isUndefined($scope.program) || angular.isUndefined($scope.year)){
      return;
    }

    FacilityDemographicEstimates.get({year: $scope.year, program: $scope.program}, function(data){
      $scope.bindEstimates(data);
    });
  };



  $scope.$watch('currentPage', function(){
    if($scope.isDirty()){
      $scope.save();
    }


    if(angular.isDefined($scope.lineItems)){
        $scope.form.estimateLineItems = $scope.lineItems.slice( $scope.pageSize * ($scope.currentPage - 1), $scope.pageSize * $scope.currentPage);
    }
  });

  $scope.save = function(){
    SaveFacilityDemographicEstimates.update($scope.form, function(data){
      $scope.message = "message.facility.demographic.estimates.saved";
    }, function(data){
      $scope.error = data.error;
    });
  };



  $scope.finalize = function(){
    var callBack = function (result) {
      if (result) {
        $scope.form.estimateLineItems = $scope.lineItems;
        FinalizeFacilityDemographicEstimates.update($scope.form, function (data) {
          $scope.bindEstimates(data);
          $scope.message = 'Estimates are now finalized';
        });
      }
    };

    var options = {
      id: "confirmDialog",
      header: "label.confirm.finalize.title",
      body: "label.confirm.finalize.demographic.estimate"
    };

    OpenLmisDialog.newDialog(options, callBack, $dialog);
  };

  $scope.undoFinalize = function(){
    var callBack = function (result) {
      if (result) {
        $scope.form.estimateLineItems = $scope.lineItems;
        UndoFinalizeFacilityDemographicEstimates.update($scope.form, function (data) {
          $scope.bindEstimates(data);
          $scope.message = 'Estimates are now available for editing.';
        });
      }
    };

    var options = {
      id: "confirmDialog",
      header: "label.confirm.undo.finalize.title",
      body: "label.confirm.finalize.demographic.estimate"
    };

    OpenLmisDialog.newDialog(options, callBack, $dialog);
  };

  $scope.init = function(){
    // default to the current year
    $scope.year = Number( $filter('date')(new Date(), 'yyyy') );
    // when the available program is only 1, default to this program.
    if(programs.length == 1){
      $scope.program = programs[0].id;
    }
    $scope.onParamChanged();
  };

  $scope.init();
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
