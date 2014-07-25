/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function ApproveRnrListController($scope, requisitionList, $location, messageService) {
  $scope.requisitions = requisitionList;
  $scope.filteredRequisitions = $scope.requisitions;
  $scope.selectedItems = [];

  $scope.gridOptions = { data: 'filteredRequisitions',
    multiSelect: false,
    selectedItems: $scope.selectedItems,
    afterSelectionChange: function (rowItem, event) {
      $scope.openRnr();
    },
    showFooter: false,
    showSelectionCheckbox: false,
    enableColumnResize: true,
    showColumnMenu: false,
    sortInfo: { fields: ['submittedDate'], directions: ['asc'] },
    showFilter: false,
    columnDefs: [
      {field: 'programName', displayName: messageService.get("program.header") },
      {field: 'facilityCode', displayName: messageService.get("option.value.facility.code")},
      {field: 'facilityName', displayName: messageService.get("option.value.facility.name")},
      {field: 'facilityType', displayName: messageService.get("option.value.facility.type")},
      {field: 'districtName', displayName: messageService.get("option.value.facility.district")},
      {field: 'stringPeriodStartDate', displayName: messageService.get("label.period.start.date")},
      {field: 'stringPeriodEndDate', displayName: messageService.get("label.period.end.date")},
      {field: 'stringSubmittedDate', displayName: messageService.get("label.date.submitted")},
      {field: 'stringModifiedDate', displayName: messageService.get("label.date.modified")},
      {field: 'emergency', displayName: messageService.get("requisition.type.emergency"),
        cellTemplate: '<div class="ngCellText checked"><i ng-class="{\'icon-ok\': row.entity.emergency}"></i></div>',
        width: 110 }
    ]
  };

  $scope.openRnr = function () {
    $scope.$parent.period = {'startDate': $scope.selectedItems[0].periodStartDate, 'endDate': $scope.selectedItems[0].periodEndDate};
    $location.url("rnr-for-approval/" + $scope.selectedItems[0].id + '/' + $scope.selectedItems[0].programId + '?supplyType=fullSupply&page=1');
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

  function contains(string, query) {
    return string.toLowerCase().indexOf(query.toLowerCase()) != -1;
  }

  function matchesAnyField(query, rnr) {
    var rnrString = "|" + rnr.programName + "|" + rnr.facilityName + "|" + rnr.facilityCode + "|";
    return contains(rnrString, query);
  }
}

ApproveRnrListController.resolve = {
  requisitionList: function ($q, $timeout, RequisitionForApproval) {
    var deferred = $q.defer();
    $timeout(function () {
      RequisitionForApproval.get({}, function (data) {
        deferred.resolve(data.rnr_list);
      }, {});
    }, 100);
    return deferred.promise;
  }
};