/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ViewRnrController($scope, $routeParams, requisition, rnrColumns, currency, $timeout, $location) {

  $scope.lossesAndAdjustmentsModal = [];
  $scope.fullSupplyLink = "#/requisition/" + $routeParams.rnr + '/' + $routeParams.program + '?supplyType=full-supply&page=1';

  $scope.nonFullSupplyLink = "#/requisition/" + $routeParams.rnr + '/' + $routeParams.program + '?supplyType=non-full-supply&page=1';

  $scope.pageLineItems = [];
  $scope.columnDefs = [];
  $scope.rnr = new Rnr(requisition, rnrColumns);
  $scope.currency = currency;
  $scope.rnrColumns = rnrColumns;
  prepareColumnDefs();

  $scope.$watch('currentPage', function () {
    $location.search('page', $scope.currentPage);
  });

  function updateSupplyType() {
    $scope.showNonFullSupply = !!($routeParams.supplyType == 'non-full-supply');

  }

  $scope.$on('$routeUpdate', function () {
    updateSupplyType();
    fillPagedGridData();
  });

  $scope.$emit('$routeUpdate');

  function prepareColumnDefs() {
    $scope.columnDefs = [
      {field:'productCategory', displayName:'Product Category', width:0}
    ];
    $($scope.rnrColumns).each(function (index, column) {
      if (!column.visible) return;

      if (column.name == 'lossesAndAdjustments') {
        if ($scope.showNonFullSupply) {
          $scope.columnDefs.push({field:'totalLossesAndAdjustments', displayName:column.label});
        } else {
          $scope.columnDefs.push({field:column.name, displayName:column.label, cellTemplate:lossesAndAdjustmentsTemplate()});
        }
        return;
      }
      if (column.name == "cost" || column.name == "price") {
        $scope.columnDefs.push({field:column.name, displayName:column.label, cellTemplate:currencyTemplate('COL_FIELD')});
        return;
      }
      if ($scope.rnr.status != 'APPROVED' && $scope.rnr.status != 'ORDERED' && column.name == 'quantityApproved') return;

      $scope.columnDefs.push({field:column.name, displayName:column.label});
    });
  }

  function fillPagedGridData() {
    var gridLineItems = $scope.showNonFullSupply ? $scope.rnr.nonFullSupplyLineItems : $scope.rnr.fullSupplyLineItems;
    $scope.numberOfPages = Math.ceil(gridLineItems.length / $scope.pageSize) ? Math.ceil(gridLineItems.length / $scope.pageSize) : 1;
    $scope.currentPage = (utils.isValidPage($routeParams.page, $scope.numberOfPages)) ? parseInt($routeParams.page, 10) : 1;
    $scope.pageLineItems = gridLineItems.slice(($scope.pageSize * ($scope.currentPage - 1)), $scope.pageSize * $scope.currentPage);
  }

  $scope.rowToggle = function (row) {
    if (row.collapsed) {
      row.toggleExpand();
    }
  };

  $scope.$on('ngGridEventRows', function () {
    $timeout(function () {
      $(angular.element('.ngAggregate')).each(function (i, aggregate) {
        aggregate.click();
      });
    });
  });

  function aggregateTemplate() {
    return "<div ng-click=\"rowToggle(row)\" ng-style=\"{'left': row.offsetleft}\" class=\"ngAggregate productCategory\">" +
      "    <span class=\"ngAggregateText\">{{row.label CUSTOM_FILTERS}}</span>" +
      "    <div style='display: none;' class=\"{{row.aggClass()}}\"></div>" +
      "</div>" +
      "";
  }

  $scope.rnrGrid = {
    data:'pageLineItems',
    enableRowSelection:false,
    showFooter:false,
    showSelectionCheckbox:false,
    aggregateTemplate:aggregateTemplate(),
    showColumnMenu:false,
    showFilter:false,
    rowHeight:44,
    enableColumnResize:true,
    enableColumnReordering:false,
    enableSorting:false,
    columnDefs:'columnDefs',
    groups:['productCategory']
  };


  function currencyTemplate(value) {
    return '<span  class = "cell-text" ng-show = "showCurrencySymbol(' + value + ')"  ng-bind="currency"></span >&nbsp; &nbsp;<span ng-bind = "' + value + '" class = "cell-text" ></span >'
  }

  function lossesAndAdjustmentsTemplate() {
    return '<div id="lossesAndAdjustments" modal="lossesAndAdjustmentsModal[row.entity.id]">' +
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
      '<div>' +
      '<a ng-click="showLossesAndAdjustmentModalForLineItem(row.entity)" class="rnr-adjustment">' +
      '<span class="adjustment-value" ng-bind="row.entity.totalLossesAndAdjustments"></span>' +
      '</a>' +
      '</div>';
  }

  $scope.totalCost = function () {
    if (!$scope.rnr) return;
    return parseFloat(parseFloat($scope.rnr.fullSupplyItemsSubmittedCost) + parseFloat($scope.rnr.nonFullSupplyItemsSubmittedCost)).toFixed(2);
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

  $scope.closeLossesAndAdjustmentsForRnRLineItem = function (rnrLineItem) {
    $scope.lossesAndAdjustmentsModal[rnrLineItem.id] = false;
  };

  $scope.showLossesAndAdjustmentModalForLineItem = function (lineItem) {
    $scope.lossesAndAdjustmentsModal[lineItem.id] = true;
  };
}

ViewRnrController.resolve = {

  requisition:function ($q, $timeout, RequisitionById, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      RequisitionById.get({id:$route.current.params.rnr}, function (data) {
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
