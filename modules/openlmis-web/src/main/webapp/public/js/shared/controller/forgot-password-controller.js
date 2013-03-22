/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ForgotPasswordController($scope, ForgotPassword) {

  $scope.user = {};
  $scope.submitButtonLabel = "Submit";
  $scope.submitDisabled = false;
    $scope.sendForgotPasswordEmail = function(){
    if (!$scope.user.userName && !$scope.user.email) {
           $scope.error="Please enter either your Email or Username";
    } else {
      $scope.submitButtonLabel = "Sending...";
      $scope.submitDisabled = true;
      ForgotPassword.save({}, $scope.user, function () {
        window.location = "email-sent.html";
      }, function (data) {
        $scope.submitDisabled = false;
        $scope.submitButtonLabel = "Submit";
        $scope.error = data.data.error;
      });
      }
    }

}
