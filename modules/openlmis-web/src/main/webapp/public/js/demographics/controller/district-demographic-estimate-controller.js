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
function DistrictDemographicEstimateController($scope, $dialog, $filter, rights, categories, programs , years, DistrictDemographicEstimates, UndoFinalizeDistrictDemographicEstimates, FinalizeDistrictDemographicEstimates , SaveDistrictDemographicEstimates) {

  $.extend(this, new BaseDemographicEstimateController($scope, rights, categories, programs , years, $filter));

  $scope.bindEstimates = function(data){
    $scope.lineItems = [];
    // initiate all objects.
    for(var i = 0; i < data.estimates.estimateLineItems.length; i ++){
      $.extend(data.estimates.estimateLineItems[i], new DistrictEstimateModel());
      $scope.lineItems.push(data.estimates.estimateLineItems[i]);
    }

    $scope.pageCount = Math.round($scope.lineItems.length / $scope.pageSize);
    data.estimates.estimateLineItems = [];
    $scope.form = data.estimates;
    $scope.currentPage = 1;
    $scope.form.estimateLineItems = $scope.lineItems.slice( $scope.pageSize * ($scope.currentPage - 1), $scope.pageSize * $scope.currentPage);
    // todo - check if the list is partially final or not?
    // the list can be partially finalized if the rivo or civo are the once that see whatever is in their respective facilities.

    $scope.isFinalized = data.estimates.estimateLineItems[0].districtEstimates[0].isFinal;

    $scope.districtSummary = new AggregateRegionEstimateModel($scope.lineItems);

  };

  $scope.onParamChanged = function(){
    if(angular.isUndefined($scope.program) || $scope.program === null || angular.isUndefined($scope.year)){
      return;
    }

    DistrictDemographicEstimates.get({year: $scope.year, program: $scope.program}, function(data){
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
    SaveDistrictDemographicEstimates.update($scope.form, function(){
      $scope.message = "message.district.demographic.estimates.saved";
    }, function(data){
      $scope.error = data.error;
    });
  };

  $scope.finalize = function(){
    var callBack = function (result) {
      if (result) {
        var form = angular.copy($scope.form);
        form.estimateLineItems = $scope.lineItems;
        FinalizeDistrictDemographicEstimates.update(form, function (data) {
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
        var form = angular.copy($scope.form);
        form.estimateLineItems = $scope.lineItems;
        UndoFinalizeDistrictDemographicEstimates.update(form, function (data) {
          $scope.bindEstimates(data);
          $scope.message = 'Estimates are now available for editing.';
        });
      }
    };

    var options = {
      id: "confirmDialog",
      header: "label.confirm.undo.finalize.title",
      body: "label.confirm.undo.finalize.demographic.estimate"
    };

    OpenLmisDialog.newDialog(options, callBack, $dialog);
  };

  $scope.init();

}

