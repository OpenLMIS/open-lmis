/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
function VaccineStorageController($scope, $location, $route, $dialog, messageService, CreateVaccineStorage,
                                  UpdateVaccineStorage,VaccineStorageList,VaccineStorageDetail,DeleteVaccineStorage,StorageTypeList ,TempratureList) {




    $scope.disabled = false;
    VaccineStorageList.get({}, function (data) {
        $scope.vaccineStorageList = data.vaccineStorageList;
    }, function (data) {
        $location.path($scope.$parent.sourceUrl);
    });
    StorageTypeList.get({}, function (data) {
        $scope.storageTypeList = data.storageTypeList;
    }, function (data) {
        $location.path($scope.$parent.sourceUrl);
    });
    TempratureList.get({}, function (data) {
        $scope.tempratureList = data.temperatureList;
    }, function (data) {
        $location.path($scope.$parent.sourceUrl);
    });

    $scope.createVaccineStorage = function () {

        $scope.error = "";
        if ($scope.vaccineStorageForm.$invalid) {
            $scope.showError = true;

            $scope.errorMessage = "The form you submitted is invalid. Please revise and try again.";
            return;
        }

        var createSuccessCallback = function (data) {
            VaccineStorageList.get({}, function (data) {
                $scope.vaccineStorageList = data.vaccineStorageList;
            }, function (data) {
                $location.path($scope.$parent.sourceUrl);
            });
            $scope.$parent.message = 'New Vaccine Storage Information created successfully';

            $scope.vaccineStorage = {};
        };

        var errorCallback = function (data) {
            $scope.showError = true;

            $scope.errorMessage = messageService.get(data.data.error);
        };
                $scope.error = "";
        if($scope.vaccineStorage.id){

            UpdateVaccineStorage.save($scope.vaccineStorage, createSuccessCallback, errorCallback);
        }
        else{
            CreateVaccineStorage.save($scope.vaccineStorage, createSuccessCallback, errorCallback);
        }

    };

    $scope.editVaccineStorage=function(id){
        if(id){
            VaccineStorageDetail.get({id:id}, function(data){
                $scope.vaccineStorage = data.vaccineStorage;
//            if($scope.editHelpTopic.active === false){
//                $scope.disableAllFields();
//            }
            });
        }
    };
    $scope.deleteVaccineStorage=function(result){
        if(result){

            var deleteSuccessCallback = function (data) {
                $scope.$parent.message = 'New Vaccine Storage Information created successfully';

                $scope.vaccineStorage = {};
                VaccineStorageList.get({}, function (data) {
                    $scope.vaccineStorageList = data.vaccineStorageList;
                }, function (data) {
                    $location.path($scope.$parent.sourceUrl);
                });
            };

            var deleteErorCallback = function (data) {
                $scope.showError = true;

                $scope.errorMessage = messageService.get(data.data.error);
            };
            DeleteVaccineStorage.save( $scope.vaccineStorage,deleteSuccessCallback,deleteErorCallback);
        }
    };
    $scope.showDeleteConfirmDialog = function (vaccineStorage) {
        $scope.vaccineStorage=vaccineStorage;
        var options = {
            id: "removeDonorMemberConfirmDialog",
            header: "Confirmation",
            body: "Are you sure you want to remove the Vaccine Storage: "+vaccineStorage.id
        };
        OpenLmisDialog.newDialog(options,$scope.deleteVaccineStorage, $dialog, messageService);
    };
$scope.clearForm=function(){
    $scope.vaccineStorage = {};
};
}


