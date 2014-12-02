/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
function CreateVaccineReportController($scope, $location, report, VaccineReportSave, VaccineReportSubmit) {
  // initial state of the display
  $scope.report = report;
  $scope.visibleTab = 'stockMovement';


  $scope.save = function(){
    VaccineReportSave.update($scope.report, function(data){
      $scope.message = "Report Saved Successfully.";
    });
  };

  $scope.submit = function(){
    VaccineReportSubmit.update($scope.report, function(data){
      $location.path('/');
    });
  };

  $scope.cancel = function(){
    $location.path('/');
  };


  $scope.showAdverseEffect = function(effect, editMode){
    $scope.currentEffect = effect;
    $scope.currentEffectMode = editMode;

    $scope.adverseEffectModal = true;
  };

  $scope.applyAdverseEffect = function(){
    if(!$scope.currentEffectMode){
      $scope.report.adverseEffectLineItems.push($scope.currentEffect);
    }
    $scope.adverseEffectModal = false;
  };

  $scope.closeAdverseEffectsModal = function(){
    $scope.adverseEffectModal = false;
  }

  $scope.showCampaignForm = function(campagin, editMode){

    $scope.currentCampaign = campagin;
    $scope.currentCampaignMode = editMode;

    $scope.campaignsModal = true;
  };

  $scope.applyCampaign = function(){
    if(!$scope.currentCampaignMode){
      $scope.report.campaignLineItems.push($scope.currentCampaign);
    }
    $scope.campaignsModal = false;
  };

  $scope.closeCampaign = function(){
    $scope.campaignsModal=false;
  };


}


CreateVaccineReportController.resolve = {
  report: function($q, $timeout, $route, VaccineReport) {
    var deferred = $q.defer();

    $timeout(function(){
      VaccineReport.get({id: $route.current.params.id}, function(data){
        deferred.resolve(data.report);
      });
    }, 100);
    return deferred.promise;
  }
};