/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function EquipmentInventoryController($scope, UserFacilityList, EquipmentInventories, CreateRequisitionProgramList, UserSupervisedFacilitiesForProgram,navigateBackService, $routeParams, messageService ) {

  if($routeParams.selectedType !== undefined){
    $scope.selectedType = $routeParams.selectedType;
    $scope.selectedFacilityId = $routeParams.facilityId;
  }
  
  $scope.$on('$viewContentLoaded', function () {
    $scope.selectedType = navigateBackService.selectedType || "0";
    $scope.selectedProgram = navigateBackService.selectedProgram;
    $scope.selectedFacilityId = navigateBackService.selectedFacilityId;
    isNavigatedBack = navigateBackService.isNavigatedBack;
    $scope.$watch('programs', function () {
      isNavigatedBack = navigateBackService.isNavigatedBack;
      if (!isNavigatedBack) $scope.selectedProgram = undefined;
      if ($scope.programs && !isUndefined($scope.selectedProgram)) {
        $scope.selectedProgram = _.where($scope.programs, {id: $scope.selectedProgram.id})[0];
      }
    });
    
    if($scope.programs && $routeParams.programId !== undefined){
      $scope.selectedProgram = _.where($scope.programs, {id: $routeParams.programId})[0];
    }
    
    $scope.loadFacilityData($scope.selectedType);
    if (isNavigatedBack) {
      $scope.loadFacilitiesForProgram();
    }
    $scope.$watch('facilities', function () {
      if ($scope.facilities && isNavigatedBack) {
        $scope.selectedFacilityId = navigateBackService.selectedFacilityId;
        isNavigatedBack = false;
      }
    });
  });

  $scope.loadFacilityData = function (selectedType) {
    isNavigatedBack = isNavigatedBack ? selectedType !== "0" : ($scope.selectedProgram = undefined , $scope.selectedFacilityId = undefined);

    if (selectedType === "0") { //My facility
      UserFacilityList.get({}, function (data) {
        $scope.facilities = data.facilityList;
        $scope.myFacility = data.facilityList[0];
        if ($scope.myFacility) {
          $scope.facilityDisplayName = $scope.myFacility.code + '-' + $scope.myFacility.name;
          $scope.selectedFacilityId = $scope.myFacility.id;
          CreateRequisitionProgramList.get({facilityId: $scope.selectedFacilityId}, function (data) {
            $scope.programs = data.programList;
            if($scope.programs && $routeParams.programId !== undefined){
              $scope.selectedProgram = _.where($scope.programs, {id: $routeParams.programId})[0];
            }
            
          }, {});
        } else {
          $scope.facilityDisplayName = messageService.get("label.none.assigned");
          $scope.programs = null;
          $scope.selectedProgram = null;
        }
      }, {});
    } else if (selectedType === "1") { // Supervised facility
      CreateRequisitionProgramList.get({}, function (data) {
        $scope.programs = data.programList;
        
        if($scope.programs && $routeParams.programId !== undefined){
          $scope.selectedProgram = _.where($scope.programs, {id: $routeParams.programId})[0];
        }
      }, {});
    }
  };

  $scope.loadFacilitiesForProgram = function () {
    if ($scope.selectedProgram.id) {
      UserSupervisedFacilitiesForProgram.get({programId: $scope.selectedProgram.id}, function (data) {
        $scope.facilities = data.facilities;
        $scope.selectedFacilityId = null;
        $scope.error = null;
      }, {});
    } else {
      $scope.facilities = null;
      $scope.selectedFacilityId = null;
    }
  };

  $scope.loadEquipments = function(){
    if($scope.selectedProgram !== undefined){
      EquipmentInventories.get({programId: $scope.selectedProgram.id, facilityId: $scope.selectedFacilityId}, function(data){
          $scope.inventory = data.inventory;
        });
    }
  };

}