function RequisitionNonFullSupplyController($scope, $rootScope, FacilityApprovedProducts, $routeParams, $location) {
  FacilityApprovedProducts.get({facilityId: $routeParams.facility, programId: $routeParams.program}, function (data) {
    $scope.facilityApprovedProducts = data.nonFullSupplyProducts;

    var map = _.map($scope.facilityApprovedProducts, function (facilitySupportedProduct) {
      return facilitySupportedProduct.programProduct.product.category;
    });

    $scope.nonFullSupplyProductsCategories = _.uniq(map, false, function (category) {
      return category.id;
    });
  });

  $scope.getId = function (prefix, parent) {
    return prefix + "_" + parent.$parent.$index;
  };

  $scope.addNonFullSupplyLineItemToRnr = function () {
    $rootScope.message = "";
    $($scope.addedNonFullSupplyProducts).each(function (i, nonFullSupplyProduct) {
      var lineItem = new RnrLineItem(nonFullSupplyProduct, $scope.rnr.period.numberOfMonths, $scope.programRnrColumnList, $scope.rnr.status);
      $scope.rnr.nonFullSupplyLineItems.push(lineItem);
      $scope.rnr.fillPacksToShip(lineItem);
    })

    $scope.facilityApprovedProduct = undefined;
    $scope.newNonFullSupply = undefined;
    $scope.updateNonFullSupplyProductsToDisplay();
    $scope.fillPagedGridData();
    if ($scope.addedNonFullSupplyProducts.length > 0) {
      $rootScope.message = "Products added successfully";
      setTimeout(function () {
        $scope.$apply(function () {
          angular.element("#saveSuccessMsgDiv").fadeOut('slow', function () {
            $rootScope.message = '';
          });
        });
      }, 3000);
    }
    $scope.nonFullSupplyProductsModal = false;
  };

  $scope.resetNonFullSupplyModal = function () {
    $scope.addedNonFullSupplyProducts = [];
    $scope.nonFullSupplyProductCategory = undefined;
    $scope.facilityApprovedProduct = undefined;
    $scope.newNonFullSupply = undefined;
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
    return !($scope.newNonFullSupply && $scope.newNonFullSupply.quantityRequested && $scope.newNonFullSupply.reasonForRequestedQuantity
      && $scope.facilityApprovedProduct);
  };

  $scope.addNonFullSupplyProductsByCategory = function () {
    var addedNonFullSupplyProduct = {};
    addedNonFullSupplyProduct.quantityRequested = $scope.newNonFullSupply.quantityRequested;
    addedNonFullSupplyProduct.reasonForRequestedQuantity = $scope.newNonFullSupply.reasonForRequestedQuantity;
    prepareNFSLineItemFields($scope.facilityApprovedProduct, addedNonFullSupplyProduct);

    $scope.addedNonFullSupplyProducts.push(addedNonFullSupplyProduct);
    $scope.updateNonFullSupplyProductsToDisplay();
    $scope.facilityApprovedProduct = undefined;
    $scope.newNonFullSupply.reasonForRequestedQuantity = undefined;
    $scope.newNonFullSupply.quantityRequested = undefined;
  }

  function populateProductInformation(nonFullSupplyProduct, newNonFullSupply) {
    var product = {};
    if (nonFullSupplyProduct != undefined) {
      angular.copy(nonFullSupplyProduct.programProduct.product, product);
      newNonFullSupply.productCode = product.code;
      newNonFullSupply.product = (product.primaryName == null ? "" : (product.primaryName + " ")) +
        (product.form.code == null ? "" : (product.form.code + " ")) +
        (product.strength == null ? "" : (product.strength + " ")) +
        (product.dosageUnit.code == null ? "" : product.dosageUnit.code);
      $(['dosesPerDispensingUnit', 'packSize', 'roundToZero', 'packRoundingThreshold', 'dispensingUnit', 'fullSupply']).each(function (index, field) {
        newNonFullSupply[field] = product[field];
      });
      newNonFullSupply.maxMonthsOfStock = nonFullSupplyProduct.maxMonthsOfStock;
      newNonFullSupply.dosesPerMonth = nonFullSupplyProduct.programProduct.dosesPerMonth;
      newNonFullSupply.price = nonFullSupplyProduct.programProduct.currentPrice;
      newNonFullSupply.productName = product.primaryName;
      newNonFullSupply.productCategory = nonFullSupplyProduct.programProduct.product.category.code;
    }
  }

  function prepareNFSLineItemFields(nonFullSupplyProduct, newNonFullSupply) {
    populateProductInformation(nonFullSupplyProduct, newNonFullSupply);
    $(['quantityReceived', 'quantityDispensed', 'beginningBalance', 'stockInHand', 'totalLossesAndAdjustments', 'calculatedOrderQuantity', 'newPatientCount',
      'stockOutDays', 'normalizedConsumption', 'amc', 'maxStockQuantity']).each(function (index, field) {
        newNonFullSupply[field] = 0;
      });
    newNonFullSupply.rnrId = $scope.$parent.rnr.id;
  }

  $scope.updateNonFullSupplyProductsToDisplay = function () {
    $scope.nonFullSupplyProductsToDisplay = undefined;
    var usedNonFullSupplyProducts = _.pluck($scope.addedNonFullSupplyProducts, 'productCode');
    var usedNonFullSupplyProductsOnRnr = _.pluck($scope.rnr.nonFullSupplyLineItems, 'productCode');
    var addedNonFullSupplyProductList = usedNonFullSupplyProducts.concat(usedNonFullSupplyProductsOnRnr);
    if ($scope.nonFullSupplyProductCategory != undefined) {
      $scope.nonFullSupplyProductsToDisplay = $.grep($scope.facilityApprovedProducts, function (facilityApprovedProduct) {
        return $.inArray(facilityApprovedProduct.programProduct.product.code, addedNonFullSupplyProductList) == -1 && $.inArray(facilityApprovedProduct.programProduct.product.category.name, [$scope.nonFullSupplyProductCategory.name]) == 0;
      });
    }
  }
  $scope.deleteCurrentNonFullSupplyLineItem = function (index) {
    $scope.addedNonFullSupplyProducts.splice(index, 1);
    $scope.updateNonFullSupplyProductsToDisplay();
    $scope.facilityApprovedProduct = undefined;
  }
}

