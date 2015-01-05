/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */


function VaccineManufacturerController($scope, $dialog, messageService, $routeParams, $location, GetVaccineManufacturerProductMapping, VaccineManufacturerList,Products,  VaccineManufacturerUpdate, GetVaccineManufacturer, DeleteVaccineManufacturer, DeleteManufacturerProductMapping) {

    $scope.vaccineManufacturers = [];


    // manufacturer list
   if (isUndefined($routeParams.id)){

        VaccineManufacturerList.get({}, function(data){
            $scope.vaccineManufacturers = data.vaccineManufacturers;
        });
    }
    // Manufacturer edit
   else {

       if ($routeParams.id > 0) {

           $scope.$parent.message = '';

           GetVaccineManufacturerProductMapping.get({
               id: $routeParams.id
           }, function (data) {
               $scope.manufacturer = data.vaccineManufacturer;
               $scope.manufacturer.registrationDate = data.vaccineManufacturer.registrationDateString;
               $scope.productMappings = data.productMapping;
           });
       }

       else{
           $scope.$parent.message = '';
       }
   }

    $scope.saveVaccineManufacturer = function(){
        if ($scope.vaccineManufacturerForm.$error.pattern || $scope.vaccineManufacturerForm.$error.required) {
            $scope.showError = "true";
            $scope.error = 'form.error';
            $scope.message = "";
            return;
        }

        var onSuccess = function(data){
            $scope.$parent.message = 'Your changes have been saved!';
            loadVaccineManufacturerList();
            $location.path('/manufacturer');
        };

        var onError = function(data){
            $scope.showError = true;
            $scope.error = data.data.error;
        };

        VaccineManufacturerUpdate.post($scope.manufacturer,onSuccess, onError);
    };

    $scope.cancelManufacturerForm = function(){
        $location.path('/manufacturer');
    };


    function loadVaccineManufacturerList(){
        VaccineManufacturerList.get({}, function(data){
            $scope.vaccineManufacturers = data.vaccineManufacturers;
        });
    }

    $scope.showRemoveVaccineManufacturerDialog = function (vaccineManufacturer) {
        $scope.selectedVaccineManufacturer = vaccineManufacturer;

        var options = {
            id: "removeVaccineManufacturerConfirmDialog",
            header: "Confirmation",
            body: "Please confirm that you want to delete vaccine Manufacturer <strong>"+ $scope.selectedVaccineManufacturer.name+"</strong>"
        };
        OpenLmisDialog.newDialog(options, $scope.removeVaccineManufacturerConfirm, $dialog, messageService);
    };

    $scope.removeVaccineManufacturerConfirm = function (result) {

        if (result) {
            DeleteVaccineManufacturer.delete({'id': $scope.selectedVaccineManufacturer.id},
             function(){
                loadVaccineManufacturerList();
                $scope.$parent.message = 'Vaccine Manufacturer deleted successfully!';
            }, function(data){
                $scope.$parent.message = "";
                $scope.error = data.data.error;
            });
        }
    };


    $scope.showRemoveManufacturerProductDialog = function (productMapping) {
        $scope.selectedProductMapping = productMapping;

        var options = {
            id: "removeManufacturerProductMappingConfirmDialog",
            header: "Confirmation",
            body: "Please confirm that you want to delete <strong>"+$scope.selectedProductMapping.productName+"</strong> from the mapping list"
        };
        OpenLmisDialog.newDialog(options, $scope.removeManufacturerProductMappingConfirm, $dialog, messageService);
    };

    $scope.removeManufacturerProductMappingConfirm = function (result) {

        if (result) {
            DeleteManufacturerProductMapping.delete({'id': $scope.selectedProductMapping.id}, function(){

                GetVaccineManufacturerProductMapping.get({
                    id: $routeParams.id
                }, function (data) {
                    $scope.manufacturer = data.vaccineManufacturer;
                    $scope.productMappings = data.productMapping;
                });

                $scope.$parent.message = 'Manufacturer product mapping deleted successfully!';
            });
        }
    };

}