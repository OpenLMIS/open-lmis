/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 * + *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function HelpUploadController($scope, SupportedUploads,$location, messageService, loginConfig) {

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

  $scope.getMessage = function (key) {
    return messageService.get(key);
  };

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
    $scope.$apply(function () {
      if (response.status == 401) {
        loginConfig.modalShown = loginConfig.preventReload = true;
      }
      else {
        try {
          $scope.errorMsg = JSON.parse(response.responseText).error;
        } catch (e) {
          $scope.errorMsg = messageService.get('error.upload.network.server.down');
          $scope.inProgress = false;
        }
      }
      $scope.inProgress = false;
    });
  };

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
    $scope.cancelUpload=function(){

        $location.path('/treeView');
    };
}
