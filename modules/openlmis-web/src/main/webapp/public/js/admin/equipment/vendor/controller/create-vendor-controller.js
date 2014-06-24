/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function CreateVendorController($scope, $routeParams, $location, $dialog, messageService, Vendor, SaveVendor, UserListForVendor, UserListAvailableForVendor, SaveVendorUserAssociation, RemoveVendorUserAssociation) {
    $scope.vendorUser = {};
    $scope.loadUsersForVendor = function(){
        UserListForVendor.get({id:$routeParams.id},function(data){
            $scope.users = data.users;
        });
    };


    $scope.loadAvailableUsersForVendor = function(){
        UserListAvailableForVendor.get(function(data){
            $scope.allUsers = data.users;
            $scope.usersLoaded = true;
        });
    };

    $scope.loadAvailableUsersForVendor();


    if ($routeParams.id === undefined) {
        $scope.current = {};
    } else {
        Vendor.get({
            id: $routeParams.id
        }, function (data) {
            $scope.current = data.vendor;
            $scope.vendorUser.vendor = $scope.current; //The vendor user object holds the association with users
        });
        $scope.loadUsersForVendor();
    }

    $scope.save = function () {
        $scope.showError = true;
        if($scope.vendorForm.$valid){
            SaveVendor.save($scope.current, function (data) {
                // success
                $location.path('');
            }, function (data) {
                // error
                $scope.error = data.messages;
            });
        }
    };

    $scope.cancel = function () {
        $location.path('');
    };

    $scope.addNewVendorUserAssociation = function(){
        $scope.userVendorAssociationModal = true;
        $scope.selectedUser = null;
    };

    $scope.closeModal=function(){
        $scope.userVendorAssociationModal = false;
    };

    $scope.saveUserVendorAssociation = function (){
        var successHandler = function (response) {
            $scope.error = "";
            $scope.message = response.success;
            $scope.loadUsersForVendor();
        };

        var errorHandler = function (response) {
            $scope.error = messageService.get(response.data.error);
        };

        SaveVendorUserAssociation.save($scope.vendorUser, successHandler, errorHandler);
        $scope.closeModal();
    };

    $scope.showRemoveVendorUserAssociationConfirmDialog = function (index) {
        var userToRemove = $scope.users[index];
        $scope.userToRemove = userToRemove;
        $scope.index = index;
        var options = {
            id: "removeVendorUserAssociationConfirmDialog",
            header: "Confirmation",
            body: "Please confirm that you want to remove the association of this vendor with user: " + userToRemove.firstName
        };
        OpenLmisDialog.newDialog(options, $scope.removeVendorUserAssociationConfirm, $dialog, messageService);
    };

    $scope.removeVendorUserAssociationConfirm = function (result) {
        if (result) {
            $scope.users.splice($scope.index,1);
            $scope.removeVendorUserAssociation();
        }
        $scope.userToRemove = undefined;
        $scope.loadAvailableUsersForVendor();
    };

    $scope.removeVendorUserAssociation = function(){
        RemoveVendorUserAssociation.get({userId: $scope.userToRemove.id, vendorId: $scope.current.id});
    };

}