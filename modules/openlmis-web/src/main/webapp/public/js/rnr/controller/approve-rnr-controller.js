function ApproveRnrController($scope, requisition, Requisitions, programRnrColumnList, $location, LossesAndAdjustmentsReferenceData, ReferenceData) {
  var visibleColumns = _.where(programRnrColumnList, {'visible' : true});
  $scope.error = "";
  $scope.message = "";

  $scope.lossesAndAdjustmentsModal = [];
  LossesAndAdjustmentsReferenceData.get({}, function (data) {
    $scope.allTypes = data.lossAdjustmentTypes;
  }, {});

  ReferenceData.get({}, function (data) {
    $scope.currency = data.currency;
  }, {});

  var columnDefinitions = [];
  var nonFullColumnDefinitions = [];
  $scope.rnr = requisition;
  populateRnrLineItems(visibleColumns);
  if (visibleColumns.length > 0) {
    $(visibleColumns).each(function (i, column) {
      if (column.name == "cost" || column.name == "price") {
        columnDefinitions.push({field:column.name, displayName:column.label, cellTemplate:currencyTemplate('row.entity.' + column.name)});
        nonFullColumnDefinitions.push({field:column.name, displayName:column.label, cellTemplate:currencyTemplate('row.entity.' + column.name)});
        return;
      }
      if (column.name == "lossesAndAdjustments") {
        columnDefinitions.push({field:column.name, displayName:column.label, cellTemplate:lossesAndAdjustmentsTemplate()});
        nonFullColumnDefinitions.push({field:'totalLossesAndAdjustments', displayName:column.label });
        return;
      }
      if (column.name == "quantityApproved") {
        columnDefinitions.push({field:column.name, displayName:column.label, width:140, cellTemplate:positiveIntegerCellTemplate(column.name, 'row.entity.quantityApproved')});
        nonFullColumnDefinitions.push({field:column.name, displayName:column.label, width:140, cellTemplate:positiveIntegerCellTemplate(column.name, 'row.entity.quantityApproved')});
        return;
      }
      if (column.name == "remarks") {
        columnDefinitions.push({field:column.name, displayName:column.label, cellTemplate:freeTextCellTemplate(column.name, 'row.entity.remarks')});
        nonFullColumnDefinitions.push({field:column.name, displayName:column.label, cellTemplate:freeTextCellTemplate(column.name, 'row.entity.remarks')});
        return;
      }

      columnDefinitions.push({field:column.name, displayName:column.label});
      nonFullColumnDefinitions.push({field:column.name, displayName:column.label});
    });
  } else {
    $scope.$parent.error = "Please contact Admin to define R&R template for this program";
  }

  function lossesAndAdjustmentsTemplate() {
    return '/public/pages/logistics/rnr/partials/lossesAndAdjustments.html';
  }

  function currencyTemplate(value) {
    return '<span  class = "cell-text" ng-show = "showCurrencySymbol(' + value + ')"  ng-bind="currency"></span >&nbsp; &nbsp;<span ng-bind = "' + value + '" class = "cell-text" ></span >'
  }


  function freeTextCellTemplate(field, value) {
    return '<div><input maxlength="250" name="' + field + '" ng-model="' + value + '"/></div>';
  }

  function positiveIntegerCellTemplate(field, value) {
    return '<div><ng-form name="positiveIntegerForm"  > <input ui-event="{blur : \'row.entity.updateCostWithApprovedQuantity()\'}" ng-class="{\'required-error\': approvedQuantityRequiredFlag && positiveIntegerForm.' + field + '.$error.required}" ' +
      '  ng-required="true" maxlength="8"  name=' + field + ' ng-model=' + value + '  ng-change="validatePositiveInteger(positiveIntegerForm.' + field + '.$error,' + value + ')" />' +
      '<span class="rnr-form-error" id=' + field + ' ng-show="positiveIntegerForm.' + field + '.$error.pattern" ng-class="{\'required-error\': approvedQuantityInvalidFlag && positiveIntegerForm.' + field + '.$error.positiveInteger}">Please Enter Numeric value</span></ng-form></div>';
  }

  $scope.validatePositiveInteger = function (error, value) {
    if (value == undefined) {
      error.positiveInteger = false;
      return
    }
    error.pattern = !isPositiveNumber(value);
  };


  $scope.totalCost = function () {
    if (!$scope.rnr) return;
    return parseFloat(parseFloat($scope.rnr.fullSupplyItemsSubmittedCost) + parseFloat($scope.rnr.nonFullSupplyItemsSubmittedCost)).toFixed(2);
  };

  $scope.fullSupplyGrid = { data:'rnr.lineItems',
    canSelectRows:false,
    displayFooter:false,
    displaySelectionCheckbox:false,
    showColumnMenu:false,
    showFilter:false,
    rowHeight:44,
    enableSorting:false,
    columnDefs:columnDefinitions
  };

  $scope.nonFullSupplyGrid = { data:'rnr.nonFullSupplyLineItems',
    canSelectRows:false,
    displayFooter:false,
    displaySelectionCheckbox:false,
    showColumnMenu:false,
    showFilter:false,
    rowHeight:44,
    enableSorting:false,
    columnDefs:nonFullColumnDefinitions
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

  $scope.saveRnr = function () {
    $scope.approvedQuantityInvalidFlag = false;
    $($scope.lineItems).each(function (i, lineItem) {
      if (lineItem.quantityApproved != undefined && !isPositiveNumber(lineItem.quantityApproved)) {
        $scope.approvedQuantityInvalidFlag = true;
        return false;
      }
    });
    if ($scope.approvedQuantityInvalidFlag) {
      $scope.error = "Please correct errors before saving.";
      $scope.message = "";
      return;
    }
    var rnr = removeExtraDataForPostFromRnr();
    Requisitions.update({id:$scope.rnr.id, operation:"save"},
      rnr, function (data) {
        $scope.message = data.success;
        $scope.error = "";
      }, function (data) {
        $scope.error = data.error;
        $scope.message = "";
      });
  };

  $scope.approveRnr = function () {
    $scope.approvedQuantityRequiredFlag = false;
    $($scope.rnr.lineItems).each(function (i, lineItem) {
      if (lineItem.quantityApproved == undefined || !isPositiveNumber(lineItem.quantityApproved)) {
        $scope.approvedQuantityRequiredFlag = true;
        return false;
      }
    });
    $($scope.rnr.nonFullSupplyLineItems).each(function (i, lineItem) {
      if (lineItem.quantityApproved == undefined || !isPositiveNumber(lineItem.quantityApproved)) {
        $scope.approvedQuantityRequiredFlag = true;
        return false;
      }
    });
    if ($scope.approvedQuantityRequiredFlag) {
      $scope.error = "Please complete the highlighted fields on the R&R form before approving";
      $scope.message = "";
      return;
    }
    var rnr = removeExtraDataForPostFromRnr();
    Requisitions.update({id:$scope.rnr.id, operation:"approve"},
        rnr, function (data) {
        $scope.$parent.message = data.success;
        $scope.error = "";
        $location.path("rnr-for-approval");
      }, function (data) {
        $scope.error = data.error;
        $scope.message = "";
      });
  }

  $scope.closeLossesAndAdjustmentsForRnRLineItem = function (rnrLineItem) {
    $scope.lossesAndAdjustmentsModal[rnrLineItem.id] = false;
  };

  $scope.showLossesAndAdjustmentModalForLineItem = function (lineItem) {
    updateLossesAndAdjustmentTypesToDisplayForLineItem(lineItem);
    $scope.lossesAndAdjustmentsModal[lineItem.id] = true;
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

  $scope.showCurrencySymbol = function (value) {
    if (value != 0 && (isUndefined(value) || value.length == 0 || value == false)) {
      return "";
    }
    return "defined";
  };

  function isPositiveNumber(value) {
    var INTEGER_REGEXP = /^\d*$/;
    return INTEGER_REGEXP.test(value);

  }

  function populateRnrLineItems(programRnrColumnList) {
    var lineItemsJson = $scope.rnr.lineItems;
    $scope.rnr.lineItems = [];
    $(lineItemsJson).each(function (i, lineItem) {
      var rnrLineItem = new RnrLineItem(lineItem, $scope.rnr, programRnrColumnList);

      rnrLineItem.updateCostWithApprovedQuantity();
      $scope.rnr.lineItems.push(rnrLineItem);
    });

    var nonFullSupplyLineItemsJson = $scope.rnr.nonFullSupplyLineItems;
    $scope.rnr.nonFullSupplyLineItems = [];
    $(nonFullSupplyLineItemsJson).each(function (i, lineItem) {
      var rnrLineItem = new RnrLineItem(lineItem, $scope.rnr, programRnrColumnList);

      rnrLineItem.updateCostWithApprovedQuantity();
      $scope.rnr.nonFullSupplyLineItems.push(rnrLineItem);
    });
  }

  function isUndefined(value) {
    return (value == null || value == undefined);
  }

  $scope.periodDisplayName = function () {
    if (!$scope.rnr) return;

    var startDate = new Date($scope.rnr.period.startDate);
    var endDate = new Date($scope.rnr.period.endDate);

    return utils.getFormattedDate(startDate) + ' - ' + utils.getFormattedDate(endDate);
  };

}

ApproveRnrController.resolve = {
  requisition:function ($q, $timeout, RequisitionForApprovalById, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      RequisitionForApprovalById.get({id:$route.current.params.rnr},
        function (data) {
          deferred.resolve(data.rnr);
        }, function () {
        });
    }, 100);
    return deferred.promise;
  },
  programRnrColumnList:function ($q, $timeout, ProgramRnRColumnList, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      ProgramRnRColumnList.get({programId:$route.current.params.program}, function (data) {
        deferred.resolve(data.rnrColumnList);
      }, {});
    }, 100);
    return deferred.promise;
  }
};