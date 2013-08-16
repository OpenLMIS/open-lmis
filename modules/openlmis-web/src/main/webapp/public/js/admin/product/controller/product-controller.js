/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ProductController($scope, $location, $dialog, messageService, ProductDetail , AllProductCost, CreateProduct, UpdateProduct,ProductCategories, ReportPrograms, ProductList, RemoveProduct, RestoreProduct, DosageUnits, ProductForms) {

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
    $scope.title = "Manage Products";

    // all products list
    ProductList.get({}, function (data) {
        $scope.productsList = data.productList;
        $scope.filteredProducts = $scope.productsList;

        $scope.initialProducts = angular.copy(data.productList, $scope.initialProducts);
        $scope.products = $scope.productsList;

        //alert(JSON.stringify($scope.filteredProducts, null, 4));
        for (var productIndex in data.productList) {
            var product = data.productList[productIndex];
            $scope.productsBackupMap[product.id] = $scope.getBackupProduct(product);
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

//  scope is undefined,
    $scope.productLoaded = function () {
        return !($scope.products == undefined || $scope.products == null);
    };

    // Programs list
    ReportPrograms.get(function (data) {
        var tmp = data.programs;
        $scope.programs = data.programs;
    })

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


};
