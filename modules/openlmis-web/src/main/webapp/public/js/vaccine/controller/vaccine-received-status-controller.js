/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */


function VaccineReceivedStatusController($scope, $dialog, messageService, $routeParams, $location, VaccineReceivedStatusList, GetVaccineReceivedStatus,
                                         SearchVaccineReceivedStatus, VaccineReceivedStatusSave, DeleteVaccineReceivedStatus, VaccineTransactionTypeList) {

    $scope.vaccineReceivedStatus = {};


    if (isUndefined($routeParams.id) || $routeParams.id === 0) {

        loadVaccineReceivedStatusList();

    } else {

        $scope.$parent.message = '';

        GetVaccineReceivedStatus.get({
            id: $routeParams.id
        }, function (data) {
            $scope.receivedStatus = data.receivedStatus;
        });

        VaccineTransactionTypeList.get({}, function(data){
            $scope.transactionTypes = data.transactionTypes;
            $scope.transactionTypes.unshift({
                name : '--Select Administration mode--'
            });
        });
    }

    $scope.saveVaccineReceivedStatus = function () {

        if ($scope.vaccineReceivedStatusForm.$error.pattern || $scope.vaccineReceivedStatusForm.$error.min || $scope.vaccineReceivedStatusForm.$error.required) {
            $scope.showError = "true";
            $scope.error = 'form.error';
            $scope.message = "";
            return;
        }

        var onSuccess = function (data) {
            $scope.$parent.message = 'Your changes have been saved!';
            loadVaccineReceivedStatusList();
            $location.path('/received-status');
        };

        var onError = function (data) {
            $scope.showError = true;
            $scope.error = data.data.error;
        };

        VaccineReceivedStatusSave.post($scope.receivedStatus, onSuccess, onError);
    };

    $scope.cancelReceivedStatusForm = function () {
        $location.path('/received-status');
    };


    function loadVaccineReceivedStatusList() {
        VaccineReceivedStatusList.get({}, function (data) {
            $scope.vaccineReceivedStatus = data.receivedStatus;
        });
    }

    $scope.showRemoveVaccineReceivedStatusDialog = function (receivedStatus) {

        $scope.selectedVaccineReceivedStatus = receivedStatus;

        var options = {
            id: "removeVaccineReceivedStatusConfirmDialog",
            header: "Confirmation",
            body: "Please confirm that you want to delete received status <strong>" + receivedStatus.name + "</strong>"
        };
        OpenLmisDialog.newDialog(options, $scope.removeVaccineReceivedStatusConfirm, $dialog, messageService);
    };

    $scope.removeVaccineReceivedStatusConfirm = function (result) {

        if (result) {
            DeleteVaccineReceivedStatus.delete({'id': $scope.selectedVaccineReceivedStatus.id}, function () {
                loadVaccineReceivedStatusList();
                $scope.clearSearch();
                $scope.$parent.message = 'Vaccine received status deleted successfully!';
            });
        }}
        ;

        $scope.previousQuery = '';

        $scope.showReceivedStatusSearchResults = function () {
            var query = $scope.query;

            var len = (query === undefined) ? 0 : query.length;

            if (len >= 3) {
                if ($scope.previousQuery.substr(0, 3) === query.substr(0, 3)) {
                    $scope.previousQuery = query;
                    filterReceivedStatusByName(query);
                    return true;
                }

                $scope.previousQuery = query;
                SearchVaccineReceivedStatus.get({param: $scope.query.substr(0, 3)}, function (data) {
                    $scope.receivedStatusList = data.receivedStatus;
                    filterReceivedStatusByName(query);
                });
                return true;
            } else {
                return false;
            }
        };

        $scope.clearSearch = function () {
            $scope.query = "";
            $scope.resultCount = 0;
            angular.element("#search").focus();
        };

        $scope.showReceivedStatusSearchResults();

        var filterReceivedStatusByName = function (query) {
            $scope.filteredReceivedStatus = [];
            query = query || "";

            angular.forEach($scope.receivedStatusList, function (status) {

                if (status.name.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0) {
                    $scope.filteredReceivedStatus.push(status);
                }
            });
            $scope.resultCount = $scope.filteredReceivedStatus.length;
        };

}