function CreateRnrController($scope, ReferenceData, ProgramRnRColumnList, $location, Requisition, Requisitions, $route, LossesAndAdjustmentsReferenceData, $rootScope) {

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

  // TODO : is this required?
  var validate = function (data) {
    return (data.rnrColumnList.length > 0);
  };

  $scope.saveRnr = function () {
    if ($scope.saveRnrForm.$error.rnrError) {
      $scope.error = "Please correct errors before saving.";
      $scope.message = "";
      return;
    }

    Requisitions.update({id: $scope.rnr.id, operation: "save"},
      $scope.rnr, function (data) {
        $scope.message = "R&R saved successfully!";
        $scope.error = "";
      }, {});
  };


  $scope.getId = function (prefix, parent, isLossAdjustment) {
    if (isLossAdjustment != null && isLossAdjustment != undefined && isLossAdjustment) {
      return prefix + "_" + parent.$parent.$parent.$index + "_" + parent.$parent.$parent.$parent.$index;
    }
    return prefix + "_" + parent.$parent.$parent.$index;
  };

  $scope.hide = function () {
    return "";
  };

  $scope.showCurrencySymbol = function (value) {
    if (value != 0 && (value == undefined || value == null || value == false)) {
      return "";
    }
    return "defined";
  };

  $scope.showSelectedColumn = function (columnName) {
    if (($scope.rnr.status == "INITIATED" || $scope.rnr.status == "CREATED") && columnName == "quantityApproved")
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

