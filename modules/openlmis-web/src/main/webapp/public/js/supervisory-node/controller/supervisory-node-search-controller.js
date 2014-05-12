/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function SupervisoryNodeSearchController($scope, $location, $routeParams, SupervisoryNodesSearch) {


  $scope.searchOptions = [
    {value: "node", name: "option.value.supervisory.node"},
    {value: "parent", name: "option.value.supervisory.node.parent"}
  ];

  $scope.selectedSearchOption = $scope.searchOptions[0];

  $scope.selectSearchType = function (searchOption) {
    $scope.selectedSearchOption = searchOption;
    $scope.pagination = undefined;
    $scope.currentPage = undefined;
  };


  $scope.showSupervisoryNodeSearchResults = function (resetPage) {
    $scope.searchParam = $scope.query;
    $scope.currentPage = $routeParams.page ? utils.parseIntWithBaseTen($routeParams.page) : 1;
    if(resetPage) $scope.currentPage = undefined;
    var searchOption = $scope.selectedSearchOption.value === 'parent' ? true : false;

    SupervisoryNodesSearch.get({page: $scope.currentPage, param: $scope.searchParam, parent: searchOption}, function (data) {
      $scope.supervisoryNodeList = data.supervisoryNodes;
      $scope.pagination = data.pagination;
      $scope.resultCount = $scope.pagination.totalRecords;
    }, {});
    return true;
  };

  $scope.$on('$routeUpdate', function () {
    $scope.showSupervisoryNodeSearchResults(false);
  });

  $scope.$watch('currentPage', function () {
    $location.search('page', $scope.currentPage);
  });

  $scope.clearSearch = function () {
    $scope.query = "";
    $scope.resultCount = 0;
    angular.element("#searchSupervisoryNode").focus();
  };


}