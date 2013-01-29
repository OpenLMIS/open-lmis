function ApproveRnrController($scope, requisition, Requisitions, programRnRColumnList, $location, LossesAndAdjustmentsReferenceData, ReferenceData) {
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
  $scope.requisition = requisition;
  $scope.lineItems = [];
  populateRnrLineItems(requisition);
  if (programRnRColumnList.length > 0) {
    $scope.programRnRColumnList = programRnRColumnList;
    $($scope.programRnRColumnList).each(function (i, column) {
      if (column.name == "cost" || column.name == "price") {
        columnDefinitions.push({field:column.name, displayName:column.label, cellTemplate:currencyTemplate('row.entity.'+column.name)})
        return;
      }
      if (column.name == "lossesAndAdjustments") {
        columnDefinitions.push({field:column.name, displayName:column.label, cellTemplate:lossesAndAdjustmentsTemplate()})
        return;
      }
      if (column.name == "quantityApproved") {
        columnDefinitions.push({field:column.name, displayName:column.label, width:140, cellTemplate:positiveIntegerCellTemplate(column.name, 'row.entity.quantityApproved')})
        return;
      }
      if (column.name == "remarks") {
        columnDefinitions.push({field:column.name, displayName:column.label, cellTemplate:freeTextCellTemplate(column.name, 'row.entity.remarks')})
        return;
      }

      columnDefinitions.push({field:column.name, displayName:column.label});
    });
  } else {
    $scope.$parent.error = "Please contact Admin to define R&R template for this program";
  }

  function lossesAndAdjustmentsTemplate() {
    return '/public/pages/logistics/rnr/partials/lossesAndAdjustments.html';
  }

  function currencyTemplate(value) {
    return '<span  class = "cell-text" ng-show = "showCurrencySymbol('+value+')"  ng-bind="currency"></span >&nbsp; &nbsp;<span ng-bind = "'+value+'" class = "cell-text" ></span >'
  }


  function freeTextCellTemplate(field, value) {
    return '<div><input maxlength="250" name="' + field + '" ng-model="' + value + '"/></div>';
  }

  function positiveIntegerCellTemplate(field, value) {
    return '<div><ng-form name="positiveIntegerForm"  > <input ui-event="{blur : \'row.entity.updateCostWithApprovedQuantity(requisition, row.entity)\'}" ng-class="{\'required-error\': approvedQuantityRequiredFlag && positiveIntegerForm.' + field + '.$error.required}" ' +
      '  ng-required="true" maxlength="8"  name=' + field + ' ng-model=' + value + '  ng-change="validatePositiveInteger(positiveIntegerForm.' + field + '.$error,' + value + ')" />' +
      '<span class="rnr-form-error" id=' + field + ' ng-show="positiveIntegerForm.' + field + '.$error.pattern" ng-class="{\'required-error\': approvedQuantityInvalidFlag && positiveIntegerForm.' + field + '.$error.positiveInteger}">Please Enter Numeric value</span></ng-form></div>';
  }

  $scope.validatePositiveInteger = function (error, value) {
    if (value == undefined) {
      error.positiveInteger = false;
      return
    }
    ;

    error.pattern = !isPositiveNumber(value);
  }

  $scope.gridOptions = { data:'lineItems',
    canSelectRows:false,
    displayFooter:false,
    displaySelectionCheckbox:false,
    showColumnMenu:false,
    showFilter:false,
    rowHeight:44,
    enableSorting: false,
    columnDefs:columnDefinitions
  };

  $scope.saveRnr = function () {
    $scope.approvedQuantityInvalidFlag = false;
    $($scope.lineItems).each(function (i, lineItem) {
      if (lineItem.quantityApproved != undefined && !isPositiveNumber(lineItem.quantityApproved)) {
        $scope.approvedQuantityInvalidFlag = true;
        return false;
      }
      ;
    })
    if ($scope.approvedQuantityInvalidFlag) {
      $scope.error = "Please correct errors before saving.";
      $scope.message = "";
      return;
    }
    Requisitions.update({id:$scope.requisition.id, operation:"save"},
      $scope.requisition, function (data) {
        $scope.message = data.success;
        $scope.error = "";
      }, function (data) {
        $scope.error = data.error;
        $scope.message = "";
      });
  }

  $scope.approveRnr = function () {
    $scope.approvedQuantityRequiredFlag = false;
    $($scope.lineItems).each(function (i, lineItem) {
      if (lineItem.quantityApproved == undefined || lineItem.quantityApproved == "" || !isPositiveNumber(lineItem.quantityApproved)) {
        $scope.approvedQuantityRequiredFlag = true;
        return false;
      }
      ;
    })
    if ($scope.approvedQuantityRequiredFlag) {
      $scope.error = "Please complete the highlighted fields on the R&R form before approving";
      $scope.message = "";
      return;
    }
    Requisitions.update({id:$scope.requisition.id, operation:"approve"},
      $scope.requisition, function (data) {
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

  function populateRnrLineItems(rnr) {
    $(rnr.lineItems).each(function (i, lineItem) {
      var rnrLineItem = new RnrLineItem();
      jQuery.extend(true, lineItem, rnrLineItem);

      lineItem.fillQuantityApproved(rnr.status);
      lineItem.updateCostWithApprovedQuantity(requisition);
      $scope.lineItems.push(lineItem);
    });
  }

  function isUndefined(value) {
    return (value == null || value == undefined);
  }


}

ApproveRnrController.resolve = {
  requisition:function ($q, $timeout, RequisitionForApprovalById, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      RequisitionForApprovalById.get({id:$route.current.params.rnr},
        function (data) {
          deferred.resolve(data.rnr);
        }, function () {
          console.log("error")
        });
    }, 100);
    return deferred.promise;
  },
  programRnRColumnList:function ($q, $timeout, ProgramRnRColumnList, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      ProgramRnRColumnList.get({programId:$route.current.params.program}, function (data) {
        deferred.resolve(data.rnrColumnList);
      }, {});
    }, 100);
    return deferred.promise;
  }
};