/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */


function ViewRequestController($scope, $location, $routeParams, EquipmentInventory, Equipment, SaveMaintenanceRequest, Vendors) {

    $scope.current = {};

    $scope.current.inventoryId = $routeParams.id;
    $scope.current.facilityId = $routeParams.facilityId;
    $scope.current.vendorId = 1;

    Vendors.get(function(data){
        $scope.vendors = data.vendors;
    });


    EquipmentInventory.get({
        id: $routeParams.id
    }, function (data) {
        $scope.equipment = data.inventory;
        Equipment.get({id: data.inventory.equipmentId}, function(d){
            $scope.equipment.name = d.equipment.name;
        });
    });

    $scope.cancel = function(){
      $location.path('');
    };

    $scope.save = function(){

        if($scope.requestForm.$valid){
            SaveMaintenanceRequest.save($scope.current, function(data){
                $location.path('');
            });
        }else{
            $scope.showError = true;
        }
    };
}