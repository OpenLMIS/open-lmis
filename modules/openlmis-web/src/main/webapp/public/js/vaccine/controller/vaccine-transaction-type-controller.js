/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */


function VaccineTransactionTypeController($scope, $dialog, messageService, $routeParams, $location, VaccineTransactionTypeList, GetVaccineTransactionType, SearchVaccineTransactionType, VaccineTransactionTypeSave, DeleteVaccineTransactionType) {

    $scope.vaccineTransactionTypes = {};


    if (isUndefined($routeParams.id) || $routeParams.id === 0) {

        loadVaccineTransactionTypeList();

    } else {

        $scope.$parent.message = '';

        GetVaccineTransactionType.get({
            id: $routeParams.id
        }, function (data) {
            $scope.transactionType = data.transactionTypes;
        });
    }

    $scope.saveVaccineTransactionType = function(){
        if ($scope.vaccineTransactionTypeForm.$error.pattern || $scope.vaccineTransactionTypeForm.$error.min || $scope.vaccineTransactionTypeForm.$error.required) {
            $scope.showError = "true";
            $scope.error = 'form.error';
            $scope.message = "";
            return;
        }

        var onSuccess = function(data){
            $scope.$parent.message = 'Your changes have been saved!';
            loadVaccineTransactionTypeList();
            $location.path('/transaction-type');
        };

        var onError = function(data){
            $scope.showError = true;
            $scope.error = data.data.error;
        };

        VaccineTransactionTypeSave.post($scope.transactionType,onSuccess, onError);
    };

    $scope.cancelTransactionTypeForm = function(){
        $location.path('/transaction-type');
    };


    function loadVaccineTransactionTypeList(){
        VaccineTransactionTypeList.get({}, function(data){
            $scope.vaccineTransactionTypes = data.transactionTypes;
        });
    }

    $scope.showRemoveVaccineTransactionTypeDialog = function (transactionType) {

        $scope.selectedVaccineTransactionType = transactionType;

        var options = {
            id: "removeVaccineTransactionTypeConfirmDialog",
            header: "Confirmation",
            body: "Please confirm that you want to delete Transaction type <strong>"+transactionType.name+"</strong>"
        };
        OpenLmisDialog.newDialog(options, $scope.removeVaccineTransactionTypeConfirm, $dialog, messageService);
    };

    $scope.removeVaccineTransactionTypeConfirm = function (result) {

        if (result) {
            DeleteVaccineTransactionType.delete({'id': $scope.selectedVaccineTransactionType.id}, function(){
                loadVaccineTransactionTypeList();
                $scope.clearSearch();
                $scope.$parent.message = 'Vaccine transaction type deleted successfully!';
            });
        }
    };

    $scope.previousQuery = '';

    $scope.showUserSearchResults = function () {
        var query = $scope.query;

        var len = (query === undefined) ? 0 : query.length;

        if (len >= 3) {
            if ($scope.previousQuery.substr(0, 3) === query.substr(0, 3)) {
                $scope.previousQuery = query;
                filterUserByName(query);
                return true;
            }

            $scope.previousQuery = query;
            SearchVaccineTransactionType.get({param: $scope.query.substr(0, 3)}, function (data) {
                $scope.transactionTypeList = data.transactionTypes;
                filterUserByName(query);
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

    $scope.showUserSearchResults();

    var filterUserByName = function (query) {
        $scope.filteredTransactionType = [];
        query = query || "";

        angular.forEach($scope.transactionTypeList, function (user) {

            if (user.name.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0) {
                console.log(user);
                $scope.filteredTransactionType.push(user);
            }
        });
        $scope.resultCount = $scope.filteredTransactionType.length;
    };

}