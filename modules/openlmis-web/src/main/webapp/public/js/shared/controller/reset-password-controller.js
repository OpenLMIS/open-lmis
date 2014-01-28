/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function ResetPasswordController($scope, UpdateUserPassword, $location, $route, tokenValid, messageService) {
  if (!tokenValid) {
    window.location = 'access-denied.html';
  }

  $scope.resetPassword = function () {
    var reWhiteSpace = new RegExp("\\s");
    var digits = new RegExp("\\d");
    if ($scope.password1.length < 8 || $scope.password1.length > 16 || !digits.test($scope.password1) ||
      reWhiteSpace.test($scope.password1)) {
      $scope.error = messageService.get("error.password.invalid");
      return;
    }
    if ($scope.password1 !== $scope.password2) {
      $scope.error = messageService.get('error.password.mismatch');
      return;
    }

    UpdateUserPassword.update({token: $route.current.params.token}, $scope.password1, function (data) {
      $location.path('/reset/password/complete');
    }, function (data) {
      window.location = 'access-denied.html';
    });
  };
}

function ValidateTokenController() {
}

function ResetCompleteController($scope) {

  $scope.goToLoginPage = function () {
    window.location = 'login.html';
  };

}

ValidateTokenController.resolve = {

  tokenValid: function ($q, $timeout, ValidatePasswordToken, $route, $location) {
    var deferred = $q.defer();
    $timeout(function () {
      ValidatePasswordToken.get({token: $route.current.params.token }, function (data) {
        $location.path('/reset/' + $route.current.params.token);
      }, function (data) {
        window.location = 'access-denied.html';
      });
    }, 100);
    return deferred.promise;
  }
};

ResetPasswordController.resolve = {
  tokenValid: function ($q, $timeout, ValidatePasswordToken, $route) {
    var deferred = $q.defer();
    $timeout(function () {
      ValidatePasswordToken.get({token: $route.current.params.token }, function (data) {
        deferred.resolve(data.TOKEN_VALID);
      }, function () {
        window.location = 'access-denied.html';
      });
    }, 100);
    return deferred.promise;
  }
};