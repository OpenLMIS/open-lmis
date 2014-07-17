/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function ProgramEquipmentProductController($scope,$dialog,messageService, $location, navigateBackService, Equipments, ProgramCompleteList,Products, GetProgramEquipmentByProgramId, SaveProgramEquipment, GetProgramEquipmentProductByProgramEquipment, SaveProgramEquipmentProduct, RemoveProgramEquipmentProduct) {
    $scope.$on('$viewContentLoaded', function () {
        $scope.$apply($scope.query = navigateBackService.query);
        $scope.getAllEquipments();
        $scope.getAllProducts();
        $scope.getAllPrograms();
    });

    $scope.isDataChanged = false;
    $scope.programEquipments = {};
    $scope.currentProgramEquipment = {};
    $scope.currentProgramEquipmentProduct = {};

    $scope.getAllEquipments = function () {
        Equipments.get(function (data) {
            $scope.allEquipments = data.equipments;
            $scope.equipmentsLoaded = true;
        });
    };

    $scope.getAllProducts = function(){
        Products.get(function(data){
            $scope.allProducts = data.productList;
            $scope.productsLoaded = true;
        });
    };

    $scope.getAllPrograms = function(){
        ProgramCompleteList.get(function(data){
            $scope.programs = data.programs;
        });
    };


    $scope.listEquipmentsForProgram = function () {
        GetProgramEquipmentByProgramId.get({programId:$scope.currentProgramEquipment.program.id},function(data){
            $scope.programEquipments = data.programEquipments;
        })
    };

    $scope.addNewEquipment = function() {
        $scope.equipmentDialogModal = true;
        $scope.currentProgramEquipment.id = null;
    };

    $scope.addNewProduct = function() {
        $scope.productDialogModal = true;
    };

    $scope.closeModal = function() {
        $scope.equipmentDialogModal = false;
        $scope.productDialogModal = false;
    };

    $scope.setSelectedProgramEquipment = function (programEquipment){
        $scope.currentProgramEquipment.id = programEquipment.id;
        $scope.currentProgramEquipment.equipmentId = programEquipment.equipmentId;
        $scope.currentProgramEquipmentProduct.programEquipment = programEquipment;
        $scope.refreshProgramEquipmentProductList();
    };

    $scope.refreshProgramEquipmentProductList = function (){
        GetProgramEquipmentProductByProgramEquipment.get({programEquipmentId:$scope.currentProgramEquipment.id},function(data){
            $scope.programEquipmentProducts = data.programEquipmentProducts;
        });
    };

    $scope.getProgramEquipmentColor = function(programEquipment){
        if(!$scope.currentProgramEquipment){
            return 'none';
        }

        if($scope.currentProgramEquipment.id == programEquipment.id){
            return "background-color : teal; color: white";
        }
        else{
            return 'none';
        }
    };

    $scope.saveProgramEquipmentChanges = function () {
        var successHandler = function (response) {
            $scope.programEquipment = response.programEquipment;
            $scope.equipmentError = false;
            $scope.equipmentErrorMessage = '';
            $scope.message = response.success;
            $scope.showMessage = true;
        };

        var errorHandler = function (response) {
            $scope.equipmentError = true;
            $scope.equipmentErrorMessage = response.data.error;
        };

        angular.forEach($scope.programEquipments, function (programEquipment) {
            if (programEquipment.isDataChanged) {
                SaveProgramEquipment.save(programEquipment,successHandler,errorHandler);
            }
        });
    };

    $scope.saveEquipment = function(){
        var successHandler = function (response) {
            $scope.programEquipment = response.programEquipment;
            $scope.equipmentError = false;
            $scope.equipmentErrorMessage = '';
            $scope.message = response.success;
            $scope.showMessage = true;
            $scope.closeModal();
            $scope.listEquipmentsForProgram();
        };

        var errorHandler = function (response) {
            $scope.equipmentError = true;
            $scope.equipmentErrorMessage = response.data.error;
        };

        SaveProgramEquipment.save($scope.currentProgramEquipment,successHandler, errorHandler);
    };

    $scope.saveProgramEquipmentProduct = function(){
        var successHandler = function (response) {
            $scope.programEquipmentProduct = response.programEquipmentProduct;
            $scope.productError = false;
            $scope.productErrorMessage = '';
            $scope.message = response.success;
            $scope.showMessage = true;
            $scope.closeModal();
            $scope.refreshProgramEquipmentProductList();
        };

        var errorHandler = function (response) {
            $scope.productError = true;
            $scope.productErrorMessage = response.data.error;
        };

        SaveProgramEquipmentProduct.save($scope.currentProgramEquipmentProduct,successHandler, errorHandler);
    };


    $scope.setDataChanged = function(programEquipment){
        programEquipment.isDataChanged = true;
        $scope.isDataChanged = true;
    };

    $scope.showRemoveProgramEquipmentProductConfirmDialog = function (index) {
        var programEquipmentProduct = $scope.programEquipmentProducts[index];
        $scope.index = index;
        $scope.selectedProgramEquipmentProduct = programEquipmentProduct;
        var options = {
            id: "removeProgramEquipmentProductConfirmDialog",
            header: "Confirmation",
            body: "Please confirm that you want to remove the product: " + $scope.selectedProgramEquipmentProduct.product.fullName.concat('(').concat($scope.selectedProgramEquipmentProduct.product.primaryName).concat(')')
        };
        OpenLmisDialog.newDialog(options, $scope.removeProgramEquipmentProductConfirm, $dialog, messageService);
    };

    $scope.removeProgramEquipmentProductConfirm = function (result) {
        if (result) {
            $scope.programEquipmentProducts.splice($scope.index,1);
            $scope.removeProgramEquipmentProduct();
        }
        $scope.selectedFacility = undefined;
    };

    $scope.removeProgramEquipmentProduct = function(){
        RemoveProgramEquipmentProduct.get({id: $scope.selectedProgramEquipmentProduct.id});
    };
}