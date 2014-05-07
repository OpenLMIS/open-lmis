/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function SupervisoryNodeSearchController($scope, $location, SupervisoryNodesSearch) {

  $scope.previousQuery = '';
  $scope.previousSearchOption = undefined;

  $scope.searchOptions = [
    {value: "node", name: "option.value.supervisory.node"},
    {value: "parent", name: "option.value.supervisory.node.parent"}
  ];

  $scope.selectedSearchOption = $scope.searchOptions[0];

  $scope.selectSearchType = function (searchOption) {
    $scope.selectedSearchOption = searchOption;
    $scope.currentPage = undefined;
    var searchOption = $scope.selectedSearchOption.value === 'parent' ? true : false;
    if($scope.previousSearchOption !== searchOption) $scope.previousQuery = '';
    $scope.showSupervisoryNodeSearchResults();

  };


  $scope.showSupervisoryNodeSearchResults = function () {
    if (!$scope.currentPage)
      $scope.currentPage = 1;
    var query = $scope.query;

    var len = (query === undefined) ? 0 : query.length;

    var searchOption = $scope.selectedSearchOption.value === 'parent' ? true : false;

    var page = $scope.pagination ? $scope.pagination.page : undefined;
    if (len >= 3) {
      if ($scope.previousQuery.substr(0, 3) === query.substr(0, 3) && $scope.currentPage === page) {
        $scope.previousQuery = query;
        filterSupervisoryNode(query);
        return true;
      }
      $scope.previousQuery = query;
      $scope.previousSearchOption = searchOption;
      SupervisoryNodesSearch.get({page: $scope.currentPage, param: $scope.query.substr(0, 3), parent: searchOption}, function (data) {
        $scope.supervisoryNodeList = data.supervisoryNodes;
        $scope.pagination = data.pagination;
        filterSupervisoryNode(query);
      }, {});

      return true;
    } else {
      return false;
    }
  };
  $scope.$on('$routeUpdate', function () {
      $scope.showSupervisoryNodeSearchResults();
  });

  $scope.$watch('currentPage', function () {
    $location.search('page', $scope.currentPage);
  });

  $scope.clearSearch = function () {
    $scope.query = "";
    $scope.resultCount = 0;
    angular.element("#searchSupervisoryNode").focus();
  };


  var filterSupervisoryNode = function (query) {
    $scope.filteredNodes = [];
    query = query || "";

    angular.forEach($scope.supervisoryNodeList, function (supervisoryNode) {
      var name = $scope.parent ? supervisoryNode.parent.name : supervisoryNode.name;

      if (name.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0) {
        $scope.filteredNodes.push(supervisoryNode);
      }
    });
    $scope.resultCount = $scope.filteredNodes.length;
  };
}