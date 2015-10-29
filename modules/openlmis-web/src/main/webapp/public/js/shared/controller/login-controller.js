/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function LoginController($scope, $http, localStorageService, messageService) {
  var FORGOT_PASSWORD = "/public/pages/forgot-password.html";

  var validateLoginForm = function () {
    if ($scope.username === undefined || $scope.username.trim() === '') {
      $scope.loginError = messageService.get("error.login.username");
      return false;
    }
    if ($scope.password === undefined) {
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
      localStorageService.add(localStorageKeys.USER_ID, data.userId);

      for (var prefKey in data.preferences) {
        localStorageService.add(prefKey, data.preferences[prefKey]);
      }
      
      if (window.location.href.indexOf("login.html") != -1) {
        window.location = data.homePage;
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
    $.each(rightList, function (index,right) {
      rights.push({name: right.name, type: right.type});
    });
    return JSON.stringify(rights);
  }
}