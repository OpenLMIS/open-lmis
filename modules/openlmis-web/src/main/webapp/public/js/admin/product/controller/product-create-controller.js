/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function ProductCreateController($scope, $location,messageService, SettingsByKey, CreateProduct, ProductGroups, ProductCategories, ReportPrograms, DosageUnits, ProductForms) {

  $scope.title = 'Products';
  $scope.programProductsCost = [];

  // clear the parent confirmation message if there was any
  $scope.$parent.message = '';

  SettingsByKey.get({key: 'ALLOW_PRODUCT_CATEGORY_PER_PROGRAM'},function (data){
    $scope.categoryPerProgramProductIsRequired = data.settings.value;
  });

  // Programs list
  ReportPrograms.get(function (data) {
    $scope.product={};

    //populate the defaults
    $scope.product.active = true;
    $scope.product.tracer = true;
    $scope.product.fullSupply = true;
    $scope.product.roundToZero = true;

    $scope.product.programProducts = [];
    $scope.programs = data.programs;

    for(var i = 0; i < data.programs.length; i++){
        var program = data.programs[i];
        $scope.product.programProducts.push({program:program , currentPrice: 0, dosesPerMonth:1, active:false, programName: program.name });
    }

  });

  // create product
  $scope.createProduct = function () {
      $scope.error = "";
      if ($scope.createProductForm.$invalid) {
          $scope.showError = true;
          $scope.errorMessage = "The form you submitted is invalid. Please revise and try again.";
          return;
      }

    var createSuccessCallback = function (data) {
      $scope.$parent.message = 'New product created successfully';
      $location.path('');
      $scope.product = {};
    };

    var errorCallback = function(data){

      $scope.errorMessage = messageService.get(data.data.error);
    };

     $scope.error = "";
     CreateProduct.save( $scope.product, createSuccessCallback , errorCallback );
  };

  $scope.cancelAddNewProduct = function () {
    $location.path('#/list');
  };

  ProductCategories.get(function (data) {
      $scope.productCategories = data.productCategoryList;
  });

  DosageUnits.get(function (data) {
      $scope.dosageUnits = data.dosageUnits;
  });

  ProductForms.get(function (data) {
      $scope.productForms = data.productForms;
  });

  ProductGroups.get(function (data){
     $scope.productGroups = data.productGroups;
  });

}
