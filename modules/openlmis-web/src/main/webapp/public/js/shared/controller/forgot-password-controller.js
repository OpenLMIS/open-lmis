/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function ForgotPasswordController($scope, ForgotPassword, messageService) {

  $scope.user = {};
  $scope.submitButtonLabel = messageService.get('button.submit');
  $scope.submitDisabled = false;
  $scope.sendForgotPasswordEmail = function () {
    if (!$scope.user.userName && !$scope.user.email) {
      $scope.error = messageService.get('enter.emailInfo');
    } else {
      $scope.submitButtonLabel = messageService.get('sending.label');
      $scope.submitDisabled = true;
      ForgotPassword.save({}, $scope.user, function () {
        window.location = "email-sent.html";
      }, function (data) {
        $scope.submitDisabled = false;
        $scope.submitButtonLabel = messageService.get('button.submit');
        $scope.error = data.data.error;
      });
    }
  };

  $scope.goToLogin = function () {
    window.location = "login.html";
  };
}
