function RequisitionFormController($scope, ReferenceData, ProgramRnRColumnList, $location, FacilityApprovedProducts, Requisitions, $routeParams, LossesAndAdjustmentsReferenceData, $rootScope) {
  FacilityApprovedProducts.get({facilityId:$routeParams.facility, programId:$routeParams.program}, function (data) {
    $scope.nonFullSupplyProducts = data.nonFullSupplyProducts;
  }, function () {
  });

  ReferenceData.get({}, function (data) {
    $scope.currency = data.currency;
  }, function () {
  });

  ProgramRnRColumnList.get({programId:$routeParams.program}, function (data) {
    if (data.rnrColumnList && data.rnrColumnList.length > 0) {
      $scope.programRnrColumnList = data.rnrColumnList;
      $scope.addNonFullSupplyLineItemButtonShown = _.findWhere($scope.programRnrColumnList, {'name':'quantityRequested'});
      $scope.prepareRnr();
    } else {
      $scope.$parent.error = "Please contact Admin to define R&R template for this program";
      $location.path("/init-rnr");
    }
  }, function () {
    $location.path("/init-rnr");
  });

  $scope.isFormDisabled = function() {
    if ($scope.rnr || $scope.$parent.rnr) {
      if ($scope.rnr.status == 'AUTHORIZED') return true;
      if (($scope.rnr.status == 'SUBMITTED' && !$rootScope.hasPermission('AUTHORIZE_REQUISITION')) || ($scope.rnr.status == 'INITIATED' && !$rootScope.hasPermission('CREATE_REQUISITION'))) return true;
    }
    return false;
  };

  function resetCostsIfNull() {
    var rnr = $scope.rnr;
    if (rnr == null) return;
    if (!rnr.fullSupplyItemsSubmittedCost)
      rnr.fullSupplyItemsSubmittedCost = 0;
    if (!rnr.nonFullSupplyItemsSubmittedCost)
      rnr.nonFullSupplyItemsSubmittedCost = 0;
  }

  $scope.prepareRnr = function() {
    var rnr = $scope.rnr;

    var lineItemsJson = rnr.lineItems;
    rnr.lineItems = [];
    $(lineItemsJson).each(function (i, lineItem) {
      rnr.lineItems.push(new RnrLineItem(lineItem, $scope.rnr, $scope.programRnrColumnList));
    });

    var nonFullSupplyLineItemsJson = rnr.nonFullSupplyLineItems;
    rnr.nonFullSupplyLineItems = [];
    $(nonFullSupplyLineItemsJson).each(function (i, lineItem) {
      rnr.nonFullSupplyLineItems.push(new RnrLineItem(lineItem, $scope.rnr, $scope.programRnrColumnList))
    });

    resetCostsIfNull();
    $scope.formDisabled = $scope.isFormDisabled();
  }

  function valid() {
    if ($scope.saveRnrForm.$error.rnrError) {
      $scope.submitError = "Please correct the errors on the R&R form before submitting";
      $scope.submitMessage = "";
      return false;
    }
    if ($scope.saveRnrForm.$error.required) {
      $scope.saveRnr();
      $scope.inputClass = "required";
      $scope.submitMessage = "";
      $scope.submitError = 'Please complete the highlighted fields on the R&R form before submitting';
      return false;
    }
    if (!formulaValid()) {
      $scope.saveRnr();
      $scope.submitError = "Please correct the errors on the R&R form before submitting";
      $scope.submitMessage = "";
      return false;
    }
    return true;
  }

  function resetFlags() {
    $scope.submitError = "";
    $scope.inputClass = "";
    $scope.submitMessage = "";
  }

  $scope.saveRnr = function () {
    resetFlags();
    if ($scope.saveRnrForm.$error.rnrError) {
      $scope.error = "Please correct errors before saving.";
      $scope.message = "";
      return;
    }
    var rnr = removeExtraDataForPostFromRnr();
    Requisitions.update({id:$scope.rnr.id, operation:"save"}, rnr, function (data) {
      $scope.message = data.success;
      $scope.error = "";
    }, function (data) {
      $scope.error = data.error;
      $scope.message = "";
    });
  };

  function removeExtraDataForPostFromRnr() {
    var rnr = {"id":$scope.rnr.id, "lineItems":[], "nonFullSupplyLineItems":[]};

    _.each($scope.rnr.lineItems, function (lineItem) {
      rnr.lineItems.push(_.omit(lineItem, ['rnr', 'programRnrColumnList']));
    });
    _.each($scope.rnr.nonFullSupplyLineItems, function (lineItem) {
      rnr.nonFullSupplyLineItems.push(_.omit(lineItem, ['rnr', 'programRnrColumnList']));
    });
    return rnr;
  }

  $scope.submitRnr = function () {
    resetFlags();
    if (!valid()) return;
    var rnr = removeExtraDataForPostFromRnr();
    Requisitions.update({id:$scope.rnr.id, operation:"submit"},
      rnr, function (data) {
        $scope.rnr.status = "SUBMITTED";
        $scope.formDisabled = !$rootScope.hasPermission('AUTHORIZE_REQUISITION');
        $scope.submitMessage = data.success;
        $scope.submitError = "";
      }, function (data) {
        $scope.submitError = data.data.error;
      });
  };

  $scope.authorizeRnr = function () {
    if (!valid()) return;
    var rnr = removeExtraDataForPostFromRnr();
    Requisitions.update({id:$scope.rnr.id, operation:"authorize"}, rnr, function (data) {
      $scope.rnr.status = "AUTHORIZED";
      $scope.formDisabled = true;
      $scope.submitMessage = data.success;
      $scope.submitError = "";
    }, function (data) {
      $scope.submitError = data.data.error;
    });
  };

  $scope.getId = function (prefix, parent, isLossAdjustment) {
    if (isLossAdjustment != null && isLossAdjustment != isUndefined && isLossAdjustment) {
      return prefix + "_" + parent.$parent.$parent.$index + "_" + parent.$parent.$parent.$parent.$index;
    }
    return prefix + "_" + parent.$parent.$parent.$index;
  };

  $scope.hide = function () {
    return "";
  };

  $scope.highlightRequired = function (value) {
    if ($scope.inputClass == 'required' && (isUndefined(value) || value.toString().trim().length == 0)) {
      return "required-error";
    }
    return null;
  };

  $scope.highlightRequiredFieldInModal = function (value) {
    if (isUndefined(value)) return "required-error";
    return null;
  };

  $scope.highlightWarningBasedOnField = function (value, field) {
    if ((isUndefined(value) || value.trim().length == 0 || value == false) && $scope.inputClass == 'required' && field) {
      return "warning-error";
    }
    return null;
  };

  $scope.highlightWarning = function (value) {
    if ((isUndefined(value) || value.trim().length == 0 || value == false) && $scope.inputClass == 'required') {
      return "warning-error";
    }
    return null;
  };

  $scope.totalCost = function () {
    if (!$scope.rnr) return;
    return parseFloat(parseFloat($scope.rnr.fullSupplyItemsSubmittedCost) + parseFloat($scope.rnr.nonFullSupplyItemsSubmittedCost)).toFixed(2);
  };

  // TODO: Push this method to rnr-line-item
  function formulaValid() {
    var valid = true;
    $($scope.rnr.lineItems).each(function (index, lineItem) {
      if (lineItem.arithmeticallyInvalid() || lineItem.stockInHand < 0 || lineItem.quantityDispensed < 0) {
        valid = false;
        return false;
      }
    });
    return valid;
  }

  $scope.getCellErrorClass = function (rnrLineItem) {
    return rnrLineItem.getErrorMessage() ? 'cell-error-highlight' : '';
  };

  $scope.getRowErrorClass = function (rnrLineItem) {
    return $scope.getCellErrorClass(rnrLineItem) ? 'row-error-highlight' : '';
  };

  $scope.labelForRnrColumn = function (columnName) {
    if ($scope.programRnrColumnList) return _.findWhere($scope.programRnrColumnList, {'name':columnName}).label + ":";
  };

  $scope.addNonFullSupplyLineItem = function () {
    prepareNFSLineItemFields();
    var lineItem = new RnrLineItem($scope.newNonFullSupply, $scope.rnr, $scope.programRnrColumnList);

    $scope.rnr.nonFullSupplyLineItems.push(lineItem);
    lineItem.fillPacksToShipBasedOnCalculatedOrderQuantityOrQuantityRequested();
    $scope.facilityApprovedProduct = undefined;
    $scope.newNonFullSupply = undefined;
    updateNonFullSupplyProductsToDisplay();
  };

  $scope.showAddNonFullSupplyModal = function () {
    updateNonFullSupplyProductsToDisplay();
    $scope.nonFullSupplyProductsModal = true;
  };

  function populateProductInformation() {
    var product = {};
    angular.copy($scope.facilityApprovedProduct.programProduct.product, product);
    $scope.newNonFullSupply.productCode = product.code;
    $scope.newNonFullSupply.product = (product.primaryName == null ? "" : (product.primaryName + " ")) +
      (product.form.code == null ? "" : (product.form.code + " ")) +
      (product.strength == null ? "" : (product.strength + " ")) +
      (product.dosageUnit.code == null ? "" : product.dosageUnit.code);
    $(['dosesPerDispensingUnit', 'packSize', 'roundToZero', 'packRoundingThreshold', 'dispensingUnit', 'fullSupply']).each(function (index, field) {
      $scope.newNonFullSupply[field] = product[field];
    });
    $scope.newNonFullSupply.maxMonthsOfStock = $scope.facilityApprovedProduct.maxMonthsOfStock;
    $scope.newNonFullSupply.dosesPerMonth = $scope.facilityApprovedProduct.programProduct.dosesPerMonth;
    $scope.newNonFullSupply.price = $scope.facilityApprovedProduct.programProduct.currentPrice;
  }

  function prepareNFSLineItemFields() {
    populateProductInformation();
    $(['quantityReceived', 'quantityDispensed', 'beginningBalance', 'stockInHand', 'totalLossesAndAdjustments', 'calculatedOrderQuantity', 'newPatientCount',
      'stockOutDays', 'normalizedConsumption', 'amc', 'maxStockQuantity']).each(function (index, field) {
        $scope.newNonFullSupply[field] = 0;
      });
    $scope.newNonFullSupply.rnrId = $scope.rnr.id;
  }

  function updateNonFullSupplyProductsToDisplay() {
    var usedNonFullSupplyProducts = _.pluck($scope.rnr.nonFullSupplyLineItems, 'productCode');
    $scope.nonFullSupplyProductsToDisplay = $.grep($scope.nonFullSupplyProducts, function (facilityApprovedProduct) {
      return $.inArray(facilityApprovedProduct.programProduct.product.code, usedNonFullSupplyProducts) == -1;
    });
  }
}
