function CreateRnrController($scope, ReferenceData, ProgramRnRColumnList, $location, Requisition, Requisitions, $route, LossesAndAdjustmentsReferenceData, $rootScope) {

  $scope.disableFormForSubmittedRnr = function () {
    return $scope.rnr != null && $scope.rnr.status == 'SUBMITTED';

  };

  $scope.lossesAndAdjustmentsModal = [];
  $scope.rnrLineItems = [];
  $rootScope.fixToolBar();
  if (!$scope.$parent.rnr) {
    Requisition.get({facilityId:$route.current.params.facility, programId:$route.current.params.program},
      function (data) {
        if (data.rnr) {
          $scope.rnr = data.rnr;
          populateRnrLineItems($scope.rnr);
        } else {
          $scope.$parent.error = "Requisition does not exist. Please initiate.";
          $location.path($scope.$parent.sourceUrl);
        }
      }, {});
  } else {
    populateRnrLineItems($scope.$parent.rnr);
  }

  ReferenceData.get({}, function (data) {
    $scope.currency = data.currency;
  }, {});

  LossesAndAdjustmentsReferenceData.get({}, function (data) {
    $scope.allTypes = data.lossAdjustmentTypes;
  }, {});
  ProgramRnRColumnList.get({programId:$route.current.params.program}, function (data) {
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

    if (validate(data)) {
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

  var validate = function (data) {
    return (data.rnrColumnList.length > 0);
  };

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
      $scope.rnr, function () {
        $scope.message = "R&R saved successfully!";
        $scope.error = "";
      }, {});
  };

  function isUndefined(value) {
    return (value == null || value == undefined || value.trim().length == 0 || value == false);
  }

  $scope.highlightRequired = function (value) {
    if (isUndefined(value) && $scope.inputClass == 'required') {
      return "required-error";
    }
  };

  $scope.highlightWarning = function (value) {
    if (isUndefined(value) && $scope.inputClass == 'required') {
      return "warning-error";
    }
  };
  function formulaValid() {
    var valid = true;
    $scope.rnrLineItems.forEach(function (lineItem) {
      if (lineItem.arithmeticallyInvalid($scope.programRnRColumnList) || lineItem.rnrLineItem.stockInHand <0 || lineItem.rnrLineItem.quantityDispensed < 0){
        valid =  false;
      }
    });
    return valid;
  }

  $scope.submitRnr = function () {
    if ($scope.saveRnrForm.$error.rnrError) {
      $scope.submitError = "Please correct the errors on the R&R form before submitting";
      $scope.submitMessage = "";
      return;
    }

    if ($scope.saveRnrForm.$error.required) {
      $scope.saveRnr();
      $scope.inputClass = "required";
      $scope.submitMessage = "";
      $scope.submitError = 'Please complete the highlighted fields on the R&R form before submitting';
      return;
    }
    if (!formulaValid()) {
      $scope.saveRnr();
      $scope.submitError = "Please correct the errors on the R&R form before submitting";
      $scope.submitMessage = "";
      return;
    }

    Requisitions.update({id:$scope.rnr.id, operation:"submit"},
      $scope.rnr, function (data) {
        $scope.rnr.status = "SUBMITTED";
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

  $scope.showCurrencySymbol = function (value) {
    if (value != 0 && isUndefined(value) ){
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
  };

  $scope.addLossAndAdjustment = function (lineItem, newLossAndAdjustment) {
    lineItem.addLossAndAdjustment(newLossAndAdjustment);
    updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem.rnrLineItem);
  };

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

  function populateRnrLineItems(rnr) {
    $(rnr.lineItems).each(function (i, lineItem) {
      var rnrLineItem = new RnrLineItem(lineItem);
      $scope.rnrLineItems.push(rnrLineItem);
    });
  }

}
