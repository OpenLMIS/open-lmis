/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ConvertToOrderListController($scope, pagedRequisitionList, Orders,
                                      RequisitionForConvertToOrder, $dialog,
                                      messageService, $routeParams, $location) {
  $scope.requisitions = pagedRequisitionList.rnr_list;
  $scope.filteredRequisitions = pagedRequisitionList.rnr_list;
  $scope.numberOfPages = pagedRequisitionList.number_of_pages;
  $scope.selectedItems = [];
  $scope.message = "";
  $scope.noRequisitionSelectedMessage = "";
  $scope.maxNumberOfPages = 10;
  $scope.searchValue = "All";

  $scope.searchOptions = [
    {searchType: "all", value: "option.value.all"},
    {searchType: "programName", value: "option.value.program"},
    {searchType: "facilityCode", value: "option.value.facility.code"},
    {searchType: "facilityName", value: "option.value.facility.name"},
    {searchType: "supplyingDepot", value: "label.supplying.depot"}
  ];

  $scope.selectSearchType = function (searchOption) {
    $scope.searchField = searchOption.searchType;
    $scope.searchValue = searchOption.value;
  };

  function setCurrentPage() {
    $scope.currentPage = (utils.isValidPage($routeParams.page, $scope.numberOfPages)) ? parseInt($routeParams.page, $scope.maxNumberOfPages) : 1;
  }

  setCurrentPage();

  $scope.$on('$routeUpdate', function () {
    setCurrentPage();
    $scope.fetchFilteredRequisitions();
  });

  $scope.$watch("currentPage", function () {
    $location.search("page", $scope.currentPage);
  });

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

  $scope.dialogCloseCallback = function (result) {
    if (result) {
      convert();
    }
  };

  var showConfirmModal = function () {
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

  $scope.fetchFilteredRequisitions = function () {
    if ($scope.requestInProgress) return;

    $scope.requestInProgress = true;
    RequisitionForConvertToOrder.get({page: $scope.currentPage,
      searchType: $scope.searchField, searchVal: $scope.query}, function (data) {

      $scope.filteredRequisitions = data.rnr_list;
      $scope.numberOfPages = data.number_of_pages;
      $scope.selectedItems.length = 0;
      $scope.resultCount = $scope.filteredRequisitions.length;
      $scope.requestInProgress = false;
    });
  };

  var convert = function () {
    var successHandler = function () {
      $scope.fetchFilteredRequisitions();
      $scope.message = "msg.rnr.converted.to.order";
      $scope.error = "";

    };

    var errorHandler = function (response) {
      $scope.message = "";
      if (response.data.error) {
        $scope.error = response.data.error;
      } else {
        $scope.error = "msg.error.occurred";
      }

      $scope.fetchFilteredRequisitions();
    };

    Orders.post({}, $scope.gridOptions.selectedItems, successHandler, errorHandler);
  };
}

ConvertToOrderListController.resolve = {
  pagedRequisitionList: function ($q, $timeout, $route,
                                  RequisitionForConvertToOrder) {
    var deferred = $q.defer();
    $timeout(function () {
      RequisitionForConvertToOrder.get({page: $route.current.params.page ? $route.current.params.page : 1}, function (data) {
        deferred.resolve(data);
      }, {});
    }, 100);
    return deferred.promise;
  }
};