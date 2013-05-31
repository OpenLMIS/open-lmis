/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function LoginController($scope, $http, localStorageService, messageService) {
  $scope.disableSignInButton = false;

  var validateLoginForm = function () {
    if($scope.username == undefined || $scope.username.trim()==''){
      $scope.loginError = messageService.get("error.login.username");
      return false;
    }
    if($scope.password == undefined) {
      $scope.loginError = messageService.get("error.login.password");
      return false;
    }
    return true;
  };

  $scope.doLogin = function () {
    if(!validateLoginForm()){
      return;
    }

    $scope.disableSignInButton = true;
    var data = $.param({j_username:$scope.username, j_password:$scope.password});
    $http({
      method:'POST',
      url:'/j_spring_security_check',
      data:data,
      headers:{'Content-Type':'application/x-www-form-urlencoded'}
    }).success(function (data) {
          $scope.disableSignInButton = false;
          if (data.authenticated) {
            localStorageService.add(localStorageKeys.RIGHT, getRights(data.rights));
            localStorageService.add(localStorageKeys.USERNAME, data.name);
            if (window.location.href.indexOf("login.html") != -1) {
              window.location = "/";
            } else {
              location.reload();
            }
          } else if (data.error) {
            $scope.loginError = data.error;
          }
        }).
        error(function (data) {
          $scope.disableSignInButton = false;
          $scope.loginError = data.error;
        });
  };

  $scope.goToForgotPassword = function () {
    window.location = "/public/pages/forgot-password.html";
  }

  function getRights(rightList) {
    var rights = [];
    if(!rightList) return rights;
    $.each(rightList, function (index, right) {
      rights.push(right.right);
    });
    return rights;
  }
}