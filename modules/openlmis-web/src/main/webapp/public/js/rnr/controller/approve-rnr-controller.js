function ApproveRnrController($scope, RequisitionForApprovalById, Requisitions, ProgramRnRColumnList, $location, ReferenceData, $routeParams, LossesAndAdjustmentsReferenceData) {
  $scope.error = "";
  $scope.message = "";

  $scope.fullSupplyLink = "#/rnr-for-approval/" + $routeParams.rnr + '/' + $routeParams.facility + '/' + $routeParams.program + '/' + 'full-supply';

  $scope.nonFullSupplyLink = "#/rnr-for-approval/" + $routeParams.rnr + '/' + $routeParams.facility + '/' + $routeParams.program + '/' + 'non-full-supply';

  $scope.showNonFullSupply = ($routeParams.supplyType == 'non-full-supply');

  $scope.lossesAndAdjustmentsModal = [];

  $scope.gridLineItems = [];
  $scope.columnDefinitions = [];

  if ($scope.rnr == undefined || $scope.rnr.id != $routeParams.rnr) {
    RequisitionForApprovalById.get({id:$routeParams.rnr}, function (data) {
      $scope.$parent.rnr = data.rnr;
      populateRnrLineItems();
      fillGridData();
      getTemplate();
    });
  } else {
    populateRnrLineItems();
    fillGridData();
    getTemplate();
  }

  ReferenceData.get({}, function (data) {
    $scope.currency = data.currency;
  }, {});


  $scope.closeLossesAndAdjustmentsForRnRLineItem = function (rnrLineItem) {
    $scope.lossesAndAdjustmentsModal[rnrLineItem.id] = false;
  };

  function getTemplate() {
    if (!$scope.rnrColumns) {
      $scope.$parent.rnrColumns = [];
      ProgramRnRColumnList.get({programId:$routeParams.program}, function (data) {
        $scope.$parent.rnrColumns = data.rnrColumnList;
        prepareColumnDefinitions();
      });
    } else {
      prepareColumnDefinitions();
    }
  }

  function prepareColumnDefinitions() {
    var columnDefinitions = [];
    var visibleColumns = _.where($scope.rnrColumns, {'visible':true});
    if (visibleColumns.length > 0) {
      $(visibleColumns).each(function (i, column) {
        if (column.name == "cost" || column.name == "price") {
          columnDefinitions.push({field:column.name, displayName:column.label, cellTemplate:currencyTemplate('row.entity.' + column.name)});
          return;
        }
        if (column.name == "lossesAndAdjustments") {
          columnDefinitions.push({field:column.name, displayName:column.label, cellTemplate:lossesAndAdjustmentsTemplate()});
          return;
        }
        if (column.name == "quantityApproved") {
          columnDefinitions.push({field:column.name, displayName:column.label, width:140, cellTemplate:positiveIntegerCellTemplate(column.name, 'row.entity.quantityApproved')});
          return;
        }
        if (column.name == "remarks") {
          columnDefinitions.push({field:column.name, displayName:column.label, cellTemplate:freeTextCellTemplate(column.name, 'row.entity.remarks')});
          return;
        }
        columnDefinitions.push({field:column.name, displayName:column.label});
      });
      $scope.columnDefinitions = columnDefinitions;
    } else {
      $scope.$parent.error = "Please contact Admin to define R&R template for this program";
    }
  }

  function fillGridData() {
    $scope.gridLineItems = $scope.showNonFullSupply ? $scope.rnr.nonFullSupplyLineItems : $scope.rnr.lineItems;
  }

  $scope.showLossesAndAdjustmentModalForLineItem = function (lineItem) {
    $scope.lossesAndAdjustmentsModal[lineItem.id] = true;
  };

  function populateRnrLineItems() {
    var lineItemsJson = $scope.rnr.lineItems;
    $scope.rnr.lineItems = [];

    $(lineItemsJson).each(function (i, lineItem) {
      var rnrLineItem = new RnrLineItem(lineItem, $scope.rnr, $scope.$parent.programRnrColumnList);

      rnrLineItem.updateCostWithApprovedQuantity();
      $scope.rnr.lineItems.push(rnrLineItem);
    });
    var nonFullSupplyLineItemsJson = $scope.rnr.nonFullSupplyLineItems;
    $scope.rnr.nonFullSupplyLineItems = [];
    $(nonFullSupplyLineItemsJson).each(function (i, lineItem) {
      var rnrLineItem = new RnrLineItem(lineItem, $scope.rnr, $scope.$parent.programRnrColumnList);

      rnrLineItem.updateCostWithApprovedQuantity();
      $scope.rnr.nonFullSupplyLineItems.push(rnrLineItem);
    });
  }

  function lossesAndAdjustmentsTemplate() {
    return '<div class="ngCellText" ng-hide="row.entity.fullSupply"><span ng-bind="row.entity.totalLossesAndAdjustments" ></span></div>' +
        '<div id="lossesAndAdjustments" modal="lossesAndAdjustmentsModal[row.entity.id]">' +
        '<div class="modal-header"><h3>Losses And Adjustments</h3></div>' +
        '<div class="modal-body">' +
        '<hr ng-show="row.entity.lossesAndAdjustments.length > 0"/>' +
        '<div class="adjustment-list" ng-show="row.entity.lossesAndAdjustments.length > 0">' +
        '<ul>' +
        '<li ng-repeat="oneLossAndAdjustment in row.entity.lossesAndAdjustments" class="clearfix">' +
        '<span class="tpl-adjustment-type" ng-bind="oneLossAndAdjustment.type.description"></span>' +
        '<span class="tpl-adjustment-qty" ng-bind="oneLossAndAdjustment.quantity"></span>' +
        '</li>' +
        '</ul>' +
        '</div>' +
        '<div class="adjustment-total clearfix alert alert-warning" ng-show="row.entity.lossesAndAdjustments.length > 0">' +
        '<span class="pull-left">Total</span> ' +
        '<span ng-bind="row.entity.totalLossesAndAdjustments"></span>' +
        '</div>' +
        '</div>' +
        '<div class="modal-footer">' +
        '<input type="button" class="btn btn-success save-button" style="width: 75px" ng-click="closeLossesAndAdjustmentsForRnRLineItem(row.entity)" value="Close"/>' +
        '</div>' +
        '</div>' +
        '<a ng-click="showLossesAndAdjustmentModalForLineItem(row.entity)" class="rnr-adjustment" ng-show="row.entity.fullSupply">' +
        '<span class="adjustment-value" ng-bind="row.entity.totalLossesAndAdjustments"></span>' +
        '</a>';
  }

  function currencyTemplate(value) {
    return '<div class="ngCellText"><span  class = "cell-text" ng-show = "showCurrencySymbol(' + value + ')"  ng-bind="currency"></span >&nbsp; &nbsp;<span ng-bind = "' + value + '" class = "cell-text" ></span ></div>'
  }

  function freeTextCellTemplate(field, value) {
    return '<div><input maxlength="250" name="' + field + '" ng-model="' + value + '"/></div>';
  }

  function positiveIntegerCellTemplate(field, value) {
    return '<div><ng-form name="positiveIntegerForm"> <input ui-event="{blur : \'row.entity.updateCostWithApprovedQuantity()\'}" ng-class="{\'required-error\': approvedQuantityRequiredFlag && positiveIntegerForm.' + field + '.$error.required}" ' +
      '  ng-required="true" maxlength="8"  name=' + field + ' ng-model=' + value + ' />' +
      '<span class="rnr-form-error" id=' + field + ' ng-show="validatePositiveInteger(' + value + ')" >Please Enter Numeric value</span></ng-form></div>';
  }

  function isUndefined(value) {
    return (value == null || value == undefined);
  }

    $scope.rnrGrid = {
      data:'gridLineItems',
      canSelectRows:false,
      displayFooter:false,
      displaySelectionCheckbox:false,
      showColumnMenu:false,
      showFilter:false,
      rowHeight:44,
      enableSorting:false,
      columnDefs:'columnDefinitions'
    };

  $scope.validatePositiveInteger = function (value) {
    if (value == undefined) {
      return false;
    }
    return !utils.isPositiveNumber(value);
  };


  $scope.totalCost = function () {
    if (!$scope.rnr) return;
    return parseFloat(parseFloat($scope.rnr.fullSupplyItemsSubmittedCost) + parseFloat($scope.rnr.nonFullSupplyItemsSubmittedCost)).toFixed(2);
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

  function validateForSave() {
    var valid = true;
    $($scope.rnr.lineItems).each(function (i, lineItem) {
      if (lineItem.quantityApproved != undefined && !utils.isPositiveNumber(lineItem.quantityApproved)) {
        valid = false;
        return false;
      }
    });
    $($scope.rnr.nonFullSupplyLineItems).each(function (i, lineItem) {
      if (lineItem.quantityApproved != undefined && !utils.isPositiveNumber(lineItem.quantityApproved)) {
        valid = false;
        return false;
      }
    });
    return valid;
  }

  $scope.saveRnr = function () {
    $scope.approvedQuantityInvalidFlag = false;
    $scope.approvedQuantityInvalidFlag = !validateForSave();
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


  function validateForApprove() {
    var valid = true;
    $($scope.rnr.lineItems).each(function (i, lineItem) {
      if (lineItem.quantityApproved == undefined || !utils.isPositiveNumber(lineItem.quantityApproved)) {
        valid = false;
        return false;
      }
    });
    $($scope.rnr.nonFullSupplyLineItems).each(function (i, lineItem) {
      if (lineItem.quantityApproved == undefined || !utils.isPositiveNumber(lineItem.quantityApproved)) {
        valid = false;
        return false;
      }
    });
    return valid;
  }

  $scope.approveRnr = function () {
    $scope.approvedQuantityRequiredFlag = !validateForApprove();
    if ($scope.approvedQuantityRequiredFlag) {
      $scope.error = "Please complete the R&R form before approving";
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


  $scope.showCurrencySymbol = function (value) {
    if (value != 0 && (isUndefined(value) || value.length == 0 || value == false)) {
      return "";
    }
    return "defined";
  };

  $scope.periodDisplayName = function () {
    if (!$scope.rnr) return;

    var startDate = new Date($scope.rnr.period.startDate);
    var endDate = new Date($scope.rnr.period.endDate);

    return utils.getFormattedDate(startDate) + ' - ' + utils.getFormattedDate(endDate);
  };
}
