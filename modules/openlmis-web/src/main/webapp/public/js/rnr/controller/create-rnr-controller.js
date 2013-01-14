function CreateRnrController($scope, ReferenceData, ProgramRnRColumnList, $location, Requisition, Requisitions, $routeParams, LossesAndAdjustmentsReferenceData, $rootScope) {

  $scope.lossesAndAdjustmentsModal = [];
  $scope.rnrLineItems = [];
  $rootScope.fixToolBar();
  if (!$scope.$parent.rnr) {
    Requisition.get({facilityId:$routeParams.facility, programId:$routeParams.program},
      function (data) {
        if (data.rnr) {
          $scope.rnr = data.rnr;
          $scope.formDisabled = isFormDisabled();
          populateRnrLineItems($scope.rnr);
        } else {
          $scope.$parent.error = "Requisition does not exist. Please initiate.";
          $location.path($scope.$parent.sourceUrl);
        }
      }, {});
  } else {
    $scope.formDisabled = isFormDisabled();
    populateRnrLineItems($scope.$parent.rnr);
  }

    ReferenceData.get({}, function (data) {
        $scope.currency = data.currency;
    }, {});

    LossesAndAdjustmentsReferenceData.get({}, function (data) {
        $scope.allTypes = data.lossAdjustmentTypes;
    }, {});

    ProgramRnRColumnList.get({programId: $routeParams.program}, function (data) {
        function resetFullSupplyItemsCostIfNull(rnr) {
            if (rnr == null) return;
            if (rnr.fullSupplyItemsSubmittedCost == null)
                rnr.fullSupplyItemsSubmittedCost = 0;
        }

        function resetTotalSubmittedCostIfNull(rnr) {
            if (rnr == null) return;
            if (rnr.totalSubmittedCost == null)
                rnr.totalSubmittedCost = 0;
        }

        if (data.rnrColumnList.length > 0) {
            $scope.programRnRColumnList = data.rnrColumnList;
            resetFullSupplyItemsCostIfNull($scope.$parent.rnr);
            resetTotalSubmittedCostIfNull($scope.$parent.rnr);
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

        Requisitions.update({id: $scope.rnr.id, operation: "save"},
            $scope.rnr, function () {
                $scope.message = "R&R saved successfully!";
                $scope.error = "";
            }, {});
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

    $scope.highlightWarning = function (value, index) {
        if ((isUndefined(value) || value.trim().length == 0 || value == false) && $scope.inputClass == 'required' && $scope.rnrLineItems[index].rnrLineItem.quantityRequested) {
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

  function isFormDisabled() {
    if ($scope.rnr || $scope.$parent.rnr) {
      if ($scope.rnr.status == 'AUTHORIZED') return true;
      if ($scope.rnr.status == 'SUBMITTED' && !$rootScope.hasPermission('AUTHORIZE_REQUISITION')) return true;
    }
    return false;
  }

  $scope.submitRnr = function () {
    if (!valid()) return;

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
        $scope.lossesAndAdjustmentsModal[rnrLineItem.rnrLineItem.id] = false;
    };

    $scope.resetModalError = function () {
        $scope.modalError = '';
    };

    $scope.showCurrencySymbol = function (value) {
        if (value != 0 && (isUndefined(value) || value.length == 0 || value == false)) {
            return "";
        }
        return "defined";
    };

    $scope.showSelectedColumn = function (columnName) {
        if (($scope.rnr.status == "INITIATED" || $scope.rnr.status == "SUBMITTED") && columnName == "quantityApproved")
            return undefined;
        return "defined";
    };

    $scope.showLossesAndAdjustmentModalForLineItem = function (lineItem) {
        updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem);
        $scope.lossesAndAdjustmentsModal[lineItem.id] = true;
    };

    $scope.removeLossAndAdjustment = function (lineItem, lossAndAdjustmentToDelete) {
        lineItem.removeLossAndAdjustment(lossAndAdjustmentToDelete);
        updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem.rnrLineItem);
        $scope.resetModalError();
    };

    $scope.addLossAndAdjustment = function (lineItem, newLossAndAdjustment) {
        lineItem.addLossAndAdjustment(newLossAndAdjustment);
        updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem.rnrLineItem);
    };

    function isUndefined(value) {
        return (value == null || value == undefined);
    }

    function formulaValid() {
        var valid = true;
        $scope.rnrLineItems.forEach(function (lineItem) {
            if (lineItem.arithmeticallyInvalid($scope.programRnRColumnList) || lineItem.rnrLineItem.stockInHand < 0 || lineItem.rnrLineItem.quantityDispensed < 0) {
                valid = false;
            }
        });
        return valid;
    }

    function isValidLossesAndAdjustments(rnrLineItem) {
        if (!isUndefined(rnrLineItem.rnrLineItem.lossesAndAdjustments)) {
            for (var index in rnrLineItem.rnrLineItem.lossesAndAdjustments) {
                if (isUndefined(rnrLineItem.rnrLineItem.lossesAndAdjustments[index].quantity)) {
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

    function populateRnrLineItems(rnr) {
        $(rnr.lineItems).each(function (i, lineItem) {
            var rnrLineItem = new RnrLineItem(lineItem);
            $scope.rnrLineItems.push(rnrLineItem);
        });
    }
}
