/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */


function VaccineQuantificationController($scope, $dialog, messageService, $routeParams, $location, VaccineQuantificationFormLookUps, ReportProductsByProgram, VaccineQuantificationList, ProgramCompleteList, VaccineQuantificationUpdate, GetVaccineQuantification, DeleteVaccineQuantification) {

    $scope.vaccineQuantifications = {};


    if (isUndefined($routeParams.id) || $routeParams.id === 0) {

        VaccineQuantificationList.get({}, function(data){
            $scope.vaccineQuantifications = data.vaccineQuantifications;
        });

    } else {

        $scope.$parent.message = '';

        ProgramCompleteList.get({}, function(data){

            $scope.programs = data.programs;
        });

        VaccineQuantificationFormLookUps.get({}, function(data){
            $scope.administrationModes = data.administrationMode;
            $scope.dilutions = data.dilution;
            $scope.vaccinationTypes = data.vaccinationType;

            $scope.administrationModes.unshift({
                name : '--Select Administration mode--'
            });
            $scope.dilutions.unshift({
                name : '--Select Dilution --'
            });
            $scope.vaccinationTypes.unshift({
                name : '--Select Vaccination Type --'
            });
        });

        GetVaccineQuantification.get({
            id: $routeParams.id
        }, function (data) {
            $scope.quantification = data.vaccineQuantification;
        });
    }

    $scope.saveVaccineQuantification = function(){
        if ($scope.vaccineQuantificationForm.$error.pattern || $scope.vaccineQuantificationForm.$error.min || $scope.vaccineQuantificationForm.$error.required) {
            $scope.showError = "true";
            $scope.error = 'form.error';
            $scope.message = "";
            return;
        }

        var onSuccess = function(data){
            $scope.$parent.message = 'Your changes have been saved!';
            loadVaccineQuantificationList();
            $location.path('/quantification');
        };

        var onError = function(data){
            $scope.showError = true;
            $scope.error = data.data.error;
        };

        VaccineQuantificationUpdate.post($scope.quantification,onSuccess, onError);
    };

    $scope.cancelQuantificationForm = function(){
        $location.path('/quantification');
    };


    function loadVaccineQuantificationList(){
        VaccineQuantificationList.get({}, function(data){
            $scope.vaccineQuantifications = data.vaccineQuantifications;
        });
    }

    $scope.showRemoveVaccineQuantificationDialog = function (index) {
        var vaccineQuantification = $scope.vaccineQuantifications[index];
        $scope.selectedVaccineQuantification = vaccineQuantification;

        var options = {
            id: "removeVaccineQuantificationConfirmDialog",
            header: "Confirmation",
            body: "Please confirm that you want to delete the selected vaccine Quantification"
        };
        OpenLmisDialog.newDialog(options, $scope.removeVaccineQuantificationConfirm, $dialog, messageService);
    };

    $scope.removeVaccineQuantificationConfirm = function (result) {

        if (result) {
            DeleteVaccineQuantification.delete({'id': $scope.selectedVaccineQuantification.id}, function(){
                loadVaccineQuantificationList();
                $scope.$parent.message = 'Vaccine Quantification deleted successfully!';
            });
        }
    };

    $scope.$watch('quantification.programId', function (value) {

        if(isUndefined(value)){
            $scope.products = {};
        }
        else{
            ReportProductsByProgram.get({
                programId: value
            }, function (data) {
                $scope.products = data.productList;
            });
        }

    });

}