/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ProductEditController($scope, $route, $location, $dialog, messageService, ProductDetail , AllProductCost, CreateProduct, UpdateProduct,ProductCategories, ReportPrograms, ProductList, RemoveProduct, RestoreProduct, DosageUnits, ProductForms) {

    $scope.productsBackupMap = [];
    $scope.newProduct = {};
    $scope.products = {};
    $scope.editProduct = {};
    $scope.product={};
    $scope.creationError = '';
    $scope.title = 'Products';
    $scope.demoproducts = {};
    $scope.AddEditMode = '';
    $scope.programProductsCost = [];

    $scope.AddEditMode = true;

    $scope.title = 'Edit Product';


    // Programs list
    ReportPrograms.get(function (data) {
        var tmp = data.programs;
        $scope.programs = data.programs;
        //alert(JSON.stringify( $scope.programs, null, 4));
    })


    // delete confirm window
    $scope.showConfirmProductDeleteWindow = function (productUnderDelete) {

        //alert(JSON.stringify( $scope.product, null, 4));
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
            $scope.AddEditMode = false;
            $scope.editProductMode = false;
            $('html, body').animate({ scrollTop: 0 }, 'fast');
            $scope.title = 'Products';

        });

    };

    // restore product window
    $scope.showConfirmProductRestoreWindow = function (productUnderRestore) {
        var dialogOpts = {
            id: "restoreProductDialog",
            header: messageService.get('Restore product'),
            //body: messageService.get('delete.facility.confirm', productUnderRestore.fullName, productUnderRestore.id)
            body: messageService.get('"' + productUnderRestore.fullName + '"' + " will be restored.")
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
            $scope.AddEditMode = false;
            $scope.editProductMode = false;
            $('html, body').animate({ scrollTop: 0 }, 'fast');
            $scope.title = 'Products';


        });

    };

    //  given product
    $scope.getBackupProduct = function (product) {
        return {
            id: product.id,
            active: product.active,
            categoryId: product.categoryId,
            code: product.code,
            dispensingUnit: product.dispensingUnit,
            displayOrder: product.displayOrder,
            dosageUnitId:		    product.dosageUnitId,
            formId: product.formId,
            fullName:		        product.fullName,
            primaryName:		    product.primaryName,
            programName:		    product.programName,
            programId:		        product.programId,
            fullSupply:		        product.fullSupply,
            packSize:		        product.packSize,
            strength:		        product.strength,
            tracer:			        product.tracer,
            type:			        product.type,
            packRoundingThreshold:	product.packRoundingThreshold,
            formCode:		        product.formCode,
            dosageUnitCode:		    product.dosageUnitCode,
            dosesPerDispensingUnit:	product.dosesPerDispensingUnit



            // TODO: add product fields
            //programid: supplyline.programid,
            //supplyingfacilityid: supplyline.supplyingfacilityid,
            //supervisorynodeid: supplyline.supervisorynodeid,
            //description: supplyline.description
        };
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


    $scope.startProductEdit = function (productId) {
        $scope.$parent.editProductMode = true;
          $scope.title='Edit product';
          $scope.AddEditMode = true;

          // now get a fresh copy of the product object from the server
          ProductDetail.get({id:productId}, function(data){
              $scope.editProduct = data.product;
              $scope.productsBackupMap[data.product.id].editFormActive = "product-form-active";
              $scope.edit_id = productUnderEdit.id;
          });

          $('html, body').animate({ scrollTop: 0 }, 'fast');

          // we will have to check if this part is going to be important to do or not.
          // if we avoided it, the better
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
           }, {});


      };


    $scope.updateProduct = function () {

        product = $scope.editProduct;

        $scope.error = "";
        if (editProductForm.$invalid) {
            alert('I was here and invalid');ß
            $scope.showErrorForEdit = true;
            return;
        }


        $scope.showErrorForEdit = true;
        UpdateProduct.update(product, function (data) {
            var returnedProduct = data.product;

            $scope.productsBackupMap[returnedProduct.id] = $scope.getBackupProduct(returnedProduct);
            $location.path('');
            $scope.message = "The product record was successfully updated.";
        }, function (data) {
            $scope.message = "";

        });
    };


    //  backup record
    $scope.completeEditProduct = function (product) {
        //$scope.productsBackupMap[product.id] = $scope.getBackupProduct(product);
        $scope.$parent.editProductMode = false;
        $scope.showErrorForCreate = false;
        $scope.AddEditMode = false;
        $('html, body').animate({ scrollTop: 0 }, 'fast');
        $scope.title = 'Products';
    };

    $scope.cancelProductEdit = function (productUnderEdit) {
        var backupProductRow = $scope.productsBackupMap[productUnderEdit.id];

        productUnderEdit.id	                =backupProductRow.id;
        productUnderEdit.active	            =backupProductRow.active;
        productUnderEdit.categoryId	        =backupProductRow.categoryId;
        productUnderEdit.code	            =backupProductRow.code;
        productUnderEdit.dispensingUnit	    =backupProductRow.dispensingUnit;
        productUnderEdit.displayOrder	    =backupProductRow.displayOrder;
        productUnderEdit.dosageUnitId	    =backupProductRow.dosageUnitId;
        productUnderEdit.formId	            =backupProductRow.formId;
        productUnderEdit.fullName	        =backupProductRow.fullName;
        productUnderEdit.primaryName	    =backupProductRow.primaryName;
        productUnderEdit.programName	    =backupProductRow.programName;
        productUnderEdit.programId	        =backupProductRow.programId;
        productUnderEdit.fullSupply	        =backupProductRow.fullSupply;
        productUnderEdit.packSize	        =backupProductRow.packSize;
        productUnderEdit.strength	        =backupProductRow.strength;
        productUnderEdit.tracer	            =backupProductRow.tracer;
        productUnderEdit.type	            =backupProductRow.type;
        productUnderEdit.packRoundingThreshold	=backupProductRow.packRoundingThreshold;
        productUnderEdit.formCode	            =backupProductRow.formCode;
        productUnderEdit.dosageUnitCode	        =backupProductRow.dosageUnitCode;
        productUnderEdit.dosesPerDispensingUnit	=backupProductRow.dosesPerDispensingUnit;

        $scope.productsBackupMap[productUnderEdit.id].error = '';
        $scope.productsBackupMap[productUnderEdit.id].editFormActive = '';

        $scope.$parent.editProductMode = false;
        $scope.AddEditMode = false;

        $('html, body').animate({ scrollTop: 0 }, 'fast');
    };


    // now that the functions are defined.
    // start the editing
    $scope.startProductEdit($route.current.params.id);

};
