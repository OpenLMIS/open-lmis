function RequisitionFormController($scope, ReferenceData, ProgramRnRColumnList, $location, FacilityApprovedProducts, Requisitions, $routeParams, LossesAndAdjustmentsReferenceData, $rootScope) {

  $scope.lossesAndAdjustmentsModal = [];

  FacilityApprovedProducts.get({facilityId:$routeParams.facility, programId:$routeParams.program},
      function (data) {
        $scope.nonFullSupplyProducts = data.nonFullSupplyProducts;
      }, function () {
      });

  ReferenceData.get({}, function (data) {
    $scope.currency = data.currency;
  }, function () {
  });

  LossesAndAdjustmentsReferenceData.get({}, function (data) {
    $scope.allTypes = data.lossAdjustmentTypes;
  }, function () {
  });

  ProgramRnRColumnList.get({programId:$routeParams.program}, function (data) {
    if (data.rnrColumnList && data.rnrColumnList.length > 0) {
      $scope.programRnrColumnList = data.rnrColumnList;
      $scope.addNonFullSupplyLineItemButtonShown = _.findWhere($scope.programRnrColumnList, {'name':'quantityRequested'});
    } else {
      $scope.$parent.error = "Please contact Admin to define R&R template for this program";
      $location.path("/init-rnr");
    }
  }, function () {
    $location.path("/init-rnr");
  });

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

  $scope.saveRnr = function () {
    $scope.submitError = "";
    $scope.inputClass = "";
    $scope.submitMessage = "";
    if ($scope.saveRnrForm.$error.rnrError) {
      $scope.error = "Please correct errors before saving.";
      $scope.message = "";
      return;
    }

    Requisitions.update({id:$scope.rnr.id, operation:"save"},
        $scope.rnr, function (data) {
          $scope.message = data.success;
          $scope.error = "";
        }, function (data) {
          $scope.error = data.error;
          $scope.message = "";
        });
  };

  $scope.submitRnr = function () {
    if (!valid()) return;
    $scope.rnr.nonFullSupplyLineItems = $scope.nonFullSupplyLineItems;
    Requisitions.update({id:$scope.rnr.id, operation:"submit"},
        $scope.rnr, function (data) {
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
    Requisitions.update({id:$scope.rnr.id, operation:"authorize"},
        $scope.rnr, function (data) {
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
    if ($scope.inputClass == 'required' && (isUndefined(value) || value.trim().length == 0)) {
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
  $scope.saveLossesAndAdjustmentsForRnRLineItem = function (rnrLineItem ) {
    if (!isValidLossesAndAdjustments(rnrLineItem)) return;

    rnrLineItem.reEvaluateTotalLossesAndAdjustments($scope.rnr, $scope.programRnrColumnList);
    $scope.lossesAndAdjustmentsModal[rnrLineItem.id] = false;
  };

  $scope.resetModalError = function () {
    $scope.modalError = '';
  };

  $scope.showLossesAndAdjustmentModalForLineItem = function (lineItem) {
    updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem);
    $scope.lossesAndAdjustmentsModal[lineItem.id] = true;
  };


  // TODO: Push this method to rnr-line-item
  $scope.removeLossAndAdjustment = function (lineItem, lossAndAdjustmentToDelete) {
    lineItem.removeLossAndAdjustment(lossAndAdjustmentToDelete, $scope.rnr, $scope.programRnrColumnList);
    updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem);
    $scope.resetModalError();
  };

  // TODO: Push this method to rnr-line-item
  $scope.addLossAndAdjustment = function (lineItem, newLossAndAdjustment) {
    lineItem.addLossAndAdjustment(newLossAndAdjustment, $scope.rnr, $scope.programRnrColumnList);
    updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem);
  };

  // TODO: Push this method to rnr-line-item
  function formulaValid() {
    var valid = true;
    $($scope.rnrLineItems).each(function (index, lineItem) {
      if (lineItem.arithmeticallyInvalid($scope.programRnrColumnList) || lineItem.stockInHand < 0 || lineItem.quantityDispensed < 0) {
        valid = false;
        return false;
      }
    });
    return valid;
  }

  // TODO: Push this method to rnr-line-item
  function isValidLossesAndAdjustments(rnrLineItem) {
    $scope.modalError = '';
    if (isUndefined(rnrLineItem.lossesAndAdjustments)) return true;

    for (var index in rnrLineItem.lossesAndAdjustments) {
      if (isUndefined(rnrLineItem.lossesAndAdjustments[index].quantity)) {
        $scope.modalError = 'Please correct the highlighted fields before submitting';
        return false;
      }
    }
    return true;
  }

  $scope.getCellErrorClass = function (rnrLineItem) {
    return rnrLineItem.getErrorMessage($scope.programRnrColumnList) ? 'cell-error-highlight' : '';
  };

  $scope.getRowErrorClass = function (rnrLineItem) {
    return $scope.getCellErrorClass(rnrLineItem) ? 'row-error-highlight' : '';
  };

  $scope.labelForRnrColumn = function (columnName) {
    if ($scope.programRnrColumnList) return _.findWhere($scope.programRnrColumnList, {'name':columnName}).label + ":";
  };

  $scope.addNonFullSupplyLineItem = function () {
    prepareNFSLineItemFields();
    var lineItem = new RnrLineItem($scope.newNonFullSupply);

    $scope.nonFullSupplyLineItems.push(lineItem);
    $scope.rnr.nonFullSupplyLineItems = $scope.nonFullSupplyLineItems;
    lineItem.fillPacksToShipBasedOnCalculatedOrderQuantityOrQuantityRequested($scope.rnr);
    $scope.facilityApprovedProduct = undefined;
    $scope.newNonFullSupply = undefined;
    updateNonFullSupplyProductsToDisplay();
  };

  $scope.showAddNonFullSupplyModal = function () {
    updateNonFullSupplyProductsToDisplay();
    $scope.nonFullSupplyProductsModal = true;
  };

  function updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem) {
    var lossesAndAdjustmentTypesForLineItem = _.pluck(_.pluck(lineItem.lossesAndAdjustments, 'type'), 'name');

    $scope.lossesAndAdjustmentTypesToDisplay = $.grep($scope.allTypes, function (lAndATypeObject) {
      return $.inArray(lAndATypeObject.name, lossesAndAdjustmentTypesForLineItem) == -1;
    });
  }

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
    var usedNonFullSupplyProducts = _.pluck($scope.nonFullSupplyLineItems, 'productCode');
    $scope.nonFullSupplyProductsToDisplay = $.grep($scope.nonFullSupplyProducts, function (facilityApprovedProduct) {
      return $.inArray(facilityApprovedProduct.programProduct.product.code, usedNonFullSupplyProducts) == -1;
    });
  }
}
