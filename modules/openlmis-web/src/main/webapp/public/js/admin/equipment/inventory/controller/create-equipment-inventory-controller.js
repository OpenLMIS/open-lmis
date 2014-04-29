/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function CreateEquipmentInventoryController($scope, $location, $routeParams, EquipmentInventory, Equipments, SaveEquipmentInventory, Facility) {

  $scope.submitted = false;
  $scope.showError = false;
  Equipments.get(function (data) {
    $scope.equipments = data.equipments;
  });

  if ($routeParams.id === undefined) {
    $scope.equipment = {};
    $scope.equipment.programId = $routeParams.programId;
    $scope.equipment.facilityId = $routeParams.facilityId;

    Facility.get({id: $routeParams.facilityId}, function(data){
      $scope.facility = data.facility;
    });

    $scope.equipment.replacementRecommended = false;
    $scope.equipment.dateLastAssessed = Date.now();
  } else {
    EquipmentInventory.get({
      id: $routeParams.id
    }, function (data) {
      $scope.equipment = data.inventory;


      Facility.get({id: $scope.equipment.facilityId}, function(data){
        $scope.facility = data.facility;
      });

    });
  }


  $scope.saveEquipment = function () {
    $scope.error = '';
    $scope.showError = true;
    if($scope.equipmentForm.$valid ){
      SaveEquipmentInventory.save($scope.equipment, function (data) {
        // success
        if($routeParams.from !== undefined){
          $location.path('/' + $routeParams.from + '/' + $routeParams.facilityId + '/' + $routeParams.programId);
        }else{
          $location.path('/0/' + $scope.equipment.facilityId + '/' + $scope.equipment.programId);
        }
      }, function (data) {
        // error
        $scope.error = data.messages;
      });
    }else{
      $scope.submitted = true;
      $scope.error = 'Please correct errors on form';
    }
  };

  $scope.cancelCreateEquipment = function () {
    $location.path('');
  };
}