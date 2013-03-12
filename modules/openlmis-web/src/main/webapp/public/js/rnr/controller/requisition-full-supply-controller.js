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

  $scope.saveLossesAndAdjustmentsForRnRLineItem = function () {
    $scope.modalError = '';

    if(!$scope.currentRnrLineItem.validateLossesAndAdjustments()){
      $scope.modalError = 'Please correct the highlighted fields before submitting';
      return;
    }

    $scope.currentRnrLineItem.reEvaluateTotalLossesAndAdjustments();
    $scope.clearAndCloseLossesAndAdjustmentModal();
  };

  $scope.clearAndCloseLossesAndAdjustmentModal = function () {
    $scope.lossAndAdjustment = undefined;
    $scope.lossesAndAdjustmentsModal = false;
  }

  $scope.resetModalError = function () {
    $scope.modalError = '';
  };

  $scope.showLossesAndAdjustmentModalForLineItem = function (lineItem) {
    $scope.currentRnrLineItem = lineItem;
    updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem);
    $scope.lossesAndAdjustmentsModal = true;
  };

  $scope.removeLossAndAdjustment = function (lossAndAdjustmentToDelete) {
    $scope.currentRnrLineItem.removeLossAndAdjustment(lossAndAdjustmentToDelete);
    updateLossesAndAdjustmentTypesToDisplayForLineItem($scope.currentRnrLineItem);
    $scope.resetModalError();
    $scope.saveRnrForm.$dirty = true;
  };

  $scope.addLossAndAdjustment = function (newLossAndAdjustment) {
    $scope.currentRnrLineItem.addLossAndAdjustment(newLossAndAdjustment);
    updateLossesAndAdjustmentTypesToDisplayForLineItem($scope.currentRnrLineItem);
    $scope.saveRnrForm.$dirty = true;
  };

  function updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem) {
    var lossesAndAdjustmentTypesForLineItem = _.pluck(_.pluck(lineItem.lossesAndAdjustments, 'type'), 'name');

    $scope.lossesAndAdjustmentTypesToDisplay = $.grep($scope.allTypes, function (lAndATypeObject) {
      return $.inArray(lAndATypeObject.name, lossesAndAdjustmentTypesForLineItem) == -1;
    });
  }
}
