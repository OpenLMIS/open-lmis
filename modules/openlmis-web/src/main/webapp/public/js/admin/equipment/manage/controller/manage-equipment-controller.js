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

function ManageEquipmentController($scope, $routeParams,$dialog, $location,messageService, Equipments,EquipmentTypes,EquipmentType,RemoveEquipment,currentEquipmentTypeId,ProgramCompleteList,EquipmentTypesByProgram,currentProgramId,ColdChainPqsStatus,SaveEquipment,$timeout) {

    //Load All Equipment Types if No program filter applied
    $scope.getAllEquipmentTypes = function () {
        EquipmentTypes.get(function (data) {
          $scope.equipmentTypes = data.equipment_type;
          });
     };

    //Load Equipment types by program if program filter applied
    $scope.getAllEquipmentTypesByProgram = function (initLoad) {
            currentProgramId.set($scope.programId);
            if(initLoad){
                    $scope.equipments={};
                    $scope.equipmentTypeId=undefined;
                    currentEquipmentTypeId.set($scope.equipmentTypeId);
             }
            EquipmentTypesByProgram.get({programId: $scope.programId}, function (data) {
                $scope.equipmentTypes = data.equipment_types;
              });
         };

    $scope.getAllPrograms = function () {
        ProgramCompleteList.get(function (data) {
          $scope.programs = data.programs;
        });
      };


   $scope.listEquipments=function(initLoad)
   {

      if(initLoad)
      {
            $scope.page=1;
      }
      var id=$scope.equipmentTypeId;
      currentEquipmentTypeId.set($scope.equipmentTypeId);
      Equipments.get({
           equipmentTypeId:id,
           page: $scope.page
           },function (data) {
               $scope.equipments = data.equipments;
               $scope.pagination = data.pagination;
               $scope.totalItems = $scope.pagination.totalRecords;
               $scope.currentPage = $scope.pagination.page;
       });
      EquipmentType.get({
            id: id
          }, function (data) {
            $scope.equipment_type = data.equipment_type;
       });

      ColdChainPqsStatus.get(function (data) {
              $scope.pqsStatus = data.pqs_status;
       });

   };

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
             $timeout(function () {
                 $scope.$parent.message = false;
             }, 3000);
             $scope.listEquipments();
           }, function (result) {
             $scope.$parent.error = messageService.get(result.data.error);
             $timeout(function () {
               $scope.$parent.error = false;
             }, 3000);
           });

         }
         $scope.selectedEquipment=undefined;
       };

     $scope.getAllPrograms();
     $scope.programId=currentProgramId.get();

     if( $scope.programId !== undefined)
     {
         $scope.getAllEquipmentTypesByProgram(false);
     }

     $scope.equipmentTypeId=currentEquipmentTypeId.get();
     if( $scope.equipmentTypeId !== undefined)
     {
          $scope.listEquipments(true);
     }

     $scope.updatePqsStatus=function(eq){
        $scope.equipment=eq;
        $scope.equipment.equipmentTypeName = "coldChainEquipment";
        var onSuccess = function(data){
             eq.showSuccess = true;
             $timeout(function () {
                eq.showSuccess = false;
              }, 2000);
          };
         var onError = function(data){

         };
        SaveEquipment.save($scope.equipment, onSuccess, onError);
     };
     if($scope.$parent.message)
     {
        $timeout(function () {
          $scope.$parent.message = false;
        }, 3000);
     }

     $scope.$watch('currentPage', function () {
         if ($scope.currentPage > 0) {
           $scope.page = $scope.currentPage;
           $scope.listEquipments(false);
         }
       });
}