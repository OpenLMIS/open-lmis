/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function CreateEquipmentInventoryController($scope, $location, $routeParams, EquipmentInventory, Donors ,Equipments, SaveEquipmentInventory, Facility, EquipmentOperationalStatus, messageService, EquipmentTypesByProgram) {

  $scope.$parent.message = $scope.$parent.error = '';

  $scope.max_year = new Date().getFullYear();
  $scope.submitted = false;
  $scope.showError = false;

  Equipments.get(function (data) {
    $scope.equipments = data.equipments;
  });

  EquipmentTypesByProgram.get({programId: $routeParams.program}, function (data) {
    for (var i = 0; i < data.equipment_types.length; i++) {
      if (data.equipment_types[i].id.toString() === $routeParams.equipmentType) {
        $scope.equipmentType = data.equipment_types[i];
      }
    }
  }, {});

  if ($routeParams.id === undefined) {
    $scope.screenType = 'create';
    $scope.inventory = {};
    $scope.inventory.programId = $routeParams.program;
    $scope.inventory.facilityId = $routeParams.facility;

    Facility.get({id: $routeParams.facility}, function(data){
      $scope.facility = data.facility;
    });

    // set default of checkboxes so the submission does not become null and hence an error.
    $scope.inventory.replacementRecommended = false;
    $scope.inventory.dateLastAssessed = Date.now();
    $scope.inventory.isActive = true;

  } else {
    $scope.screenType = 'edit';
    EquipmentInventory.get({
      id: $routeParams.id
    }, function (data) {
      $scope.inventory = data.inventory;
      $scope.inventory.dateLastAssessed = $scope.inventory.dateLastAssessedString ;
      $scope.inventory.dateDecommissioned = $scope.inventory.dateDecommissionedString;
      Facility.get({ id: $scope.inventory.facilityId }, function(data){
        $scope.facility = data.facility;
      });
    });
  }

  EquipmentOperationalStatus.get(function(data){
     $scope.operationalStatusList = data.status;
  });

  Donors.get(function(data){
    $scope.donors = data.donors;
  });

  $scope.saveInventory = function () {
    $scope.error = '';
    $scope.showError = true;
    if(!$scope.inventoryForm.$invalid ){
      SaveEquipmentInventory.save($scope.inventory, function (data) {
        $scope.$parent.message = messageService.get(data.success);
        $scope.$parent.selectedProgram = {id: $scope.inventory.programId};
        console.info($scope.$parent.selectedProgram);
        $location.path('/' + $routeParams.from + '/' + $scope.inventory.facilityId + '/' + $scope.inventory.programId + '/' + $routeParams.equipmentType);
      }, function (data) {
        $scope.error = data.error;
      });
    }else{
      $scope.submitted = true;
      $scope.error = messageService.get('message.equipment.inventory.data.invalid');
    }
  };

  $scope.cancelCreateInventory = function () {
    $location.path('');
  };
}