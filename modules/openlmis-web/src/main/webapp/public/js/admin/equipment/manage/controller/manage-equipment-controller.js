/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function ManageEquipmentController($scope, $routeParams,$dialog, $location,messageService, Equipments,EquipmentTypes,EquipmentType,RemoveEquipment,currentEquipmentTypeId) {

   EquipmentTypes.get(function (data) {
      $scope.equipmentTypes = data.equipment_type;
    });

   $scope.listEquipments=function()
   {
      var id=$scope.equipmentTypeId;
      currentEquipmentTypeId.set($scope.equipmentTypeId);
      Equipments.get({
           equipmentTypeId:id
           },function (data) {
               $scope.equipments = data.equipments;
       });
      EquipmentType.get({
            id: id
          }, function (data) {
            $scope.equipment_type = data.equipment_type;
       });
   };

   $scope.currentEquipmentTypeId=currentEquipmentTypeId.get();
   $scope.equipmentTypeId=$scope.currentEquipmentTypeId;
   if( $scope.currentEquipmentTypeId !== undefined)
   {
    $scope.listEquipments();
   }

   var ASC=true;
   $scope.sortBy=function(title){
        if(ASC){
          $scope.orderTitle=title;
          ASC=false;
        }
        else{
          $scope.orderTitle='-'+title;
          ASC=true;
        }

   };

   $scope.showRemoveEquipmentConfirmDialog = function (id) {
       $scope.selectedEquipment=id;
       var options = {
         id: "removeEquipmentConfirmDialog",
         header: "Confirmation",
         body: "Are you sure you want to remove the Equipment"
       };
       OpenLmisDialog.newDialog(options, $scope.removeEquipmentConfirm, $dialog, messageService);
     };

     $scope.removeEquipmentConfirm = function (result) {
         if (result) {
           RemoveEquipment.get({equipmentTypeId:$scope.equipmentTypeId, id: $scope.selectedEquipment}, function (data) {
             $scope.$parent.message = messageService.get(data.success);

             $scope.listEquipments();
           }, function () {
             $scope.error = messageService.get(data.error);
           });

         }
         $scope.selectedEquipment=undefined;
       };


}