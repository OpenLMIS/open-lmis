/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function CreateEquipmentController($scope, $routeParams, $location, Equipment,EquipmentType, EquipmentTypes, SaveEquipment, messageService, ColdChainDesignations,ColdChainPqsStatus,EquipmentEnergyTypes, Donors,currentEquipmentTypeId) {

  $scope.currentEquipmentTypeId=currentEquipmentTypeId.get();
  if( $scope.currentEquipmentTypeId === undefined)
  {
    $location.path('');
  }

  EquipmentEnergyTypes.get(function (data) {
        $scope.energyTypes = data.energy_types;
   });

  ColdChainDesignations.get(function (data) {
     $scope.designations = data.designations;
  });

  ColdChainPqsStatus.get(function (data) {
       $scope.pqsStatus = data.pqs_status;
    });

  // clear the message when this page is loaded.
  $scope.$parent.message = '';

  if ($routeParams.id === undefined) {
    $scope.equipment = {};
     EquipmentType.get({
        id:$scope.currentEquipmentTypeId
      },function (data) {
        $scope.equipment.equipmentType = data.equipment_type;
        $scope.equipment.equipmentTypeId=$scope.equipment.equipmentType.id;
      });

  } else {
    Equipment.get({
      id: $routeParams.id,
      equipmentTypeId:$routeParams.type
    }, function (data) {
      $scope.equipment = data.equipment;
      $scope.checkEquipmentType();
      $scope.showError = true;
    });

  }

  $scope.updateName=function(isColdChain)
  {
    if(isColdChain)
    {
     $scope.equipment.name=$scope.equipment.manufacturer+' / '+$scope.equipment.model;
     $scope.equipment.code=$scope.equipment.manufacturer+'-'+$scope.equipment.model;
    }
  };

  $scope.saveEquipment = function () {
    var onSuccess = function(data){
      $scope.$parent.message = messageService.get(data.success);
      $location.path('');
    };

    var onError = function(data){
      $scope.showError = true;
      $scope.error = messageService.get(data.data.error);
    };

    if(!$scope.equipmentForm.$invalid){
      if($scope.equipment.equipmentType.coldChain)
      {
        $scope.equipment.equipmentTypeName = "coldChainEquipment";
      }
      else{
        $scope.equipment.equipmentTypeName = "equipment";
      }
      SaveEquipment.save($scope.equipment, onSuccess, onError);
    }
  };

  $scope.cancelCreateEquipment = function () {
    $location.path('');
  };

}