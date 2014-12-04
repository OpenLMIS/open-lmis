/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
function StorageTypeController($scope, $location,  $dialog, messageService,navigateBackService, CreateStorageType, UpdateStorageType,  DeleteStorageType, StorageTypeList,storageTypeList,StorageTypes) {


    $scope.disabled = false;
    $scope.storageType = {};

        $scope.storageTypes = storageTypeList;

//    storage type search

    $scope.showStorageTypeSearch = function () {

        var query = $scope.query;

        var len = (query === undefined) ? 0 : query.length;

        if (len >= 3) {

            if ($scope.previousQuery.substr(0, 3) === query.substr(0, 3)) {
                $scope.previousQuery = query;

                filterStorageByName(query);
                return true;
            }
            $scope.previousQuery = query;
            StorageTypes.get({param: $scope.query.substr(0, 3)}, function (data) {
                $scope.storageTypeList = data.storageTypeList;
                filterStorageByName(query);
            }, {});

            return true;
        } else {
            return false;
        }
    };

    $scope.previousQuery = '';
    $scope.query = navigateBackService.query;
    $scope.showStorageTypeSearch();
    var filterStorageByName = function (query) {
        $scope.filteredStorageTypes = [];
        query = query || "";

        angular.forEach($scope.storageTypeList, function (storageType) {
            var typeName = storageType.storageTypeName.toLowerCase();

            if (typeName.indexOf() >= 0 ||
                typeName.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0 ) {
                $scope.filteredStorageTypes.push(storageType);
            }
        });
        $scope.resultCount = $scope.filteredStorageTypes.length;
    };
//    end of search
    $scope.createStorageType = function () {

        $scope.error = "";
        if ($scope.storageTypeForm.$invalid) {
            $scope.showError = true;

            $scope.errorMessage = "The form you submitted is invalid. Please revise and try again.";
            return;
        }

        var createSuccessCallback = function (data) {
            StorageTypeList.get({}, function (data) {
                $scope.storageTypes = data.storageTypeList;
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
        $location.path('/storage-type');
    };
    $scope.cancelEdit=function(){
        $location.path('/storage-type');
    };
    $scope.clearSearch = function () {
        $scope.query = "";
        $scope.resultCount = 0;
        angular.element("#searchStorageType").focus();
        $location.path('/storage-type');
    };
    $scope.editStorageType = function (id) {
        if (id) {

            $location.path('/storage-type-manage/'+id);
        }
    };
    $scope.deleteStorageType = function (result) {
        if (result) {

            var deleteSuccessCallback = function (data) {
                $scope.$parent.message = 'Storage type Deleted Successfully';

                $scope.storageType = {};
                StorageTypeList.get({}, function (data) {
                    $scope.storageTypes = data.storageTypeList;
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
        $location.path('/storage-type-create');
    };


}
StorageTypeController.resolve = {
    storageTypeList: function ($q, $timeout, StorageTypes) {
        var deferred = $q.defer();

        $timeout(function () {
            // show the list of users by a default
            StorageTypes.get({param: ''}, function(data){
                deferred.resolve( data.storageTypeList );
            },{});

        }, 100);
        return deferred.promise;
    }
};

