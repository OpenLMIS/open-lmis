/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function CreateReportController($scope) {


  $scope.$on('$viewContentLoaded', function () {
    var options = {
      beforeSubmit:validate,
      success:processResponse,
      error:processResponse
    };
    $('#reportForm').ajaxForm(options);

  });

  function validate(formData, jqForm, options) {
    $scope.showError = false;
    _.each(formData, function (input) {
      if (utils.isEmpty(input.value)) {
        $scope.$apply(function () {
          $scope.showError = true;
          return false;
        });
      }
    });

    return !$scope.showError;
  }

  function processResponse(responseText, statusText, xhr, $form) {
    var responseJson = JSON.parse(responseText);
    $scope.$apply(function () {
      $scope.successMessage = responseJson.success;
      $scope.errorMessage = responseJson.error;
    });
  }
}