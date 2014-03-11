/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function UserSearchController($scope, $location, Users, navigateBackService, UpdatePassword, messageService) {
  $scope.showUserSearchResults = function () {
    var query = $scope.query;

    var len = (query === undefined) ? 0 : query.length;

    if (len >= 3) {
      if ($scope.previousQuery.substr(0, 3) === query.substr(0, 3)) {
        $scope.previousQuery = query;
        filterUserByName(query);
        return true;
      }
      $scope.previousQuery = query;
      Users.get({param: $scope.query.substr(0, 3)}, function (data) {
        $scope.userList = data.userList;
        filterUserByName(query);
      }, {});

      return true;
    } else {
      return false;
    }
  };

  $scope.previousQuery = '';
  $scope.query = navigateBackService.query;
  $scope.showUserSearchResults();


  $scope.editUser = function (id) {
    var data = {query: $scope.query};
    navigateBackService.setData(data);
    $location.path('edit/' + id);
  };

  $scope.changePassword = function (user) {
    if (user.active) {
      $scope.userId = undefined;
      $scope.password1 = $scope.password2 = $scope.message = $scope.error = "";
      $scope.changePasswordModal = true;
      $scope.user = user;
    }
  };

  $scope.updatePassword = function () {
    var reWhiteSpace = new RegExp("\\s");
    var digits = new RegExp("\\d");
    if ($scope.password1.length < 8 || $scope.password1.length > 16 || !digits.test($scope.password1) ||
        reWhiteSpace.test($scope.password1)) {
      $scope.error = messageService.get("error.password.invalid");
      return;
    }

    if ($scope.password1 != $scope.password2) {
      $scope.error = messageService.get('error.password.mismatch');
      return;
    }

    UpdatePassword.update({userId: $scope.user.id}, $scope.password1, function (data) {
      $scope.message = data.success;
    }, {});
  };

  $scope.resetPasswordModal = function () {
    $scope.changePasswordModal = false;
    $scope.user = undefined;
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
          user.email.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0 ||
          user.userName.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0) {
        $scope.filteredUsers.push(user);
      }
    });
    $scope.resultCount = $scope.filteredUsers.length;
  };
}