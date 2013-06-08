/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function UserSearchController($scope, $location, Users, navigateBackService) {
  $scope.$on('$viewContentLoaded', function() {
    $scope.$apply($scope.query = navigateBackService.query);
    $scope.showUserSearchResults('searchUser');
  });
  $scope.previousQuery = '';

  $scope.showUserSearchResults = function (id) {
    var query = document.getElementById(id).value;
    $scope.query = query;

    var len = (query == undefined) ? 0 : query.length;

    if (len >= 3) {
      if ($scope.previousQuery.substr(0, 3) == query.substr(0, 3)) {
        $scope.previousQuery = query;
        filterUserByName(query);
        return true;
      }
      $scope.previousQuery = query;
      Users.get({param:$scope.query.substr(0, 3)}, function (data) {
        $scope.userList = data.userList;
        filterUserByName(query);
      }, {});

      return true;
    } else {
      return false;
    }
  };

  $scope.editUser = function (id) {
    var data = {query: $scope.query};
    navigateBackService.setData(data);
    $location.path('edit/' + id);
  };


  $scope.clearSearch = function () {
    $scope.query = "";
    $scope.resultCount = 0;
    angular.element("#searchUser").focus();
  };

  var filterUserByName = function (query) {
    $scope.filteredUsers = [];
    query = query || "";

    angular.forEach($scope.userList, function (user) {
      var fullName = user.firstName.toLowerCase() + ' ' + user.lastName.toLowerCase();

      if (user.firstName.toLowerCase().indexOf() >= 0 ||
          user.lastName.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0 ||
          fullName.indexOf(query.trim().toLowerCase()) >= 0 ||
          user.email.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0
          ) {
        $scope.filteredUsers.push(user);
      }
    });
    $scope.resultCount = $scope.filteredUsers.length;
  };
}