/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
function VaccineStorageUpdateController($scope, $location, $route, messageService, UpdateVaccineStorage, VaccineStorageDetail, StorageTypeList, TempratureList, StorageFacilityList) {

    $scope.vaccineStorage = {};
    $scope.startVaccineEdit = function (id) {

        VaccineStorageDetail.get({id: id}, function (data) {
            $scope.vaccineStorage = data.vaccineStorage;
        });
    };
    StorageFacilityList.get({}, function (data) {
        $scope.facillityList = data.facilityList;
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

            $scope.$parent.message = 'Vaccine Storage Information updated successfully';

            $scope.vaccineStorage = {};
        };

        var errorCallback = function (data) {
            $scope.showError = true;

            $scope.errorMessage = messageService.get(data.data.error);
        };
        $scope.error = "";


        UpdateVaccineStorage.save($scope.vaccineStorage, createSuccessCallback, errorCallback);


        $location.path('/vaccine-storage');
    };
    $scope.cancelEdit = function () {
        $location.path('/vaccine-storage');
    };

    $scope.startVaccineEdit($route.current.params.id);
}
