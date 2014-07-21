/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function SupplyLineSearchController($scope, $location, navigateBackService, SupplyLinesSearch) {
  $scope.searchOptions = [
    {value: "facility", name: "label.supplying.facility"},
    {value: "supervisoryNode", name: "label.supervisory.node"},
    {value: "program", name: "label.program"}
  ];

  $scope.showResults = false;
  $scope.currentPage = 1;
  $scope.selectedSearchOption = navigateBackService.selectedSearchOption || $scope.searchOptions[0];

  $scope.selectSearchType = function (searchOption) {
    $scope.selectedSearchOption = searchOption;
  };

  $scope.$on('$viewContentLoaded', function () {
    $scope.query = navigateBackService.query;
  });

  $scope.edit = function (id) {
    var data = {query: $scope.query, selectedSearchOption: $scope.selectedSearchOption};
    navigateBackService.setData(data);
    $location.path('edit/' + id);
  };

  $scope.search = function (page, lastQuery) {
    if (!($scope.query || lastQuery)) return;
    lastQuery ? getSupplyLines(page, lastQuery) : getSupplyLines(page, $scope.query);
  };

  function getSupplyLines(page, query){
    query = query.trim();
    $scope.searchedQuery = query;
    SupplyLinesSearch.get({page: page, searchParam: $scope.searchedQuery, column: $scope.selectedSearchOption.value}, function (data) {
      $scope.supplyLines = data.supplyLines;
      $scope.pagination = data.pagination;
      $scope.totalItems = $scope.pagination.totalRecords;
      $scope.currentPage = $scope.pagination.page;
      $scope.showResults = true;
    }, {});
  }

  $scope.$watch('currentPage', function () {
    if ($scope.currentPage !== 0)
      $scope.search($scope.currentPage, $scope.searchedQuery);
  });

  $scope.clearSearch = function () {
    $scope.query = "";
    $scope.totalItems = 0;
    $scope.supplyLines = [];
    $scope.showResults = false;
    angular.element("#searchSupplyLine").focus();
  };

  $scope.triggerSearch = function (event) {
    if (event.keyCode === 13) {
      $scope.search(1);
    }
  };
}