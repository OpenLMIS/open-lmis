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
     console.log(data);
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
      $scope.showError = true;
    });

  }

  $scope.updateName=function(isColdChain)
  {
    if(isColdChain)
    {
     $scope.equipment.name=$scope.equipment.manufacturer+' / '+$scope.equipment.model;
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
        $scope.equipment.designationId=$scope.equipment.designation.id;
      }
      else{
        $scope.equipment.equipmentTypeName = "equipment";
        $scope.equipment.designationId=$scope.equipment.designation.id;
      }
      SaveEquipment.save($scope.equipment, onSuccess, onError);
    }
  };

  $scope.cancelCreateEquipment = function () {
    $location.path('');
  };

}