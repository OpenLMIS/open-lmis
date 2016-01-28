/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function RequisitionGroupSearchController($scope, RequisitionGroups, $location, navigateBackService) {

  $scope.searchOptions = [
    {value: "requisitionGroup", name: "option.value.requisition.group"},
    {value: "supervisoryNode", name: "option.value.supervisory.node"}
  ];

  $scope.showCloseButton = false;
  $scope.currentPage = 1;
  $scope.searchedQuery = '%';
  $scope.selectedSearchOption = navigateBackService.selectedSearchOption || $scope.searchOptions[0];

  $scope.selectSearchType = function (searchOption) {
    $scope.selectedSearchOption = searchOption;
    $scope.search(1, $scope.searchedQuery);
  };

  $scope.$on('$viewContentLoaded', function () {
    $scope.query = navigateBackService.query;
  });

  $scope.edit = function (id) {
    var data = {query: $scope.query, selectedSearchOption: $scope.selectedSearchOption};
    navigateBackService.setData(data);
    $location.path('edit/' + id);
  };

  $scope.$watch('currentPage', function () {
    if ($scope.currentPage !== 0)
      $scope.search($scope.currentPage, $scope.searchedQuery);
  });

  $scope.search = function (page, lastQuery) {
    if (!($scope.query || lastQuery)) return;
    lastQuery ? getRequisitionGroups(page, lastQuery) : getRequisitionGroups(page, $scope.query);
  };

  function getRequisitionGroups(page, query) {
    query = query.trim();
    $scope.searchedQuery = query;
    RequisitionGroups.get({"searchParam": $scope.searchedQuery, "columnName": $scope.selectedSearchOption.value, "page": page}, function (data) {
      $scope.requisitionGroupList = data.requisitionGroupList;
      $scope.pagination = data.pagination;
      $scope.totalItems = $scope.pagination.totalRecords;
      $scope.currentPage = $scope.pagination.page;
      if($scope.searchedQuery === '%'){
        $scope.showCloseButton = false;
      }else {
        $scope.showCloseButton = true;
      }
    }, {});
  }

  $scope.clearSearch = function () {
    $scope.query = "";
    $scope.totalItems = 0;
    $scope.requisitionGroupList = [];
    $scope.showCloseButton = false;
    angular.element("#searchRequisitionGroup").focus();
    $scope.searchedQuery= "%";
    $scope.currentPage = 1;
    $scope.search($scope.currentPage, $scope.searchedQuery);
  };

  $scope.triggerSearch = function (event) {
    if (event.keyCode === 13) {
      $scope.search(1);
    }
  };
}
