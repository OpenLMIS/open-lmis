/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function LoginController($scope, $http, localStorageService, messageService) {
  var HOME_PAGE = "/";
  var FORGOT_PASSWORD = "/public/pages/forgot-password.html";

  var validateLoginForm = function () {
    if ($scope.username == undefined || $scope.username.trim() == '') {
      $scope.loginError = messageService.get("error.login.username");
      return false;
    }
    if ($scope.password == undefined) {
      $scope.loginError = messageService.get("error.login.password");
      return false;
    }
    return true;
  };

  $scope.doLogin = function () {
    if (!validateLoginForm()) {
      return;
    }

    $scope.disableSignInButton = true;
    var data = $.param({j_username: $scope.username, j_password: $scope.password});
    $scope.password = undefined;
    $http({
      method: 'POST',
      url: '/j_spring_security_check',
      data: data,
      headers: {'Content-Type': 'application/x-www-form-urlencoded'}
    }).success(function (data) {
        $scope.disableSignInButton = false;
        if (data.error || !data.authenticated) {
          $scope.loginError = data.error;
          return;
        }
        localStorageService.add(localStorageKeys.RIGHT, getRights(data.rights));
        localStorageService.add(localStorageKeys.USERNAME, data.name);
        if (window.location.href.indexOf("login.html") != -1) {
          window.location = HOME_PAGE;
          return;
        }
        if (!$scope.loginConfig.preventReload) {
          location.reload();
          return;
        }
        $scope.loginConfig.modalShown = false;
        $scope.loginConfig.preventReload = false;
      }).error(function (data) {
        $scope.disableSignInButton = false;
        $scope.loginError = data.error;
      });
  };

  $scope.goToForgotPassword = function () {
    window.location = FORGOT_PASSWORD;
  };

  function getRights(rightList) {
    var rights = [];
    if (!rightList) return rights;
    $.each(rightList, function (index, right) {
      rights.push(right.right);
    });
    return rights;
  }
}