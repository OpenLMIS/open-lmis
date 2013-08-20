function ProductEditController($scope, $route, $location, $dialog, messageService, ProductDetail, PriceHistory , ProductGroups , CreateProduct, UpdateProduct,ProductCategories, ReportPrograms, ProductList, RemoveProduct, RestoreProduct, DosageUnits, ProductForms) {

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

    // clear the parent confirmation message if there was any
    $scope.$parent.message = '';

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
          });

          PriceHistory.get({productId:productId}, function(data){
             $scope.priceHistory    = data.priceHistory;
          });


      };


    $scope.updateProduct = function () {

        product = $scope.editProduct;

        $scope.error = "";
        if (editProductForm.$invalid) {
            $scope.showErrorForEdit = true;
            return;
        }
        $scope.showErrorForEdit = true;

        UpdateProduct.update($scope.editProduct, function (data) {
        var returnedProduct = data.product;
            $location.path('');
            $scope.$parent.message = "The product record was successfully updated.";
        }, function (data) {
            alert(JSON.stringify(data));
            $scope.creationError = data.message;
        });
    };


    $scope.cancelProductEdit = function (productUnderEdit) {
        $location.path('');
    };


    // now that the functions are defined.
    // start the editing
    $scope.startProductEdit($route.current.params.id);

};
