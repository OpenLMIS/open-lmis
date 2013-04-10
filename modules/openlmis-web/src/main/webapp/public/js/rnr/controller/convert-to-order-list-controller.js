/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ConvertToOrderListController($scope, requisitionList, RequisitionOrder, RequisitionForConvertToOrder, $dialog) {
  $scope.requisitions = requisitionList;
  $scope.filteredRequisitions = $scope.requisitions;
  $scope.selectedItems = [];
  $scope.message = "";

  $scope.gridOptions = { data: 'filteredRequisitions',
    multiSelect: true,
    selectedItems: $scope.selectedItems,
    showFooter: false,
    showSelectionCheckbox: true,
    showColumnMenu: false,
    sortInfo: { field: 'submittedDate', direction: 'ASC'},
    showFilter: false,
    columnDefs: [
      {field: 'programName', displayName: 'Program' },
      {field: 'facilityCode', displayName: 'Facility Code'},
      {field: 'facilityName', displayName: "Facility Name"},
      {field: 'periodStartDate', displayName: "Period Start Date", cellFilter: "date:'dd/MM/yyyy'"},
      {field: 'periodEndDate', displayName: "Period End Date", cellFilter: "date:'dd/MM/yyyy'"},
      {field: 'submittedDate', displayName: "Date Submitted", cellFilter: "date:'dd/MM/yyyy'"},
      {field: 'modifiedDate', displayName: "Date Modified", cellFilter: "date:'dd/MM/yyyy'"},
      {field: 'supplyingDepot', displayName: "Supplying Depot"}
    ]
  };

  $scope.filterRequisitions = function () {
    $scope.filteredRequisitions = [];
    var query = $scope.query || "";
    var searchField = $scope.searchField;

    $scope.filteredRequisitions = $.grep($scope.requisitions, function (rnr) {
      return (searchField) ? contains(rnr[searchField], query) : matchesAnyField(query, rnr);
    });

    $scope.resultCount = $scope.filteredRequisitions.length;
  };

  $scope.dialogCloseCallback = function (result) {
    if(result) {
      convert();
    }
  };

  showConfirmModal = function () {
    var options = {
      id: "confirmDialog",
      header: "Confirm Action",
      body: "Are you sure? Please confirm."
    };
    OpenLmisDialog.new(options, $scope.dialogCloseCallback, $dialog);
  };

  $scope.convertToOrder = function () {
    if ($scope.gridOptions.selectedItems.length == 0) {
      $scope.message = "Please select atleast one Requisition for Converting to Order.";
      return;
    }
    showConfirmModal();
  };

  var convert = function () {
    var successHandler = function () {
      RequisitionForConvertToOrder.get({}, function (data) {
        $scope.requisitions = data.rnr_list;
        $scope.filterRequisitions();
      });

      $scope.message = "The requisition(s) have been successfully converted to Orders";
      $scope.error = "";
    };

    var errorHandler = function () {
      $scope.error = "Error Occurred";
    };

    var rnrList = {"rnrList": $scope.gridOptions.selectedItems};
    RequisitionOrder.save({}, rnrList, successHandler, errorHandler);
  }

  function contains(string, query) {
    return string.toLowerCase().indexOf(query.toLowerCase()) != -1;
  }

  function matchesAnyField(query, rnr) {
    var rnrString = "|" + rnr.programName + "|" + rnr.facilityCode + "|" + "|" + rnr.facilityName + "|" + "|" + rnr.supplyingDepot + "|";
    return contains(rnrString, query);
  }
}

ConvertToOrderListController.resolve = {
  requisitionList: function ($q, $timeout, RequisitionForConvertToOrder) {
    var deferred = $q.defer();
    $timeout(function () {
      RequisitionForConvertToOrder.get({}, function (data) {
        deferred.resolve(data.rnr_list);
      }, {});
    }, 100);
    return deferred.promise;
  }
};