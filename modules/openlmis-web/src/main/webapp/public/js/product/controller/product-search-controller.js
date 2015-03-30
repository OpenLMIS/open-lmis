/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function ProductSearchController($scope, ProgramProductsSearch, AdjustmentProducts, $location, navigateBackService) {

  $scope.searchOptions = [
    {value: "product", name: "option.value.product"},
    {value: "program", name: "option.value.program"}
  ];

  $scope.showResults = false;
  $scope.currentPage = 1;
  $scope.selectedSearchOption = navigateBackService.selectedSearchOption || $scope.searchOptions[0];

  $scope.selectSearchType = function (searchOption) {
    $scope.selectedSearchOption = searchOption;
  };

  $scope.$on('$viewContentLoaded', function () {
    $scope.query = navigateBackService.query;
  });

  $scope.edit = function (id) {
    var data = {query: $scope.query, selectedSearchOption: $scope.selectedSearchOption};
    navigateBackService.setData(data);
    $location.path('edit/' + id);
  };

  $scope.addRationing = function (id) {
    var data = {query: $scope.query, selectedSearchOption: $scope.selectedSearchOption};
    navigateBackService.setData(data);
    $location.path('rationing/' + id);
  };
  $scope.editRationing = function (id) {
    var data = {query: $scope.query, selectedSearchOption: $scope.selectedSearchOption};
    navigateBackService.setData(data);
    $location.path('edit/rationing/' + id);
  };

  $scope.loadProducts = function (page, lastQuery) {
    if (!($scope.query || lastQuery)) return;
    lastQuery ? getProducts(page, lastQuery) : getProducts(page, $scope.query);
  };

  function getProducts(page, query) {
    query = query.trim();
    $scope.searchedQuery = query;
    loadAdjustmentProducts();
    ProgramProductsSearch.get({page: page, searchParam: $scope.searchedQuery, column: $scope.selectedSearchOption.value}, function (data) {
      $scope.programProducts = data.programProductList;
      $scope.pagination = data.pagination;
      $scope.totalItems = $scope.pagination.totalRecords;
      $scope.currentPage = $scope.pagination.page;
      $scope.showResults = true;
    }, {});
  }

  var loadAdjustmentProducts = function(){
    AdjustmentProducts.get({}, function (data){
      $scope.adjustmentProducts = data.adjustmentProducts;
    });
  };
  $scope.isAdjustmentExists = function (productId){
    var recordExists = false;
    angular.forEach($scope.adjustmentProducts, function(adjustment){
      if(adjustment.product.id === productId) {
        recordExists = true;
      }
    });
    return recordExists;
  };

  $scope.$watch('currentPage', function () {
    if ($scope.currentPage !== 0)
      $scope.loadProducts($scope.currentPage, $scope.searchedQuery);
  });

  $scope.triggerSearch = function (event) {
    if (event.keyCode === 13) {
      $scope.loadProducts(1);
    }
  };

  $scope.showCategory = function (index) {
    if ($scope.programProducts[index].productCategory) {
      return !(((index > 0) && ($scope.programProducts[index].productCategory.name === $scope.programProducts[index - 1].productCategory.name)));
    }
    else {
      return !((index > 0) && $scope.programProducts[index].productCategory === $scope.programProducts[index - 1].productCategory);
    }
  };

  $scope.clearSearch = function () {
    $scope.query = "";
    $scope.totalItems = 0;
    $scope.programProducts = [];
    $scope.showResults = false;
    angular.element("#searchProgramProduct").focus();
  };
}