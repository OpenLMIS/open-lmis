/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ConvertToOrderListController($scope, requisitionList, Orders, RequisitionForConvertToOrder, $dialog,
                                      messageService) {
  $scope.requisitions = requisitionList;
  $scope.filteredRequisitions = $scope.requisitions;
  $scope.selectedItems = [];
  $scope.message = "";
  $scope.noRequisitionSelectedMessage = "";

  $scope.gridOptions = { data: 'filteredRequisitions',
    selectedItems: $scope.selectedItems,
    multiSelect: true,
    showSelectionCheckbox: true,
    sortInfo: { fields: ['submittedDate'], directions: ['asc'] },
    columnDefs: [
      {field: 'programName', displayName: messageService.get("program.header") },
      {field: 'facilityCode', displayName: messageService.get("option.value.facility.code")},
      {field: 'facilityName', displayName: messageService.get("option.value.facility.name")},
      {field: 'periodStartDate', displayName: messageService.get("label.period.start.date"), cellFilter: "date:'dd/MM/yyyy'"},
      {field: 'periodEndDate', displayName: messageService.get("label.period.end.date"), cellFilter: "date:'dd/MM/yyyy'"},
      {field: 'submittedDate', displayName: messageService.get("label.date.submitted"), cellFilter: "date:'dd/MM/yyyy'"},
      {field: 'modifiedDate', displayName: messageService.get("label.date.modified"), cellFilter: "date:'dd/MM/yyyy'"},
      {field: 'supplyingDepotName', displayName: messageService.get("label.supplying.depot")}
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
    if (result) {
      convert();
    }
  };

  showConfirmModal = function () {
    var options = {
      id: "confirmDialog",
      header: messageService.get("label.confirm.action"),
      body: messageService.get("msg.question.confirmation")
    };
    OpenLmisDialog.newDialog(options, $scope.dialogCloseCallback, $dialog, messageService);
  };

  $scope.convertToOrder = function () {
    $scope.message = "";
    $scope.noRequisitionSelectedMessage = "";
    if ($scope.gridOptions.selectedItems.length == 0) {
      $scope.noRequisitionSelectedMessage = "msg.select.atleast.one.rnr";
      return;
    }
    showConfirmModal();
  };

  var fetchPendingRequisitions = function () {
    RequisitionForConvertToOrder.get({}, function (data) {
      $scope.requisitions = data.rnr_list;
      $scope.selectedItems.length = 0;
      $scope.filterRequisitions();
    });
  };

  var convert = function () {
    var successHandler = function () {
      fetchPendingRequisitions();
      $scope.message = "msg.rnr.converted.to.order";
      $scope.error = "";

    };

    var errorHandler = function (response) {
      $scope,message= "";
      if (response.data.error) {
        $scope.error = response.data.error;
      } else {
        $scope.error = "msg.error.occurred";
      }

      fetchPendingRequisitions();
    };

    Orders.post({}, $scope.gridOptions.selectedItems, successHandler, errorHandler);
  };

  function contains(string, query) {
    return string.toLowerCase().indexOf(query.toLowerCase()) != -1;
  }

  function matchesAnyField(query, rnr) {
    var rnrString = "|" + rnr.programName + "|" + rnr.facilityCode + "|" + "|" + rnr.facilityName + "|" + "|" + rnr.supplyingDepotName + "|";
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