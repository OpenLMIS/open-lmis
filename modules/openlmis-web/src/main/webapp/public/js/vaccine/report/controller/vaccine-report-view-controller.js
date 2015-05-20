/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function ViewVaccineReportController($scope, programs, VaccineReportFacilities, ViewVaccineReportPeriods, messageService , $location) {

  $scope.programs = programs;

  $scope.onProgramChanged = function(){
    VaccineReportFacilities.get({programId: $scope.filter.program}, function(data){
      $scope.facilities = data.facilities;
    });
  };


  $scope.onFacilityChanged = function(){
    if(isUndefined($scope.filter.facility)){
      return;
    }
    ViewVaccineReportPeriods.get({facilityId: $scope.filter.facility, programId: $scope.filter.program}, function(data){
      $scope.periodGridData = data.periods;
      if($scope.periodGridData.length > 0){
        $scope.periodGridData[0].showButton = true;
      }
    });
  };

  $scope.view = function(period){
    if(!angular.isUndefined(period.id) && (period.id !== null)){
      // redirect already
      $location.path('/view/'+ period.id);
    }
  };

  function getActionButton(showButton){
    return '<a href="" class="padding2px" ng-click="view(row.entity)" openlmis-message="link.view" />';
  }

  $scope.periodGridOptions = { data: 'periodGridData',
    canSelectRows: false,
    displayFooter: false,
    displaySelectionCheckbox: false,
    enableColumnResize: true,
    enableColumnReordering: true,
    enableSorting: false,
    showColumnMenu: false,
    showFilter: false,
    columnDefs: [
      {field: 'periodName', displayName: messageService.get("label.periods")},
      {field: 'status', displayName: messageService.get("label.ivd.status") },
      {field: '', displayName: '', cellTemplate: getActionButton('row.entity.showButton')}
    ]
  };


  // load facility list for program.
  if(programs.length == 1){
    $scope.filter = {program: programs[0].id};
    $scope.onProgramChanged();
  }

}

ViewVaccineReportController.resolve = {
  programs: function($q, $timeout, VaccineSupervisedIvdPrograms){
    var deferred = $q.defer();

    $timeout(function(){
      VaccineSupervisedIvdPrograms.get({},function(data){
        deferred.resolve(data.programs);
      });
    },100);

    return deferred.promise;
  }
};