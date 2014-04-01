/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function CreateNonFullSupplyController($scope, messageService) {
  var map = _.map($scope.facilityApprovedProducts, function (facilitySupportedProduct) {
    return facilitySupportedProduct.programProduct.productCategory;
  });

  $scope.nonFullSupplyProductsCategories = _.uniq(map, false, function (category) {
    return category.id;
  });

  $scope.getId = function (prefix, parent) {
    return prefix + "_" + parent.$parent.$index;
  };

  $scope.addNonFullSupplyLineItemsToRnr = function () {
    var validNonFullSupplyLineItems = [];
    var lineItem;
    var invalid = false;

    $($scope.addedNonFullSupplyProducts).each(function (i, nonFullSupplyProduct) {
      lineItem = nonFullSupplyProduct;
      if (lineItem.validateQuantityRequestedAndReason()) {
        invalid = true;
        return false;
      }
      validNonFullSupplyLineItems.push(lineItem);
    });

    if (invalid) {
      $scope.modalError = 'error.correct.highlighted';
      return;
    }
    $scope.modalError = undefined;

    $(validNonFullSupplyLineItems).each(function (i, rnrLineItem) {
      $scope.rnr.nonFullSupplyLineItems.push(rnrLineItem);
      $scope.rnr.fillPacksToShip(rnrLineItem);
    });

    $scope.rnr.nonFullSupplyLineItems.sort(function (lineItem1, lineItem2) {
      return lineItem1.compareTo(lineItem2);
    });

    $scope.page.nonFullSupply = $scope.rnr.nonFullSupplyLineItems.slice($scope.pageSize * ($scope.currentPage - 1), $scope.pageSize * $scope.currentPage);

    $scope.$emit('$routeUpdate');
    $scope.saveRnrForm.$dirty = (validNonFullSupplyLineItems.length > 0);
    $scope.nonFullSupplyProductsModal = false;
  };

  $scope.resetNonFullSupplyModal = function () {
    $scope.addedNonFullSupplyProducts = [];
    $scope.nonFullSupplyProductCategory = undefined;
    $scope.nonFullSupplyProductsToDisplay = undefined;
    $scope.clearNonFullSupplyProductModalData();
  };

  $scope.showAddNonFullSupplyModal = function () {
    $scope.resetNonFullSupplyModal();
    $scope.nonFullSupplyProductsModal = true;
  };

  $scope.clearNonFullSupplyProductModalData = function () {
    $scope.facilityApprovedProduct = undefined;
    $scope.newNonFullSupply = undefined;
  };

  $scope.labelForRnrColumn = function (columnName) {
    if ($scope.$parent.programRnrColumnList) return _.findWhere($scope.$parent.programRnrColumnList, {'name': columnName}).label + ":";
  };

  $scope.shouldDisableAddButton = function () {
    return !($scope.newNonFullSupply && $scope.newNonFullSupply.quantityRequested &&
      $scope.newNonFullSupply.reasonForRequestedQuantity &&
      $scope.facilityApprovedProduct);
  };

  $scope.addNonFullSupplyProductsByCategory = function () {
    prepareNFSLineItemFields();
    var rnrLineItem = new RegularRnrLineItem($scope.newNonFullSupply, $scope.rnr.period.numberOfMonths, $scope.programRnrColumnList, $scope.rnr.status);
    $scope.addedNonFullSupplyProducts.push(rnrLineItem);
    $scope.updateNonFullSupplyProductsToDisplay();
    $scope.clearNonFullSupplyProductModalData();
  };

  $scope.updateNonFullSupplyProductsToDisplay = function () {
    var addedNonFullSupplyProductList =
      _.pluck($scope.addedNonFullSupplyProducts, 'productCode')
        .concat(_.pluck($scope.rnr.nonFullSupplyLineItems, 'productCode'));
    if ($scope.nonFullSupplyProductCategory !== undefined) {
      $scope.nonFullSupplyProductsToDisplay = $.grep($scope.facilityApprovedProducts, function (facilityApprovedProduct) {
        return $.inArray(facilityApprovedProduct.programProduct.product.code, addedNonFullSupplyProductList) == -1 &&
          $.inArray(facilityApprovedProduct.programProduct.productCategory.name, [$scope.nonFullSupplyProductCategory.name]) === 0;
      });
    }
  };

  $scope.deleteCurrentNonFullSupplyLineItem = function (index) {
    $scope.addedNonFullSupplyProducts.splice(index, 1);
    $scope.updateNonFullSupplyProductsToDisplay();
  };


  function populateProductInformation() {
    var product = {};
    if ($scope.facilityApprovedProduct !== undefined) {
      angular.copy($scope.facilityApprovedProduct.programProduct.product, product);
      $scope.newNonFullSupply.productCode = product.code;
      $scope.newNonFullSupply.productName = product.primaryName;
      $scope.newNonFullSupply.product = (product.primaryName === null ? "" : (product.primaryName + " ")) +
        (product.form.code === null ? "" : (product.form.code + " ")) +
        (product.strength === null ? "" : (product.strength + " ")) +
        (product.dosageUnit.code === null ? "" : product.dosageUnit.code);
      $(['dosesPerDispensingUnit', 'packSize', 'roundToZero', 'packRoundingThreshold', 'dispensingUnit', 'fullSupply']).each(function (index, field) {
        $scope.newNonFullSupply[field] = product[field];
      });
      $scope.newNonFullSupply.maxMonthsOfStock = $scope.facilityApprovedProduct.maxMonthsOfStock;
      $scope.newNonFullSupply.dosesPerMonth = $scope.facilityApprovedProduct.programProduct.dosesPerMonth;
      $scope.newNonFullSupply.price = $scope.facilityApprovedProduct.programProduct.currentPrice;
      $scope.newNonFullSupply.productCategory = $scope.facilityApprovedProduct.programProduct.productCategory.name;
      $scope.newNonFullSupply.productDisplayOrder = $scope.facilityApprovedProduct.programProduct.displayOrder;
      $scope.newNonFullSupply.productCategoryDisplayOrder = $scope.nonFullSupplyProductCategory.displayOrder;
    }
  }

  function prepareNFSLineItemFields() {
    populateProductInformation();
    $(['quantityReceived', 'quantityDispensed', 'beginningBalance', 'stockInHand', 'totalLossesAndAdjustments', 'calculatedOrderQuantity', 'newPatientCount',
      'stockOutDays', 'normalizedConsumption', 'amc', 'maxStockQuantity']).each(function (index, field) {
        $scope.newNonFullSupply[field] = 0;
      });
    $scope.newNonFullSupply.rnrId = $scope.$parent.rnr.id;
  }


  $scope.highlightRequiredFieldInModal = function (value) {
    if (isUndefined(value)) return "required-error";
    return null;
  };

  $scope.formatNoMatches = function () {
    return messageService.get('msg.no.matches.found');
  };

}


