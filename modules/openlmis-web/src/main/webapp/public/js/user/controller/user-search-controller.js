/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function UserSearchController($scope, $location, Users, navigateBackService, UpdatePassword, messageService) {

    // show the list of users by a deault
    Users.get({param: ''}, function(data){
       $scope.users = data.userList;
    });

    $scope.showUserSearchResults = function () {
    var query = $scope.query;

    var len = (query == undefined) ? 0 : query.length;

    if (len >= 3) {
      if ($scope.previousQuery.substr(0, 3) == query.substr(0, 3)) {
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
    $scope.userId = undefined;
    $scope.password1 = $scope.password2 = $scope.message = $scope.error = "";
    $scope.changePasswordModal = true;
    $scope.user = user;
  };

  $scope.updatePassword = function () {
    var reWhiteSpace = new RegExp("\\s");
    var digits = new RegExp("\\d");
    if ($scope.password1.length < 8 || $scope.password1.length > 16 || !digits.test($scope.password1) || reWhiteSpace.test($scope.password1)) {
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

  }

  $scope.resetPasswordModal = function () {
    $scope.changePasswordModal = false;
    $scope.user = undefined;
  }

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
        user.userName.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0
        ) {
        $scope.filteredUsers.push(user);
      }
    });
    $scope.resultCount = $scope.filteredUsers.length;
  };

  setTimeout(function() {
    angular.element(".user-list a").live("focus", function() {
      $(".user-actions a").hide();
      $(this).parents("li").find(".user-actions a").css("display", "inline-block");
    });
  });

  $scope.YesNo = function (tf) {
    return (tf == true)? "Yes" : "No";
  };

}