/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function FacilityApprovedProductController($scope, programs, facilityTypes, ProgramProductsFilter, FacilityTypeApprovedProducts, $dialog, messageService) {

  $scope.programs = programs;
  $scope.facilityTypes = facilityTypes;

  $scope.showResults = false;
  $scope.currentPage = 1;

  $scope.loadProducts = function (page, lastQuery) {
    if (!$scope.program || !$scope.facilityType) return;
    if (lastQuery !== undefined) {
      getProducts(page, lastQuery);
    } else {
      getProducts(page, $scope.query);
    }
  };

  function getProducts(page, query) {
    $scope.searchedQuery = query || "";
    FacilityTypeApprovedProducts.get({page: page, searchParam: $scope.searchedQuery, programId: $scope.program.id,
      facilityTypeId: $scope.facilityType.id}, function (data) {
      $scope.facilityApprovedProducts = data.facilityApprovedProducts;
      $scope.pagination = data.pagination;
      $scope.totalItems = $scope.pagination.totalRecords;
      $scope.currentPage = $scope.pagination.page;
      $scope.showResults = true;
    }, {});
  }

  $scope.$watch('currentPage', function () {
    if ($scope.currentPage !== 0)
      $scope.loadProducts($scope.currentPage, $scope.searchedQuery);
  });

  $scope.triggerSearch = function (event) {
    if (event.keyCode === 13) {
      $scope.loadProducts(1);
    }
  };

  $scope.showCategory = function (list, index) {
    return !((index > 0 ) && (list[index].programProduct.productCategory.name === list[index - 1].programProduct.productCategory.name));
  };

  $scope.clearSearch = function () {
    $scope.query = "";
    $scope.loadProducts(1);
  };

  $scope.edit = function (facilityApprovedProduct) {
    facilityApprovedProduct.underEdit = true;
    facilityApprovedProduct.previousMaxMonthsOfStock = facilityApprovedProduct.maxMonthsOfStock;
    facilityApprovedProduct.previousMinMonthsOfStock = facilityApprovedProduct.minMonthsOfStock;
    facilityApprovedProduct.previousEop = facilityApprovedProduct.eop;
  };

  var deleteFacilityApprovedProduct = function (result) {
    if (result) {
      FacilityTypeApprovedProducts.remove({id: $scope.facilityApprovedProductToBeDeleted.id}, $scope.facilityApprovedProductToBeDeleted, function (data) {
        $scope.message = messageService.get(data.success, $scope.facilityApprovedProductToBeDeleted.programProduct.product.primaryName);
        $scope.loadProducts($scope.currentPage);
      }, {});
    }
  };

  $scope.confirmDelete = function (facilityApprovedProduct) {
    $scope.facilityApprovedProductToBeDeleted = facilityApprovedProduct;
    var options = {
      id: "confirmDialog",
      header: "label.confirm.action",
      body: messageService.get('msg.delete.facility.approved.product.confirmation',
        $scope.facilityApprovedProductToBeDeleted.programProduct.product.primaryName, $scope.facilityType.name, $scope.program.name)
    };
    OpenLmisDialog.newDialog(options, deleteFacilityApprovedProduct, $dialog);
  };

  $scope.cancel = function (facilityApprovedProduct) {
    facilityApprovedProduct.maxMonthsOfStock = facilityApprovedProduct.previousMaxMonthsOfStock;
    facilityApprovedProduct.minMonthsOfStock = facilityApprovedProduct.previousMinMonthsOfStock;
    facilityApprovedProduct.eop = facilityApprovedProduct.previousEop;
    facilityApprovedProduct.underEdit = false;
    facilityApprovedProduct.error = "";
  };

  $scope.focusSuccessMessageDiv = function () {
    var searchFacilityApprovedProductLabel = angular.element('#searchFacilityApprovedProductLabel').get(0);
    if (!isUndefined(searchFacilityApprovedProductLabel)) {
      searchFacilityApprovedProductLabel.scrollIntoView();
    }
  };

  function updateListToDisplay(updatedFacilityApprovedProduct) {
    for (var i = 0; i < $scope.facilityApprovedProducts.length; i++) {
      if ($scope.facilityApprovedProducts[i].id == updatedFacilityApprovedProduct.id) {
        $scope.facilityApprovedProducts[i] = updatedFacilityApprovedProduct;
      }
    }
  }

  $scope.update = function (facilityApprovedProduct) {
    if (isUndefined(facilityApprovedProduct.maxMonthsOfStock)) {
      facilityApprovedProduct.error = 'error.correct.highlighted';
      return;
    }
    facilityApprovedProduct.facilityType = $scope.facilityType;
    facilityApprovedProduct.programProduct.program = $scope.program;

    FacilityTypeApprovedProducts.update({id: facilityApprovedProduct.id}, facilityApprovedProduct, function (data) {
      $scope.updatedFacilityApprovedProduct = data.facilityApprovedProduct;
      $scope.message = data.success;
      facilityApprovedProduct.underEdit = false;
      facilityApprovedProduct.error = "";
      updateListToDisplay($scope.updatedFacilityApprovedProduct);
    }, function (data) {
      facilityApprovedProduct.error = data.data.error;
    });
    $scope.focusSuccessMessageDiv();
  };

  $scope.openFacilityApprovedProductsModal = function () {
    ProgramProductsFilter.get({programId: $scope.program.id, facilityTypeId: $scope.facilityType.id}, function (data) {
      $scope.programProductList = data.programProductList;
      var productCategories = _.pluck($scope.programProductList, "productCategory");
      $scope.productCategories = _.uniq(productCategories, function (category) {
        return category.id;
      });
      $scope.facilityApprovedProductsModal = true;
    }, {});
  };
}

FacilityApprovedProductController.resolve = {
  programs: function ($q, $timeout, Programs) {
    var deferred = $q.defer();

    $timeout(function () {
      Programs.get({type: "pull"}, function (data) {
        deferred.resolve(data.programs);
      }, {});
    }, 100);
    return deferred.promise;
  },

  facilityTypes: function ($q, $route, $timeout, FacilityTypes) {
    var deferred = $q.defer();

    $timeout(function () {
      FacilityTypes.get({}, function (data) {
        deferred.resolve(data.facilityTypeList);
      }, {});
    }, 100);
    return deferred.promise;
  }
};