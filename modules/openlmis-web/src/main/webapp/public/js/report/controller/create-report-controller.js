/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function CreateReportController($scope, $location) {
  $scope.$parent.successMessage = "";

  var successHandler = function (data) {
    $scope.$parent.successMessage = data.success;
    $scope.$parent.$error = "";
    $location.path('list');
  };


  $scope.$on('$viewContentLoaded', function () {
    var options = {
      beforeSubmit:validate,
      success:processResponse
    };
    $('#reportForm').ajaxForm(options);

  });

  function validate(formData, jqForm, options) {
    $scope.showError = false;
    _.each(formData, function (input) {
      $scope.$apply(function () {
        if (utils.isEmpty(input.value)) {
          $scope.showError = true;
          $scope.reportForm[input.name].$error.required = true;
        } else {
          $scope.reportForm[input.name].$error.required = false;
        }
      });
    });

    return !$scope.showError;
  }

  function processResponse(responseText, statusText, xhr, $form) {
    var responseJson = JSON.parse(responseText);
    $scope.$apply(function () {
      if (responseJson.success) successHandler(responseJson);
      $scope.errorMessage = responseJson.error;
    });
  }

}