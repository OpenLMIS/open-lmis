function RequisitionFullSupplyController($scope, $routeParams, $location, LossesAndAdjustmentsReferenceData) {
  $scope.currentRnrLineItem = undefined;

  $scope.showCategory = function (index) {
    return !((index > 0 ) && ($scope.pageLineItems[index].productCategory == $scope.pageLineItems[index-1].productCategory));
  };

  LossesAndAdjustmentsReferenceData.get({}, function (data) {
    $scope.allTypes = data.lossAdjustmentTypes;
  }, function () {
  });

  $scope.showCategory = function (index) {
    return !((index > 0 ) && ($scope.pageLineItems[index].productCategory == $scope.pageLineItems[index-1].productCategory));
  };

  $scope.getId = function (prefix, parent) {
      return prefix + "_" + parent.$parent.$parent.$index;
    };


  // TODO: Push this method to rnr-line-item
  $scope.saveLossesAndAdjustmentsForRnRLineItem = function () {
    if (!isValidLossesAndAdjustments($scope.currentRnrLineItem)) return;

    $scope.currentRnrLineItem.reEvaluateTotalLossesAndAdjustments();
    $scope.lossesAndAdjustmentsModal = false;
    $scope.currentRnrLineItem = undefined;
  };

  $scope.resetModalError = function () {
    $scope.modalError = '';
  };

  $scope.showLossesAndAdjustmentModalForLineItem = function (lineItem) {
    $scope.currentRnrLineItem = lineItem;
    updateLossesAndAdjustmentTypesToDisplayForLineItem();
    $scope.lossesAndAdjustmentsModal = true;
  };

  // TODO: Push this method to rnr-line-item
  $scope.removeLossAndAdjustment = function (lossAndAdjustmentToDelete) {
    $scope.currentRnrLineItem.removeLossAndAdjustment(lossAndAdjustmentToDelete);
    updateLossesAndAdjustmentTypesToDisplayForLineItem();
    $scope.resetModalError();
  };

  // TODO: Push this method to rnr-line-item
  $scope.addLossAndAdjustment = function (newLossAndAdjustment) {
    $scope.currentRnrLineItem.addLossAndAdjustment(newLossAndAdjustment);
    updateLossesAndAdjustmentTypesToDisplayForLineItem();
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

  function updateLossesAndAdjustmentTypesToDisplayForLineItem() {
    var lossesAndAdjustmentTypesForLineItem = _.pluck(_.pluck($scope.currentRnrLineItem.lossesAndAdjustments, 'type'), 'name');

    $scope.lossesAndAdjustmentTypesToDisplay = $.grep($scope.allTypes, function (lAndATypeObject) {
      return $.inArray(lAndATypeObject.name, lossesAndAdjustmentTypesForLineItem) == -1;
    });
  }
}
