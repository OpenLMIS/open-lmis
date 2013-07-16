/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ProductController($scope, $location, $dialog, messageService, CreateProduct, ProductCategories, ReportPrograms, ProductList, RemoveProduct, RestoreProduct, DosageUnits) {

    $scope.productsBackupMap = [];
    $scope.newProduct = {};
    $scope.products = {};
    $scope.editProduct = {};
    $scope.creationError = '';
    $scope.title='Products';

    // Programs list
    ReportPrograms.get(function (data) {
        var tmp = data.programs;
        $scope.programs = data.programs;
        //alert(JSON.stringify( $scope.programs, null, 4));
    })

     // all products list
    ProductList.get({}, function (data) {
        $scope.productsList = data.productList;
        $scope.filteredProducts = $scope.productsList;
        //alert(JSON.stringify($scope.productsList, null, 4));
    }, function (data) {
        $location.path($scope.$parent.sourceUrl);
    });

    // show search results
    $scope.showProductsSearchResults = function (id) {
        var query = document.getElementById(id).value;
        query = parseInt(query) + 1;
        query = query.toString();
        $scope.query = query;

        var len = (query == undefined) ? 0 : query.length;
        if (len >= 0) {
            $scope.previousQuery = query;
            //alert(query);
            filterProductsByProgram(query);
            return true;
        } else {
            return false;
        }
    };

    // apply filter
    var filterProductsByProgram = function (query) {
        $scope.filteredProducts = [];
        query = query || "";
        if (query == 'NaN') {
            $scope.filteredProducts = $scope.productsList;
            return;
        }
        angular.forEach($scope.productsList, function (product) {
            if (product.programId == query) {
                $scope.filteredProducts.push(product);
            }

        });

        $scope.resultCount = $scope.filteredProducts.length;
    };

    // delete confirm window
    $scope.showConfirmProductDeleteWindow = function (productUnderDelete) {
        //alert(JSON.stringify( productUnderDelete, null, 4));
        var dialogOpts = {
            id: "deleteProductDialog",
            header: messageService.get('Delete product'),
            body: messageService.get('delete.facility.confirm', productUnderDelete.fullName, productUnderDelete.id)
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
        RemoveProduct.get({id: $scope.productUnderDelete.id }, $scope.product, function (data) {

            $scope.message = data.success;
            setTimeout(function () {
                $scope.$apply(function () {
                    // refresh list
                    $scope.productsList = data.productsList;
                    $scope.filteredProducts = data.productList;
                    $scope.message = "";
                });
            }, 4000);
            $scope.error = "";
            $scope.newProduct = {};
            $scope.editProduct = {};

        });

    };

    // restore product window
    $scope.showConfirmProductRestoreWindow = function (productUnderRestore) {
        var dialogOpts = {
            id: "restoreProductDialog",
            header: messageService.get('Restore product'),
            //body: messageService.get('delete.facility.confirm', productUnderRestore.fullName, productUnderRestore.id)
            body: messageService.get('"' + productUnderRestore.fullName + '"'+ " will be restored.")
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
            }, 4000);
            $scope.error = "";
            $scope.newProduct = {};
            $scope.editProduct = {};

        });

    };

    //  given product
    $scope.getBackupProduct = function (product) {
        return {
            // TODO: add product fields
            //programid: supplyline.programid,
            //supplyingfacilityid: supplyline.supplyingfacilityid,
            //supervisorynodeid: supplyline.supervisorynodeid,
            //description: supplyline.description
        };
    };


// Add Product

    // create product
    $scope.createProduct = function () {
        $scope.error = "";

        if ($scope.createProductForm.$invalid) {

            $scope.showErrorForCreate = true;
            return;
        }
        $scope.showErrorForCreate = false;

        CreateProduct.post( $scope.product, function (data) {
            alert(JSON.stringify( $scope.newProduct, null, 4));
            $scope.products.unshift(data.product);
            $scope.completeAddNewProduct(data.product);
            $scope.message = data.success;
            setTimeout(function() {
                $scope.$apply(function() {
                    $scope.productsList = data.productList;
                    $scope.message = "";
                });
            }, 4000);
            $scope.newProduct = {};
        }, function (data) {
            $scope.message = "";
            $scope.creationError = data.data.error;
        });
    };

//  switch to new mode
    $scope.startAddNewProduct = function () {
        $scope.title='Add product';
        $scope.$parent.newProductMode = true;
        $scope.$parent.formActive = "product-form-active";

    };

    //  backup record
    $scope.completeAddNewSupplyline = function (product) {
        $scope.productsBackupMap[product.id] = $scope.getBackupProduct(product);
        $scope.$parent.newProductMode = false;
        $scope.showErrorForCreate = false;
    };

// cancel record
    $scope.cancelAddNewProduct = function (product) {
        $scope.$parent.newProductMode = false;
        $scope.showErrorForCreate = false;
    };

//  scope is undefined,
    $scope.productLoaded = function () {
        return !($scope.products == undefined || $scope.products == null);
    };


    // drop down lists
    ProductCategories.get(function (data) {
        $scope.productCategories = data.productCategoryList;
        //alert(JSON.stringify( $scope.productCategories, null, 4));
    });


  // drop down lists
    DosageUnits.get(function (data) {
        $scope.dosageUnits = data.dosageUnits;
    });



    $scope.productForms         = [

        {'name':'Tablet','value':'1'},
        {'name':'Bottle','value':'2'},
        {'name':'Vial','value':'3'},
        {'name':'Capsule','value':'4'},
        {'name':'Select product form','value':'0'}
    ];


    $scope.YesNo = function (tf) {
        var retval = '';
        if (tf == true) {
            retval = 'Yes';
        } else
        {
            retval = 'No';
        }
        return retval;
    };

    /*
     // all supply lines   for list
     ProductList.get({}, function (data) {
     $scope.productslist = data.productList;

     }, function (data) {
     $location.path($scope.$parent.sourceUrl);
     });
     */


};
