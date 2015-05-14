/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function VaccineReportController($scope, programs, VaccineReportFacilities, VaccineReportPeriods, VaccineReportInitiate, $location) {

  $scope.programs = programs;

  $scope.onProgramChanged = function(){
    VaccineReportFacilities.get({programId: $scope.filter.program}, function(data){
      $scope.facilities = data.facilities;
    });
  };


  $scope.onFacilityChanged = function(){
    VaccineReportPeriods.get({facilityId: $scope.filter.facility, programId: $scope.filter.program}, function(data){
      $scope.periods = data.periods;
    });
  };

  $scope.initiate = function(period){
    if(!angular.isUndefined(period.id) && (period.id !== null)){
      // redirect already
      $location.path('/create/'+ period.id);
    }else{
      // initiate
      VaccineReportInitiate.get({ periodId: period.periodId, facilityId: period.facilityId, programId: period.programId}, function(data){
        $location.path('/create/'+ data.report.id);
      });
    }
  };

}

VaccineReportController.resolve = {
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