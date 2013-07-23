/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ProductController($scope, $location, $dialog, messageService, AllProductCost, CreateProduct, ProductCategories, ReportPrograms, ProductList, RemoveProduct, RestoreProduct, DosageUnits, ProductForms) {

    $scope.productsBackupMap = [];
    $scope.newProduct = {};
    $scope.products = {};
    $scope.editProduct = {};
    $scope.creationError = '';
    $scope.title='Products';
    $scope.demoproducts = {};
    $scope.AddEditMode = '';
    $scope.programProductsCost = [];


    if ($scope.$parent.newProductMode || $scope.$parent.editProductMode) {
         $scope.AddEditMode = true;
          $scope.title = ($scope.$parent.newProductMode) ? $scope.title='Add Product' : $scope.title='Edit Product';

     } else
    {
        $scope.AddEditMode = false;
        $scope.title='Products';
    }

    // Programs list
    ReportPrograms.get(function (data) {
        var tmp = data.programs;
        $scope.programs = data.programs;
        //alert(JSON.stringify( $scope.programs, null, 4));
    })

 /*
    // Programs list
    ProgramPricesList.get(function (data) {
        $scope.prices = data.programPrices;
        alert(JSON.stringify( $scope.prices, null, 4));
    })

*/

     // all products list
    ProductList.get({}, function (data) {
        $scope.productsList = data.productList;
        $scope.filteredProducts = $scope.productsList;

        $scope.initialProducts = angular.copy(data.productList, $scope.initialProducts);
        $scope.products = $scope.productsList;

        //alert(JSON.stringify($scope.products, null, 4));
        for(var productIndex in data.productList){
            var product = data.productList[productIndex];
            $scope.productsBackupMap[product.id] =  $scope.getBackupProduct(product);
        }

    //alert(JSON.stringify($scope.products, null, 4));
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
            }, 2000);
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
            }, 2000);
            $scope.error = "";
            $scope.newProduct = {};
            $scope.editProduct = {};

        });

    };

    //  given product
    $scope.getBackupProduct = function (product) {
        return {
            active: 	    product.active,
            code:		    product.code,
            dispensingUnit:	product.dispensingUnit,
            fullName:	    product.fullName,
            fullSupply:	    product.fullSupply,
            id:		        product.id,
            programId:	    product.programId,
            programName:	product.programName,
            strength:	    product.strength,
            type:		    product.type,
            packSize:		product.packSize

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

        CreateProduct.save( $scope.product, function (data) {
            //alert(JSON.stringify( $scope.newProduct, null, 4));
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
        if ($scope.AddEditMode) return false;
        $scope.title='Add product';
        $scope.AddEditMode = true;
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
        $scope.AddEditMode = false;
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


    ProductForms.get(function (data) {
        $scope.productForms = data.productForms;
        //alert(JSON.stringify( $scope.productCategories, null, 4));

    });

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


      $scope.startProductEdit = function (productUnderEdit) {
          $scope.$parent.editProductMode = true;
          $scope.title='Edit product';
          $scope.AddEditMode = true;
          $scope.editProduct = productUnderEdit;
          $scope.productsBackupMap[productUnderEdit.id].editFormActive = "product-form-active";
          $('html, body').animate({ scrollTop: 0 }, 'fast');
          $scope.edit_id = productUnderEdit.id;

          //alert(JSON.stringify($scope.editProduct, null, 4));

          AllProductCost.get({}, function (data) {
              $scope.productCost = data.allProductCost;
              $scope.selectedProductCost = _.where($scope.productCost, {productid: $scope.edit_id});

              //alert(JSON.stringify($scope.products, null, 4));
              var tmp = 0;
              for(var programIndex in $scope.selectedProductCost){
                  var program = $scope.selectedProductCost[programIndex];
                  if (program.progamid !== tmp) {
                      $scope.programProductsCost[program.programid] =  program;
                  }
                  tmp = program.programid;
              }
              //$scope.programProductsCost = $scope.programProductsCost.filter(function(e){return e});
              //$scope.selectedProductCost = $scope.selectedProductCost[0];
              //alert(JSON.stringify($scope.selectedProductCost, null, 4));
              //alert(JSON.stringify($scope.programProductsCost, null, 4));
          }, {});


      };


    $scope.cancelProductEdit = function (productUnderEdit) {
        var backupProductRow = $scope.productsBackupMap[productUnderEdit.id];

        productUnderEdit.active = 	        backupProductRow.active;
        productUnderEdit.code =		        backupProductRow.code;
        productUnderEdit.dispensingUnit =	backupProductRow.dispensingUnit;
        productUnderEdit.fullName =	        backupProductRow.fullName;
        productUnderEdit.fullSupply =	    backupProductRow.fullSupply;
        productUnderEdit.programId =	    backupProductRow.programId;
        productUnderEdit.programName =	    backupProductRow.programName;
        productUnderEdit.strength =	        backupProductRow.strength;
        productUnderEdit.type =		        backupProductRow.type;
        productUnderEdit.packSize =		    backupProductRow.packSize;

        $scope.productsBackupMap[productUnderEdit.id].error = '';
        $scope.productsBackupMap[productUnderEdit.id].editFormActive = '';

        $scope.$parent.editProductMode = false;
        $scope.AddEditMode = false;

        $('html, body').animate({ scrollTop: 0 }, 'fast');

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
