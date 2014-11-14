/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
function StorageTypeController($scope, $location, $route, $dialog, messageService, CreateStorageType, UpdateStorageType, VaccineStorageList, StorageTypeDetail, DeleteStorageType, StorageTypeList) {


    $scope.disabled = false;
    $scope.storageType = {};
    StorageTypeList.get({}, function (data) {
        $scope.storageTypeList = data.storageTypeList;
    }, function (data) {
        $location.path($scope.$parent.sourceUrl);
    });
    $scope.createStorageType = function () {

        $scope.error = "";
        if ($scope.storageTypeForm.$invalid) {
            $scope.showError = true;

            $scope.errorMessage = "The form you submitted is invalid. Please revise and try again.";
            return;
        }

        var createSuccessCallback = function (data) {
            StorageTypeList.get({}, function (data) {
                $scope.storageTypeList = data.storageTypeList;
            }, function (data) {
                $location.path($scope.$parent.sourceUrl);
            });
            $scope.$parent.message = 'New Vaccine Storage Type created successfully';

            $scope.storageType = {};
        };

        var errorCallback = function (data) {
            $scope.showError = true;

            $scope.errorMessage = messageService.get(data.data.error);
        };
        $scope.error = "";
        if ($scope.storageType.id) {

            UpdateStorageType.save($scope.storageType, createSuccessCallback, errorCallback);
        }
        else {

            CreateStorageType.save($scope.storageType, createSuccessCallback, errorCallback);
        }

    };

    $scope.editStorageType = function (id) {
        if (id) {
            StorageTypeDetail.get({id: id}, function (data) {
                $scope.storageType = data.storageType;
//            if($scope.editHelpTopic.active === false){
//                $scope.disableAllFields();
//            }
            });
        }
    };
    $scope.deleteStorageType = function (result) {
        if (result) {

            var deleteSuccessCallback = function (data) {
                $scope.$parent.message = 'Storage type Deleted Successfully';

                $scope.storageType = {};
                StorageTypeList.get({}, function (data) {
                    $scope.storageTypeList = data.storageTypeList;
                }, function (data) {
                    $location.path($scope.$parent.sourceUrl);
                });
            };

            var deleteErorCallback = function (data) {
                $scope.showError = true;

                $scope.errorMessage = messageService.get(data.data.error);
            };
            DeleteStorageType.save($scope.storageType, deleteSuccessCallback, deleteErorCallback);
        }
    };
    $scope.showDeleteConfirmDialog = function (storageType) {
        $scope.storageType = storageType;
        var options = {
            id: "removeStorageTypeConfirmDialog",
            header: "Confirmation",
            body: "Are you sure you want to remove the Storage Type: " + storageType.storageTypeName
        };
        OpenLmisDialog.newDialog(options, $scope.deleteStorageType, $dialog, messageService);
    };
    $scope.clearForm = function () {
        $scope.storageType = {};
    };
}


