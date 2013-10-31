/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function ProductEditController($scope, $route, $location, $dialog, messageService, ProductDetail, PriceHistory , ProductGroups , CreateProduct, UpdateProduct,ProductCategories, ReportPrograms, ProductList, RemoveProduct, RestoreProduct, DosageUnits, ProductForms) {

    $scope.title = 'Products';
    $scope.AddEditMode = '';
    $scope.programProductsCost = [];
    $scope.AddEditMode = true;
    $scope.title = 'Edit Product';

    // clear the parent confirmation message if there was any
    $scope.$parent.message = '';

    // Programs list
    ReportPrograms.get(function (data) {
        var tmp = data.programs;
        $scope.programs = data.programs;
    });


    $scope.disableAllFields = function() {
        $('.form-group').find(':input').attr('disabled','disabled');
    };

    // delete confirm window
    $scope.showConfirmProductDeleteWindow = function (productUnderDelete) {

        var dialogOpts = {
            id: "deleteProductDialog",
            header: messageService.get('Delete product'),
            body: "The product " + productUnderDelete.fullName + " will be marked as inactive in the system. Are you sure?"
        };
        $scope.productUnderDelete = productUnderDelete;
        OpenLmisDialog.newDialog(dialogOpts, $scope.deleteProductCallBack, $dialog, messageService);
    };

    // delete product
    $scope.deleteProductCallBack = function (result) {
        if (!result) {
            $scope.productsBackupMap[$scope.productUnderDelete.id].deleted = false;
            return;
        }

        //alert(JSON.stringify( RemoveProduct, null, 4));
        RemoveProduct.get({id: $scope.editProduct.id }, $scope.product, function (data) {

            $scope.message = data.success;
            setTimeout(function () {
                $scope.$apply(function () {
                    // refresh list
                    $scope.productsList = data.productsList;
                    $scope.filteredProducts = data.productList;
                    $scope.message = "";
                });
            }, 2000);

            $location.path('');
            $scope.$parent.message = "The product was marked as deleted.";

        });

    };

    // restore product window
    $scope.showConfirmProductRestoreWindow = function (productUnderRestore) {
        var dialogOpts = {
            id: "restoreProductDialog",
            header: messageService.get('Restore product'),
            body: messageService.get('"' + productUnderRestore.fullName + '"' + " will be restored. Are you sure?")
        };
        $scope.productUnderRestore = productUnderRestore;
        OpenLmisDialog.newDialog(dialogOpts, $scope.restoreProductCallBack, $dialog, messageService);
    };

    //restore product
    $scope.restoreProductCallBack = function (result) {
        if (!result) {
            $scope.productsBackupMap[$scope.productUnderRestore.id].restore = false;
            return;
        }

        //alert(JSON.stringify( RemoveProduct, null, 4));
        RestoreProduct.get({id: $scope.productUnderRestore.id }, $scope.product, function (data) {

            $scope.message = data.success;
            setTimeout(function () {
                $scope.$apply(function () {
                    // refresh list
                    $scope.productsList = data.productsList;
                    $scope.filteredProducts = data.productList;
                    $scope.message = "";
                });
            }, 2000);

            $location.path('');
            $scope.$parent.message = "The product was restored.";
        });

    };

    // cancel record
    $scope.cancelAddNewProduct = function (product) {
        $scope.$parent.newProductMode = false;
        $scope.AddEditMode = false;
        $scope.showErrorForCreate = false;
    };

    //  scope is undefined,
    $scope.productLoaded = function () {
        return !($scope.products === undefined || $scope.products === null);
    };


    // drop down lists
    ProductCategories.get(function (data) {
        $scope.productCategories = data.productCategoryList;
    });

    // load the product group dropdown list
    ProductGroups.get(function (data){
        $scope.productGroups = data.productGroups;
    });

    // drop down lists
    DosageUnits.get(function (data) {
        $scope.dosageUnits = data.dosageUnits;
    });


    ProductForms.get(function (data) {
        $scope.productForms = data.productForms;
    });


    $scope.updateProduct = function () {

        product = $scope.editProduct;

        $scope.error = "";
        if ($scope.editProductForm.$invalid) {
            $scope.showErrorForCreate = $scope.showErrorForEdit = true ;
            return;
        }

        UpdateProduct.update($scope.editProduct, function (data) {
        var returnedProduct = data.product;
            $location.path('');
            $scope.$parent.message = "The product record was successfully updated.";
        }, function (data) {
            $scope.error = true;
            $scope.errorMessage = messageService.get(data.data.error);
            alert( messageService.get(data.data.error ));
        });
    };


    $scope.cancelProductEdit = function (productUnderEdit) {
        $location.path('');
    };

    $scope.startProductEdit = function (productId) {
        $scope.$parent.editProductMode = true;
        $scope.title='Edit product';
        $scope.AddEditMode = true;

        // now get a fresh copy of the product object from the server
        ProductDetail.get({id:productId}, function(data){
            $scope.editProduct = data.product;
            if($scope.editProduct.active === false){
                $scope.disableAllFields();
            }
        });

        PriceHistory.get({productId:productId}, function(data){
            $scope.priceHistory    = data.priceHistory;
        });
    };
    // now that the functions are defined.
    // start the editing
    $scope.startProductEdit($route.current.params.id);

}
