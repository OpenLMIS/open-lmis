/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function CreateFacilityApprovedProductController($scope, FacilityTypeApprovedProducts, messageService) {
  $scope.addedFacilityTypeApprovedProducts = [];
  $scope.selectedProgramProductList = [];

  var fillFacilityTypeApprovedProduct = function (selectedProduct) {
    return {
      "facilityType": $scope.$parent.$parent.facilityType,
      "programProduct": {"program": $scope.$parent.$parent.program, "product": selectedProduct, "productCategory": $scope.productCategorySelected},
      "maxMonthsOfStock": $scope.newFacilityTypeApprovedProduct.maxMonthsOfStock,
      "minMonthsOfStock": $scope.newFacilityTypeApprovedProduct.minMonthsOfStock,
      "eop": $scope.newFacilityTypeApprovedProduct.eop
    };
  };

  var clearFacilityApprovedProductModalData = function () {
    $scope.productCategorySelected = undefined;
    $scope.productSelected = undefined;
    $scope.products = undefined;
    $scope.newFacilityTypeApprovedProduct = undefined;
  };

  var addAndRemoveFromProgramProductList = function (listToBeFiltered, listToBeAdded, value) {
    listToBeAdded.push(_.find(listToBeFiltered, function (programProduct) {
      return  programProduct.product.code == value;
    }));

    listToBeFiltered = _.reject(listToBeFiltered, function (programProduct) {
      return  programProduct.product.code == value;
    });
    return listToBeFiltered;
  };

  var filterProductsToDisplay = function (selectedProduct) {
    $scope.$parent.$parent.programProductList = addAndRemoveFromProgramProductList($scope.programProductList, $scope.selectedProgramProductList, selectedProduct.code);
  };

  $scope.getHeader = function () {
    return messageService.get('header.code') + " | " +
      messageService.get('header.name') + " | " +
      messageService.get('header.strength') + " | " +
      messageService.get('header.unit.of.measure') + " | " +
      messageService.get('header.template.type');
  };

  $scope.formatResult = function (product) {
    if (!product) return false;
    var productData = product.text.split("|");
    var productType = productData[4].trim();

    if (productType.toLowerCase() === "true") {
      productType = messageService.get('label.full.supply');
    }
    if (productType.toLowerCase() === "false") {
      productType = messageService.get('label.non.full.supply');
    }
    return "<div class='row-fluid'>" +
      "<div class='span2'>" + productData[0] + "</div>" +
      "<div class='span4'>" + productData[1] + "</div>" +
      "<div class='span2'>" + productData[2] + "</div>" +
      "<div class='span2'>" + productData[3] + "</div>" +
      "<div class='span2'>" + productType + "</div>" +
      "</div>";
  };

  $scope.formatSelection = function (product) {
    if (!product) return false;
    var productData = product.text.split("|");
    return productData[0] + " - " + productData[1];
  };

  $scope.filterProductsByCategory = function () {
    var filteredProducts = _.filter($scope.programProductList, function (programProduct) {
      return programProduct.productCategory.code === $scope.productCategorySelected.code;
    });

    $scope.products = _.pluck(filteredProducts, "product");
  };

  $scope.isAddDisabled = function () {
    return !($scope.newFacilityTypeApprovedProduct && $scope.newFacilityTypeApprovedProduct.maxMonthsOfStock &&
      $scope.productCategorySelected && $scope.productSelected);
  };

  function sortByCategory(facilityTypeApprovedProducts) {
    return _(facilityTypeApprovedProducts).chain().sortBy(function (facilityApprovedProduct) {
      return facilityApprovedProduct.programProduct.product.code.toLowerCase();
    }).sortBy(function (facilityApprovedProduct) {
      return facilityApprovedProduct.programProduct.productCategory.name.toLowerCase();
    }).value();
  }

  $scope.addFacilityTypeApprovedProduct = function () {
    var selectedProduct = $.parseJSON($scope.productSelected);
    var facilityApprovedProgramProduct = fillFacilityTypeApprovedProduct(selectedProduct);
    $scope.addedFacilityTypeApprovedProducts.push(facilityApprovedProgramProduct);
    $scope.addedFacilityTypeApprovedProducts = sortByCategory($scope.addedFacilityTypeApprovedProducts);
    filterProductsToDisplay(selectedProduct);
    clearFacilityApprovedProductModalData();
  };

  $scope.removeFacilityTypeApprovedProduct = function (index) {
    var removedFTAProduct = $scope.addedFacilityTypeApprovedProducts[index];
    $scope.addedFacilityTypeApprovedProducts.splice(index, 1);
    $scope.selectedProgramProductList = addAndRemoveFromProgramProductList($scope.selectedProgramProductList, $scope.programProductList, removedFTAProduct.programProduct.product.code);
    clearFacilityApprovedProductModalData();
  };

  $scope.addFacilityTypeApprovedProducts = function () {
    if ($scope.addedFacilityTypeApprovedProducts && $scope.addedFacilityTypeApprovedProducts.length > 0) {
      var invalid = false;

      _.each($scope.addedFacilityTypeApprovedProducts, function (facilityTypeApprovedProduct) {
        if (isUndefined(facilityTypeApprovedProduct.maxMonthsOfStock) || isUndefined(facilityTypeApprovedProduct.facilityType) || isUndefined(facilityTypeApprovedProduct.programProduct)) {
          invalid = true;
          return false;
        }
        return true;
      });
      if (invalid) {
        $scope.modalError = 'error.correct.highlighted';
        return;
      }
      $scope.modalError = undefined;

      FacilityTypeApprovedProducts.save({}, $scope.addedFacilityTypeApprovedProducts, function (data) {
        $scope.$parent.$parent.message = data.success;
        $scope.$parent.$parent.facilityApprovedProductsModal = false;
        $scope.$parent.$parent.loadProducts(1);
        $scope.addedFacilityTypeApprovedProducts = [];
        clearFacilityApprovedProductModalData();
      }, function (data) {
        $scope.$parent.$parent.message = undefined;
        $scope.modalError = data.data.error;
      });
      $scope.$parent.$parent.focusSuccessMessageDiv();
    }
  };

  $scope.cancelFacilityTypeApprovedProducts = function () {
    $scope.$parent.$parent.facilityApprovedProductsModal = false;
    $scope.addedFacilityTypeApprovedProducts = [];
    $scope.$parent.$parent.message = undefined;
    clearFacilityApprovedProductModalData();
  };
}