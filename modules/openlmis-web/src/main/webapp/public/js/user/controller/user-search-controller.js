/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function UserSearchController($scope, $location, Users, navigateBackService) {
  $scope.showResults = false;
  $scope.currentPage = 1;

  $scope.loadUsers = function (page, lastQuery) {
    if (!($scope.query || lastQuery)) return;
    lastQuery ? getUsers(page,lastQuery) : getUsers(page, $scope.query);
  };

  function getUsers(page, query) {
    query = query.trim();
    $scope.searchedQuery = query;
    Users.get({"searchParam": $scope.searchedQuery, "page": page}, function (data) {
      $scope.userList = data.userList;
      $scope.pagination = data.pagination;
      $scope.totalItems = $scope.pagination.totalRecords;
      $scope.currentPage = $scope.pagination.page;
      $scope.showResults = true;
    }, {});
  }

  $scope.$on('$viewContentLoaded', function () {
    $scope.query = navigateBackService.query;
  });

  $scope.triggerSearch = function (event) {
    if (event.keyCode === 13) {
      $scope.loadUsers(1);
    }
  };

  $scope.clearSearch = function () {
    $scope.query = "";
    $scope.totalItems = 0;
    $scope.userList = [];
    $scope.showResults = false;
    angular.element("#searchUser").focus();
  };

  $scope.edit = function (id) {
    var data = {query: $scope.query};
    navigateBackService.setData(data);
    $location.path('edit/' + id);
  };

  $scope.$watch('currentPage', function () {
    if ($scope.currentPage !== 0) {
      $scope.loadUsers($scope.currentPage, $scope.searchedQuery);
    }
  });
}