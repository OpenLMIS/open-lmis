function RequisitionFullSupplyController($scope, ProgramRnRColumnList, $location, Requisitions, $routeParams, LossesAndAdjustmentsReferenceData, $rootScope) {
  $scope.lossesAndAdjustmentsModal = [];

  LossesAndAdjustmentsReferenceData.get({}, function (data) {
    $scope.allTypes = data.lossAdjustmentTypes;
  }, function () {
  });

  ProgramRnRColumnList.get({programId:$routeParams.program}, function (data) {
    if (data.rnrColumnList && data.rnrColumnList.length > 0) {
      $scope.programRnrColumnList = data.rnrColumnList;
      prepareRnr();
    } else {
      $scope.$parent.error = "Please contact Admin to define R&R template for this program";
      $location.path("/init-rnr");
    }
  }, function () {
    $location.path("/init-rnr");
  });

  function resetCostsIfNull() {
    var rnr = $scope.$parent.rnr;
    if (rnr == null) return;
    if (!rnr.fullSupplyItemsSubmittedCost)
      rnr.fullSupplyItemsSubmittedCost = 0;
  }

  function prepareRnr() {
    var rnr = $scope.$parent.rnr;

    var lineItemsJson = rnr.lineItems;
    rnr.lineItems = [];
    $(lineItemsJson).each(function (i, lineItem) {
      rnr.lineItems.push(new RnrLineItem(lineItem, $scope.$parent.rnr, $scope.programRnrColumnList));
    });

    resetCostsIfNull();
//    $scope.$parent.formDisabled = $scope.$parent.isFormDisabled();
  }

  $scope.getId = function (prefix, parent, isLossAdjustment) {
    if (isLossAdjustment != null && isLossAdjustment != isUndefined && isLossAdjustment) {
      return prefix + "_" + parent.$parent.$parent.$index + "_" + parent.$parent.$parent.$parent.$index;
    }
    return prefix + "_" + parent.$parent.$parent.$index;
  };

  // TODO: Push this method to rnr-line-item
  $scope.saveLossesAndAdjustmentsForRnRLineItem = function (rnrLineItem) {
    if (!isValidLossesAndAdjustments(rnrLineItem)) return;

    rnrLineItem.reEvaluateTotalLossesAndAdjustments();
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
    lineItem.removeLossAndAdjustment(lossAndAdjustmentToDelete);
    updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem);
    $scope.resetModalError();
  };

  // TODO: Push this method to rnr-line-item
  $scope.addLossAndAdjustment = function (lineItem, newLossAndAdjustment) {
    lineItem.addLossAndAdjustment(newLossAndAdjustment);
    updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem);
  };

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

  function updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem) {
    var lossesAndAdjustmentTypesForLineItem = _.pluck(_.pluck(lineItem.lossesAndAdjustments, 'type'), 'name');

    $scope.lossesAndAdjustmentTypesToDisplay = $.grep($scope.allTypes, function (lAndATypeObject) {
      return $.inArray(lAndATypeObject.name, lossesAndAdjustmentTypesForLineItem) == -1;
    });
  }
}
