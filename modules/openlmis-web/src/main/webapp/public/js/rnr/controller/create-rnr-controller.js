function CreateRnrController($scope, ReferenceData, ProgramRnRColumnList, $location, FacilityApprovedProducts, Requisitions, $routeParams, LossesAndAdjustmentsReferenceData, $rootScope) {

  $scope.lossesAndAdjustmentsModal = [];
  $rootScope.fixToolBar();

  FacilityApprovedProducts.get({facilityId:$routeParams.facility, programId:$routeParams.program},
    function (data) {
      $scope.nonFullSupplyProducts = data.nonFullSupplyProducts;
    }, function (data) {
    });

  $scope.totalCost = function() {
    if(!$scope.rnr) return;
    return parseFloat(parseFloat($scope.rnr.fullSupplyItemsSubmittedCost) + parseFloat($scope.rnr.nonFullSupplyItemsSubmittedCost)).toFixed(2);
  };

  ReferenceData.get({}, function (data) {
    $scope.currency = data.currency;
  }, function() {});

  LossesAndAdjustmentsReferenceData.get({}, function (data) {
    $scope.allTypes = data.lossAdjustmentTypes;
  }, function() {});

  ProgramRnRColumnList.get({programId:$routeParams.program}, function (data) {
    function resetCostsIfNull(rnr) {
      if (rnr == null) return;
      if (rnr.fullSupplyItemsSubmittedCost == null)
        rnr.fullSupplyItemsSubmittedCost = 0;
      if (rnr.nonFullSupplyItemsSubmittedCost == null)
        rnr.nonFullSupplyItemsSubmittedCost = 0;
    }

    if (data.rnrColumnList && data.rnrColumnList.length > 0) {
      $scope.programRnRColumnList = data.rnrColumnList;
      resetCostsIfNull($scope.$parent.rnr);
    } else {
      $scope.$parent.error = "Please contact Admin to define R&R template for this program";
      $location.path($scope.$parent.sourceUrl);
    }
  }, function () {
    $location.path($scope.$parent.sourceUrl);
  });

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

  $scope.saveLossesAndAdjustmentsForRnRLineItem = function (rnrLineItem, rnr, programRnrColumnList) {
    if (!isValidLossesAndAdjustments(rnrLineItem)) return;

    rnrLineItem.fill(rnr, programRnrColumnList);
    $scope.lossesAndAdjustmentsModal[rnrLineItem.id] = false;
  };

  $scope.resetModalError = function () {
    $scope.modalError = '';
  };

  $scope.showLossesAndAdjustmentModalForLineItem = function (lineItem) {
    updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem);
    $scope.lossesAndAdjustmentsModal[lineItem.id] = true;
  };


  $scope.removeLossAndAdjustment = function (lineItem, lossAndAdjustmentToDelete) {
    lineItem.removeLossAndAdjustment(lossAndAdjustmentToDelete);
    updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem);
    $scope.resetModalError();
  };

  $scope.addLossAndAdjustment = function (lineItem, newLossAndAdjustment) {
    lineItem.addLossAndAdjustment(newLossAndAdjustment);
    updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem);
  };

  function isUndefined(value) {
    return (value == null || value == undefined);
  }

  function formulaValid() {
    var valid = true;
    $scope.rnrLineItems.forEach(function (lineItem) {
      if (lineItem.arithmeticallyInvalid($scope.programRnRColumnList) || lineItem.stockInHand < 0 || lineItem.quantityDispensed < 0) {
        valid = false;
      }
    });
    return valid;
  }

  function isValidLossesAndAdjustments(rnrLineItem) {
    if (!isUndefined(rnrLineItem.lossesAndAdjustments)) {
      for (var index in rnrLineItem.lossesAndAdjustments) {
        if (isUndefined(rnrLineItem.lossesAndAdjustments[index].quantity)) {
          $scope.modalError = 'Please correct the highlighted fields before submitting';
          return false;
        }
      }
    }
    $scope.modalError = '';
    return true;
  }

  function updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem) {
    var lossesAndAdjustmentTypesForLineItem = [];
    $(lineItem.lossesAndAdjustments).each(function (index, lineItemLossAndAdjustment) {
      lossesAndAdjustmentTypesForLineItem.push(lineItemLossAndAdjustment.type.name);
    });

    var allTypes = $scope.allTypes;
    $scope.lossesAndAdjustmentTypesToDisplay = $.grep(allTypes, function (lAndATypeObject) {
      return $.inArray(lAndATypeObject.name, lossesAndAdjustmentTypesForLineItem) == -1;
    });
  }

  $scope.getCellErrorClass = function (rnrLineItem, programRnRColumnList) {
    return rnrLineItem.getErrorMessage(programRnRColumnList) ? 'cell-error-highlight' : '';
  };

  $scope.getRowErrorClass = function (rnrLineItem, programRnRColumnList) {
    return $scope.getCellErrorClass(rnrLineItem, programRnRColumnList) ? 'row-error-highlight' : '';
  };


  $scope.labelForRnrColumn = function (columnName) {
    var label = "";
    $($scope.programRnRColumnList).each(function (index, column) {
      if (column.name == columnName) {
        label = column.label;
        return false;
      }
    });
    return label + ":";
  };


  function populateProductInformation() {
    var product = {};
    angular.copy($scope.facilityApprovedProduct.programProduct.product, product);
    $scope.newNonFullSupply.productCode = product.code;
    $scope.newNonFullSupply.product = (product.primaryName == null ? "" : (product.primaryName + " ")) +
      (product.form.code == null ? "" : (product.form.code + " ")) +
      (product.strength == null ? "" : (product.strength + " ")) +
      (product.dosageUnit.code == null ? "" : product.dosageUnit.code);
    $scope.newNonFullSupply.dosesPerDispensingUnit = product.dosesPerDispensingUnit;
    $scope.newNonFullSupply.packSize = product.packSize;
    $scope.newNonFullSupply.roundToZero = product.roundToZero;
    $scope.newNonFullSupply.packRoundingThreshold = product.packRoundingThreshold;
    $scope.newNonFullSupply.dispensingUnit = product.dispensingUnit;
    $scope.newNonFullSupply.fullSupply = product.fullSupply;
    $scope.newNonFullSupply.maxMonthsOfStock = $scope.facilityApprovedProduct.maxMonthsOfStock;
    $scope.newNonFullSupply.dosesPerMonth = $scope.facilityApprovedProduct.programProduct.dosesPerMonth;
    $scope.newNonFullSupply.price = $scope.facilityApprovedProduct.programProduct.currentPrice;
  }

  function prepareNFSLineItemFields() {
    $(['quantityReceived', 'quantityDispensed', 'beginningBalance', 'stockInHand', 'totalLossesAndAdjustments', 'calculatedOrderQuantity', 'newPatientCount',
      'stockOutDays', 'normalizedConsumption', 'amc', 'maxStockQuantity']).each(function (index, field) {
        $scope.newNonFullSupply[field] = 0;
      });
    $scope.newNonFullSupply.rnrId = $scope.rnr.id;
  }

  function updateNonFullSupplyProductsToDisplay() {
    var usedNonFullSupplyProducts = [];
    $($scope.nonFullSupplyLineItems).each(function (index, nonFullSupplyLineItem) {
      usedNonFullSupplyProducts.push(nonFullSupplyLineItem.productCode);
    });

    var allNonFullSupplyProducts = $scope.nonFullSupplyProducts;
    $scope.nonFullSupplyProductsToDisplay = $.grep(allNonFullSupplyProducts, function (facilityApprovedProduct) {
      return $.inArray(facilityApprovedProduct.programProduct.product.code, usedNonFullSupplyProducts) == -1;
    });
  }

  $scope.addNonFullSupplyLineItem = function () {
    populateProductInformation();
    jQuery.extend(true, $scope.newNonFullSupply, new RnrLineItem());
    prepareNFSLineItemFields();
    $scope.nonFullSupplyLineItems.push($scope.newNonFullSupply);
    $scope.rnr.nonFullSupplyLineItems = $scope.nonFullSupplyLineItems;
    $scope.newNonFullSupply.fill($scope.rnr, $scope.programRnRColumnList);
    $scope.facilityApprovedProduct = undefined;
    $scope.newNonFullSupply = undefined;
    updateNonFullSupplyProductsToDisplay();
  };

  $scope.showAddNonFullSupplyModal = function () {
    updateNonFullSupplyProductsToDisplay();
    $scope.nonFullSupplyProductsModal = true;
  };
}
