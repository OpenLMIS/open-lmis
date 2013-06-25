/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function UploadController($scope, SupportedUploads, messageService) {

  SupportedUploads.get({}, function (data) {
    $scope.supportedUploads = data.supportedUploads;
  }, {});

  $scope.$on('$viewContentLoaded', function () {
    var options = {
      beforeSubmit: $scope.validate,
      success: processResponse,
      error: failureHandler
    };
    $('#uploadForm').ajaxForm(options);
  });

  $scope.validate = function (formData) {
    $scope.$apply(function () {
      $scope.inProgress = true;
      $scope.successMsg = $scope.errorMsg = "";
      if (setErrorMessageIfEmpty(formData[0].value, 'model', 'upload.select.type')) {
        $scope.inProgress = false;
      }
      if (setErrorMessageIfEmpty(formData[1].value, 'csvFile', 'upload.select.file')) {
        $scope.inProgress = false;
      }
    });
    return $scope.inProgress;
  };

  function setErrorMessageIfEmpty(value, fieldName, messageKey) {
    if (utils.isEmpty(value)) {
      $scope.uploadForm[fieldName].errorMessage = messageService.get(messageKey);
      return true;
    } else {
      $scope.uploadForm[fieldName].errorMessage = "";
      return false;
    }
  }

  var failureHandler = function (response) {
    var errorMessage = JSON.parse(response.responseText).error;
    $scope.$apply(function () {
      $scope.errorMsg = errorMessage;
      $scope.inProgress = false;
    });
  }

  function processResponse(responseText) {
    var response = JSON.parse(responseText);
    $scope.$apply(function () {
      if (response.success) {
        successHandler(response);
      }

      if (response.error) {
        $scope.successMsg = "";
        $scope.errorMsg = response.error;
      }

      $scope.inProgress = false;
    });
  }

  var successHandler = function (data) {
    $scope.successMsg = data.success;
    $scope.errorMsg = "";
  };

}
