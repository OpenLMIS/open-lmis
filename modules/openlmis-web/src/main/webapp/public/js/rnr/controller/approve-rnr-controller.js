function ApproveRnrController($scope, requisition, Requisitions, rnrColumns, $location, currency, $routeParams) {

  $scope.rnr = requisition;
  $scope.rnrColumns = rnrColumns;
  $scope.currency = currency;

  $scope.error = "";
  $scope.message = "";

  $scope.lossesAndAdjustmentsModal = [];
  $scope.pageLineItems = [];
  $scope.columnDefinitions = [];
  $scope.showPositiveIntegerError = [];

  $scope.isDirty = false;

  function updateSupplyTypeForGrid() {
    $scope.showNonFullSupply = !!($routeParams.supplyType == 'non-full-supply');
  }

  function prepareColumnDefinitions() {
    var columnDefinitions = [];
    var visibleColumns = _.where($scope.rnrColumns, {'visible':true});
    if (visibleColumns.length > 0) {
      $(visibleColumns).each(function (i, column) {
        switch (column.name) {
          case 'price':
          case 'cost' :
            columnDefinitions.push({field:column.name, displayName:column.label, cellTemplate:currencyTemplate('row.entity.' + column.name)});
            break;
          case 'lossesAndAdjustments' :
            columnDefinitions.push({field:column.name, displayName:column.label, cellTemplate:lossesAndAdjustmentsTemplate()});
            break;
          case 'quantityApproved' :
            columnDefinitions.push({field:column.name, displayName:column.label, width:140, cellTemplate:positiveIntegerCellTemplate(column.name, 'row.entity.quantityApproved')});
            break;
          case 'remarks' :
            columnDefinitions.push({field:column.name, displayName:column.label, cellTemplate:freeTextCellTemplate(column.name, 'row.entity.remarks')});
            break;
          default :
            columnDefinitions.push({field:column.name, displayName:column.label});
        }
      });
      $scope.columnDefinitions = columnDefinitions;
    } else {
      $scope.$parent.error = "Please contact Admin to define R&R template for this program";
    }
  }

  function fillPagedGridData() {
    var gridLineItems = $scope.showNonFullSupply ? $scope.rnr.nonFullSupplyLineItems : $scope.rnr.fullSupplyLineItems;
    $scope.numberOfPages = Math.ceil(gridLineItems.length / $scope.pageSize)? Math.ceil(gridLineItems.length / $scope.pageSize):1;
    $scope.currentPage = (utils.isValidPage($routeParams.page, $scope.numberOfPages)) ? parseInt($routeParams.page, 10) : 1;
    $scope.pageLineItems = gridLineItems.slice(($scope.pageSize * ($scope.currentPage - 1)), $scope.pageSize * $scope.currentPage);
  }

  function populateRnrLineItems() {
    var lineItemsJson = $scope.rnr.fullSupplyLineItems;
    $scope.rnr.fullSupplyLineItems = [];

    $(lineItemsJson).each(function (i, lineItem) {
      var rnrLineItem = new RnrLineItem(lineItem, $scope.rnr.period.numberOfMonths, $scope.programRnrColumnList, $scope.rnr.status);

      rnrLineItem.updateCostWithApprovedQuantity();
      $scope.rnr.fullSupplyLineItems.push(rnrLineItem);
    });
    var nonFullSupplyLineItemsJson = $scope.rnr.nonFullSupplyLineItems;
    $scope.rnr.nonFullSupplyLineItems = [];
    $(nonFullSupplyLineItemsJson).each(function (i, lineItem) {
      var rnrLineItem = new RnrLineItem(lineItem, $scope.rnr.period.numberOfMonths, $scope.programRnrColumnList, $scope.rnr.status);

      rnrLineItem.updateCostWithApprovedQuantity();
      $scope.rnr.nonFullSupplyLineItems.push(rnrLineItem);
    });
  }

  updateSupplyTypeForGrid();
  populateRnrLineItems();
  fillPagedGridData();
  prepareColumnDefinitions();

  $scope.$watch("currentPage", function () {
    if(!$routeParams.supplyType) $location.search('supplyType', 'full-supply');
    $location.search("page", $scope.currentPage);
  });

  $scope.closeLossesAndAdjustmentsForRnRLineItem = function (rnrLineItem) {
    $scope.lossesAndAdjustmentsModal[rnrLineItem.id] = false;
  };

  $scope.switchSupplyType = function (supplyType) {
    $location.search('page', 1);
    $location.search('supplyType', supplyType);
  };

  $scope.$on('$routeUpdate', function () {
    if (!utils.isValidPage($routeParams.page, $scope.numberOfPages)) {
      $location.search('page', 1);
      return;
    }
    if($scope.isDirty)
      $scope.saveRnr();
    updateSupplyTypeForGrid();
    fillPagedGridData();
  });

  $scope.showLossesAndAdjustmentModalForLineItem = function (lineItem) {
    $scope.lossesAndAdjustmentsModal[lineItem.id] = true;
  };

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
    return '<div><input maxlength="250" ng-change = \'setDirty()\' name="' + field + '" ng-model="' + value + '"/></div>';
  }

  function positiveIntegerCellTemplate(field, value) {
    return '<div><ng-form name="positiveIntegerForm"> <input ng-change = \'validatePositiveInteger(row.entity)\' ' +
      'ui-event="{blur : \'showPositiveIntegerError[row.entity.id] = false\'}"' +
      'ng-class="{\'required-error\': approvedQuantityRequiredFlag && positiveIntegerForm.' + field + '.$error.required}" ' +
      '  ng-required="true" maxlength="8"  name=' + field + ' ng-model=' + value + ' />' +
      '<span class="rnr-form-error" id=' + field + ' ng-show="showPositiveIntegerError[row.entity.id]" >Please Enter Numeric value</span></ng-form></div>';
  }

  $scope.rnrGrid = {
    data:'pageLineItems',
    canSelectRows:false,
    displayFooter:false,
    displaySelectionCheckbox:false,
    showColumnMenu:false,
    showFilter:false,
    rowHeight:44,
    enableSorting:false,
    columnDefs:'columnDefinitions'
  };

  $scope.setDirty = function () {
    $scope.isDirty = true;
  };

  $scope.validatePositiveInteger = function (lineItem) {
    $scope.setDirty();
    if (!isUndefined(lineItem.quantityApproved)) {
      $scope.showPositiveIntegerError[lineItem.id] = !utils.isPositiveNumber(lineItem.quantityApproved);
    }

    lineItem.updateCostWithApprovedQuantity();
  };


  $scope.totalCost = function () {
    if (!$scope.rnr) return;
    return parseFloat(parseFloat($scope.rnr.fullSupplyItemsSubmittedCost) + parseFloat($scope.rnr.nonFullSupplyItemsSubmittedCost)).toFixed(2);
  };

  function removeExtraDataForPostFromRnr() {
    var rnr = {"id":$scope.rnr.id, "fullSupplyLineItems":[], "nonFullSupplyLineItems":[]};

    _.each($scope.rnr.fullSupplyLineItems, function (lineItem) {
      rnr.fullSupplyLineItems.push(_.omit(lineItem, ['rnr', 'programRnrColumnList']));
    });
    _.each($scope.rnr.nonFullSupplyLineItems, function (lineItem) {
      rnr.nonFullSupplyLineItems.push(_.omit(lineItem, ['rnr', 'programRnrColumnList']));
    });
    return rnr;
  }

  var fadeSaveMessage = function () {
    $scope.$apply(function () {
      angular.element("#saveSuccessMsgDiv").fadeOut('slow', function () {
        $scope.message = '';
      });
    });
  };

  $scope.saveRnr = function () {
    var rnr = removeExtraDataForPostFromRnr();
    Requisitions.update({id:$scope.rnr.id, operation:"save"},
      rnr, function (data) {
        $scope.message = data.success;
        $scope.error = "";
        setTimeout(fadeSaveMessage, 3000);
      }, function (data) {
        $scope.error = data.error;
        $scope.message = "";
      });
    $scope.isDirty = false;
  };


  function validateForApprove() {
    var valid = true;
    $($scope.rnr.fullSupplyLineItems).each(function (i, lineItem) {
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
  };


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

ApproveRnrController.resolve = {

  requisition:function ($q, $timeout, RequisitionForApprovalById, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      RequisitionForApprovalById.get({id:$route.current.params.rnr}, function (data) {
        deferred.resolve(data.rnr);
      }, {});
    }, 100);
    return deferred.promise;
  },

  rnrColumns:function ($q, $timeout, ProgramRnRColumnList, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      ProgramRnRColumnList.get({programId:$route.current.params.program}, function (data) {
        deferred.resolve(data.rnrColumnList);
      }, {});
    }, 100);
    return deferred.promise;
  },

  currency:function ($q, $timeout, ReferenceData) {
    var deferred = $q.defer();
    $timeout(function () {
      ReferenceData.get({}, function (data) {
        deferred.resolve(data.currency);
      }, {});
    }, 100);
    return deferred.promise;
  }
};

