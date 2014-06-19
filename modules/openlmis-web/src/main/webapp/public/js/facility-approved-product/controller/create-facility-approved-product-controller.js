function CreateFacilityApprovedProductController($scope, ProgramProductsFilter, FacilityTypeApprovedProducts, messageService) {
  $scope.addedFacilityTypeApprovedProducts = [];
  $scope.selectedProgramProductList = [];

  var loadProductsWithCategory = function () {
    ProgramProductsFilter.get({programId: $scope.$parent.$parent.program.id, facilityTypeId: $scope.$parent.$parent.facilityType.id}, function (data) {
      $scope.programProductList = data.programProductList;
      var productCategories = _.pluck(data.programProductList, "productCategory");
      $scope.productCategories = _.uniq(productCategories, function (category) {
        return category.id;
      });
    }, {});
  };

  var fillFacilityTypeApprovedProduct = function (selectedProduct) {
    return {
      "facilityType": $scope.$parent.$parent.facilityType,
      "programProduct": {"program": $scope.$parent.$parent.program, "product": selectedProduct},
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
    $scope.programProductList = addAndRemoveFromProgramProductList($scope.programProductList, $scope.selectedProgramProductList, selectedProduct.code);
  };

  $scope.getHeader = function () {
    return messageService.get('header.code') + " | " +
        messageService.get('header.name') + " | " +
        messageService.get('header.strength') + " | " +
        messageService.get('header.unit.of.measure') + " | " +
        messageService.get('header.template.type');
  };

  $scope.$parent.$parent.$watch('facilityApprovedProductsModal', function () {
    if ($scope.$parent.$parent.facilityApprovedProductsModal)
      loadProductsWithCategory();
  });

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

  $scope.addFacilityTypeApprovedProduct = function () {
    var selectedProduct = $.parseJSON($scope.productSelected);
    var facilityApprovedProgramProduct = fillFacilityTypeApprovedProduct(selectedProduct);
    $scope.addedFacilityTypeApprovedProducts.push(facilityApprovedProgramProduct);
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
  };

  $scope.cancelFacilityTypeApprovedProducts = function () {
    $scope.$parent.$parent.facilityApprovedProductsModal = false;
    $scope.addedFacilityTypeApprovedProducts = [];
    $scope.$parent.$parent.message = undefined;
    clearFacilityApprovedProductModalData();
  };
}