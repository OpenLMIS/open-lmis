/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
function VaccineStorageController($scope, $location, $filter,ngTableParams, $route, $dialog, messageService, CreateVaccineStorage, UpdateVaccineStorage, VaccineStorageList, VaccineStorageDetail, DeleteVaccineStorage, StorageTypeList, TempratureList, StorageFacilityList) {


    $scope.disabled = false;
    $scope.title = 'Vaccine Storage List View';
    StorageFacilityList.get({}, function (data) {
        $scope.facillityList = data.facilityList;
    }, function (data) {
        $location.path($scope.$parent.sourceUrl);
    });
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
        if ($scope.vaccineStorage.id) {

            UpdateVaccineStorage.save($scope.vaccineStorage, createSuccessCallback, errorCallback);
        }
        else {
            CreateVaccineStorage.save($scope.vaccineStorage, createSuccessCallback, errorCallback);
        }
        $location.path('/vaccine-storage');
    };
    $scope.cancelEdit = function () {
        $location.path('/vaccine-storage');
    };
    $scope.editVaccineStorage = function (id) {
        if (id) {
            $location.path('/vaccine-storage-edit/' + id);
        }
    };
    $scope.deleteVaccineStorage = function (result) {
        if (result) {

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
            DeleteVaccineStorage.save($scope.vaccineStorage, deleteSuccessCallback, deleteErorCallback);
        }
    };
    $scope.showDeleteConfirmDialog = function (vaccineStorage) {
        $scope.vaccineStorage = vaccineStorage;
        var options = {
            id: "removeDonorMemberConfirmDialog",
            header: "Confirmation",
            body: "Are you sure you want to remove the Vaccine Storage: " + vaccineStorage.id
        };
        OpenLmisDialog.newDialog(options, $scope.deleteVaccineStorage, $dialog, messageService);
    };
    $scope.clearForm = function () {
        $scope.vaccineStorage = {};
    };

    //start of pagination////////////////////////////////////////////////

    // the grid options
    $scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        total: 0,           // length of data
        count: 25           // count per page
    });

    $scope.paramsChanged = function (params) {
        // slice array data on pages

        $scope.vaccineStorageList = [];
        VaccineStorageList.get({}, function (data) {
            $scope.data = data.vaccineStorageList;
        }, function (data) {
            $location.path($scope.$parent.sourceUrl);
        });
        params.total = $scope.data.length;

        var data = $scope.data;
        var orderedData = params.filter ? $filter('filter')(data, params.filter) : data;
        orderedData = params.sorting ? $filter('orderBy')(orderedData, params.orderBy()) : data;

        params.total = orderedData.length;
        $scope.vaccineStorageList = orderedData.slice((params.page - 1) * params.count, params.page * params.count);
        var i = 0;
        var baseIndex = params.count * (params.page - 1) + 1;

        while (i < $scope.vaccineStorageList.length) {

            $scope.vaccineStorageList[i].no = baseIndex + i;

            i++;

        }
    };

    // watch for changes of parameters
    $scope.$watch('tableParams', $scope.paramsChanged, true);

    $scope.getPagedDataAsync = function (pageSize, page) {
        // Clear the results on the screen
        $scope.vaccineStorageList = [];
        $scope.data = [];
        var params = {
            "max": 10000,
            "page": 1
        };

        $.each($scope.filterObject, function (index, value) {
            if (value !== undefined)
                params[index] = value;
        });
        $scope.paramsChanged($scope.tableParams);
    };
// end of pagination
}


