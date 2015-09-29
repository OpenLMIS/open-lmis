/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function ConvertToOrderListController($scope, Orders, RequisitionForConvertToOrder,SupplyingDepots, $dialog, messageService, $routeParams, $location) {
  $scope.message = "";
  $scope.maxNumberOfPages = 10;
  $scope.selectedItems = [];
  $scope.searchOptions = [
    {value: "all", name: "option.value.all"},
    {value: "programName", name: "option.value.program"},
    {value: "facilityCode", name: "option.value.facility.code"},
    {value: "facilityName", name: "option.value.facility.name"},
    {value: "supplyingDepot", name: "label.supplying.depot"}
  ];

  $scope.noRequisitions = false;

  SupplyingDepots.get(function(data){
    $scope.depots = data.supplylines;
  });


  $scope.selectedSearchOption = $scope.searchOptions[0];
  $scope.sortOptions = { fields: ['submittedDate'], directions: ['asc'] };

  var refreshGrid = function () {

    $scope.noRequisitionSelectedMessage = "";
    $scope.selectedItems.length = 0;
    $scope.currentPage = $routeParams.page ? utils.parseIntWithBaseTen($routeParams.page) : 1;
    $scope.selectedSearchOption = _.findWhere($scope.searchOptions,
        {value: $routeParams.searchType}) || $scope.searchOptions[0];
    $scope.query = $routeParams.searchVal;

    RequisitionForConvertToOrder.get({page: $scope.currentPage, searchType: $scope.selectedSearchOption.value,
          searchVal: $scope.query, sortBy: $scope.sortOptions.fields[0], sortDirection: $scope.sortOptions.directions[0]},
        function (data) {
          $scope.filteredRequisitions = data.rnr_list;

          $scope.numberOfPages = data.number_of_pages || 1;
          $scope.resultCount = $scope.filteredRequisitions.length;
          if (!$scope.resultCount) $scope.noRequisitions = true;
        }, function () {
          $location.search('page', 1);
        });
  };

  $scope.$watch('sortOptions', function (newValue, oldValue) {
    if (newValue.fields[0] != oldValue.fields[0] || newValue.directions[0] != oldValue.directions[0])
      refreshGrid();
  }, true);

  $scope.$on('$routeUpdate', refreshGrid);

  refreshGrid();

  $scope.inputKeypressHandler = function ($event) {
    if ($event.keyCode == 13) {
      $event.preventDefault();
      $scope.updateSearchParams();
    }
  };

  $scope.selectSearchType = function (searchOption) {
    $scope.selectedSearchOption = searchOption;
  };

  $scope.updateSearchParams = function () {
    $location.search({page: 1, searchType: $scope.selectedSearchOption.value, searchVal: $scope.query || ''});
  };

  $scope.$watch("currentPage", function () {
    $location.search("page", $scope.currentPage);
  });

  $scope.gridOptions = { data: 'filteredRequisitions',
    selectedItems: $scope.selectedItems,
    multiSelect: true,
    showSelectionCheckbox: true,
    sortInfo: $scope.sortOptions,
    useExternalSorting: true,
    columnDefs: [
      {field: 'programName', displayName: messageService.get("program.header") },
      {field: 'facilityCode', displayName: messageService.get("option.value.facility.code")},
      {field: 'facilityName', displayName: messageService.get("option.value.facility.name")},
      {field: 'districtName', sortable:false, displayName: messageService.get("option.value.facility.district")},
      {field: 'stringPeriodStartDate', sortable:false, displayName: messageService.get("label.period.start.date")},
      {field: 'stringPeriodEndDate', sortable:false, displayName: messageService.get("label.period.end.date")},
      {field: 'stringSubmittedDate', sortable:false, displayName: messageService.get("label.date.submitted")},
      {field: 'stringModifiedDate', sortable:false, displayName: messageService.get("label.date.modified")},
      {field: 'supplyingDepotName', sortable:false,
        displayName: messageService.get("label.supplying.depot"),
        width: 220,
        cellTemplate: "<div><select ng-model='row.entity.supplyingDepotId'><option ng-repeat='t in depots' ng-selected='t.id == row.entity.supplyingDepotId' value='{{t.id}}'>{{t.name}}</option></select></div>"},
      {field: 'emergency', sortable:false, displayName: messageService.get("requisition.type.emergency"),
        cellTemplate: "<div id=\"orderCheckbox{{ $parent.$index }}\" class='ngCellText checked'><i ng-class='{\"icon-ok\": row.entity.emergency}'></i></div>",
        width: 110 }
    ]
  };

  var showConfirmModal = function () {
    var options = {
      id: "confirmDialog",
      header: "label.confirm.action",
      body: "msg.question.confirmation"
    };

    function callBack() {
      return function (result) {
        if (result) {
          convert();
        }
      };
    }

    OpenLmisDialog.newDialog(options, callBack(), $dialog);
  };

  $scope.convertToOrder = function () {
    $scope.message = "";
    $scope.noRequisitionSelectedMessage = "";
    if ($scope.selectedItems.length === 0) {
      $scope.noRequisitionSelectedMessage = "msg.select.atleast.one.rnr";
      return;
    }
    showConfirmModal();
  };

  var convert = function () {
    var successHandler = function () {
      refreshGrid();
      $scope.message = "msg.rnr.converted.to.order";
      $scope.error = "";
    };

    var errorHandler = function (response) {
      $scope.message = "";
      if (response.status === 409) {
        $scope.error = response.data.error;
      } else {
        $scope.error = "msg.error.occurred";
      }

      refreshGrid();
    };

    Orders.post({}, $scope.selectedItems, successHandler, errorHandler);
  };
}
