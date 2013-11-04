/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function ProductController($scope, $location, $dialog, messageService, ProductDetail , ReportPrograms, ProductList, RemoveProduct, RestoreProduct, DosageUnits, ProductForms) {

    $scope.title = 'Products';
    $scope.title = "Manage Products";

    // all products list
    ProductList.get({}, function (data) {
        $scope.filteredProducts = $scope.productsList = data.productList;
    }, function (data) {
        $location.path($scope.$parent.sourceUrl);
    });

    // apply filter
    $scope.filterProductsByProgram = function () {
        query = $scope.program;
        $scope.filteredProducts = [];
        query = query || "";
        if (query == 'NaN' || query === '') {
            $scope.filteredProducts = $scope.filterProductsByName($scope.productsList);
            return;
        }
        $scope.query = query;
        $scope.filteredArray = [];
        angular.forEach($scope.productsList, function (product) {
             $scope.product = product;
             angular.forEach($scope.product.programs, function (pp){
                 if(pp.id == $scope.query){
                     $scope.filteredArray.push($scope.product);
                 }
            });
        });
        $scope.filteredProducts =  $scope.filterProductsByName( $scope.filteredArray );
        $scope.resultCount = $scope.filteredProducts.length;
    };

    $scope.filterProductsByName = function(arrayOfProducts){
        if($scope.productName === '' || $scope.productName === undefined){
            return arrayOfProducts;
        }

        var result = [];
        angular.forEach(arrayOfProducts, function(product){
            if(product.primaryName.toLowerCase().indexOf($scope.productName.toLowerCase()) === 0){
                result.push(product);
            }
        });
        return result;

    };

    $scope.productLoaded = function () {
        return !($scope.products === undefined || $scope.products === null);
    };

    // Programs list
    ReportPrograms.get(function (data) {
        var tmp = data.programs;
        $scope.programs = data.programs;
    });

    $scope.YesNo = function (tf) {
        var retval = '';
        if (tf === true) {
            retval = 'Yes';
        } else
        {
            retval = 'No';
        }
        return retval;
    };

}