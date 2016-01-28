/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

function ProgramEquipmentProductController($scope, $dialog, messageService, navigateBackService, EquipmentTypes, ProgramCompleteList, PossibleProductsForProgram, GetProgramEquipmentByProgramId, SaveProgramEquipment, GetProgramEquipmentProductByProgramEquipment, SaveProgramEquipmentProduct, RemoveProgramEquipmentProduct, RemoveProgramEquipment) {
  $scope.$on('$viewContentLoaded', function () {
    $scope.getAllEquipments();
    $scope.getAllPrograms();
  });

  $scope.isDataChanged = false;
  $scope.programEquipments = {};
  $scope.currentProgramEquipment = {};
  $scope.currentProgramEquipmentProduct = {};

  $scope.getAllEquipments = function () {
    EquipmentTypes.get(function (data) {
       $scope.allEquipments = data.equipment_types;
       $scope.equipmentsLoaded = true;
      });
  };

  $scope.getAllProductsForAProgram = function () {
    PossibleProductsForProgram.get({
      program: $scope.currentProgramEquipment.program.id,
      equipmentType: $scope.currentProgramEquipment.equipmentId
    }, function (data) {
      $scope.allProducts = data.products;
      $scope.productsLoaded = true;
    });
  };

  $scope.getAllPrograms = function () {
    ProgramCompleteList.get(function (data) {
      $scope.programs = data.programs;
    });
  };


  $scope.listEquipmentsForProgram = function () {

    if ($scope.currentProgramEquipment.program) {
      GetProgramEquipmentByProgramId.get({programId: $scope.currentProgramEquipment.program.id}, function (data) {
        $scope.programEquipments = data.programEquipments;
      });
      $scope.getAllProductsForAProgram();
      $scope.programEquipmentProducts = null;
      $scope.currentProgramEquipment.id = null;
      $scope.currentProgramEquipment.equipmentId = null;
      $scope.currentProgramEquipmentProduct.product=null;
    }
    else {
      $scope.programEquipments = null;
      $scope.programEquipmentProducts = null;
      $scope.currentProgramEquipment = {};
      $scope.currentProgramEquipmentProduct = {};
    }
  };

  $scope.addNewEquipment = function () {
    $scope.equipmentDialogModal = true;
    $scope.currentProgramEquipment.id = null;
  };

  $scope.deleteEquipment = function () {
    $scope.equipmentDialogModal = true;
    $scope.currentProgramEquipment.id = null;
  };

  $scope.addNewProduct = function () {
    $scope.productDialogModal = true;
  };

  $scope.closeModal = function () {
    $scope.equipmentDialogModal = false;
    $scope.productDialogModal = false;
  };

  $scope.setSelectedProgramEquipment = function (programEquipment) {
    if (programEquipment) {
      $scope.currentProgramEquipment.id = programEquipment.id;
      $scope.currentProgramEquipment.equipmentId = programEquipment.equipmentType.id;
      $scope.currentProgramEquipmentProduct.programEquipmentType = programEquipment;
    }
    else {
      $scope.currentProgramEquipment = null;
      $scope.currentProgramEquipmentProduct = null;
    }

    $scope.refreshProgramEquipmentProductList();
  };

  $scope.refreshProgramEquipmentProductList = function () {
    if ($scope.currentProgramEquipment) {
      GetProgramEquipmentProductByProgramEquipment.get({programEquipmentId: $scope.currentProgramEquipment.id}, function (data) {
        $scope.programEquipmentProducts = data.programEquipmentProducts;
      });
    }
    else {
      $scope.programEquipmentProducts = null;
    }
  };

  $scope.getProgramEquipmentColor = function (programEquipment) {
    if (!$scope.currentProgramEquipment) {
      return 'none';
    }

    if ($scope.currentProgramEquipment.id == programEquipment.id) {
      return "background-color : teal; color: white";
    }
    else {
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
        SaveProgramEquipment.save(programEquipment, successHandler, errorHandler);
      }
    });
  };

  $scope.saveEquipment = function () {
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

    SaveProgramEquipment.save($scope.currentProgramEquipment, successHandler, errorHandler);
  };

  $scope.saveProgramEquipmentProduct = function () {
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

    SaveProgramEquipmentProduct.save($scope.currentProgramEquipmentProduct, successHandler, errorHandler);
  };


  $scope.setDataChanged = function (programEquipment) {
    programEquipment.isDataChanged = true;
    $scope.isDataChanged = true;
  };

  $scope.showRemoveProgramEquipmentConfirmDialog = function (index) {
    var programEquipment = $scope.programEquipments[index];

    $scope.selectedProgramEquipment = programEquipment;
    var options = {
      id: "removeProgramEquipmentConfirmDialog",
      header: "Confirmation",
      body: "Please confirm that you want to remove the equipment: " + programEquipment.equipmentType.name
    };

    OpenLmisDialog.newDialog(options, $scope.removeProgramEquipmentConfirm, $dialog, messageService);
  };

  $scope.removeProgramEquipmentConfirm = function (result) {

    var successCallBack = function (response) {
      $scope.message = response.success;
      $scope.showMessage = true;
      $scope.listEquipmentsForProgram();
    };

    var errorCallBack = function (response) {
      $scope.equipmentError = true;
      $scope.equipmentErrorMessage = response.data.error;
    };

    if (result) {
      RemoveProgramEquipment.delete({id: $scope.selectedProgramEquipment.id}, successCallBack, errorCallBack);
    }
  };

  $scope.showRemoveProgramEquipmentProductConfirmDialog = function (index) {
    var programEquipmentProduct = $scope.programEquipmentProducts[index];
    $scope.index = index;
    $scope.selectedProgramEquipmentProduct = programEquipmentProduct;
    var options = {
      id: "removeProgramEquipmentProductConfirmDialog",
      header: "Confirmation",
      body: "Please confirm that you want to remove the product: " + $scope.selectedProgramEquipmentProduct.product.fullName + '('+ $scope.selectedProgramEquipmentProduct.product.primaryName + ')'
    };
    OpenLmisDialog.newDialog(options, $scope.removeProgramEquipmentProductConfirm, $dialog, messageService);
  };

  $scope.removeProgramEquipmentProductConfirm = function (result) {
    if (result) {
      $scope.programEquipmentProducts.splice($scope.index, 1);
      $scope.removeProgramEquipmentProduct();
    }
    $scope.selectedProgramEquipmentProduct = undefined;
  };

  $scope.removeProgramEquipmentProduct = function () {

    var successCallBack = function (response) {
      $scope.message = response.success;
      $scope.showMessage = true;
    };

    var errorCallBack = function (response) {
      $scope.productError = true;
      $scope.productErrorMessage = response.data.error;
    };
    RemoveProgramEquipmentProduct.get({id: $scope.selectedProgramEquipmentProduct.id}, successCallBack, errorCallBack);

  };
}