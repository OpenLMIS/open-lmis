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
      beforeSubmit: this.validate,
      success:processResponse
    };
    $('#uploadForm').ajaxForm(options);
  });

  UploadController.prototype.validate = function(formData, jqForm, options) {
    $scope.$apply(function () {
      setErrorMessageIfEmpty(formData[0].value, 'model', 'upload.select.type');
      setErrorMessageIfEmpty(formData[1].value, 'csvFile', 'upload.select.file');
    });
  };

  function setErrorMessageIfEmpty(value, fieldName, messageKey) {
    if (utils.isEmpty(value)) {
      $scope.uploadForm[fieldName].errorMessage = messageService.get(messageKey);
    } else {
      $scope.uploadForm[fieldName].errorMessage = "";
    }
  }

  function processResponse(responseText, statusText, xhr, $form) {
    var response = JSON.parse(responseText);
    $scope.$apply(function () {
      if (response.success) {
        successHandler(response);
      }

      if (response.error) {
        $scope.successMsg = "";
        $scope.errorMsg = response.error;
      }

      $scope.model = response.model;
    });
  }
  var successHandler = function (data) {
    $scope.successMsg = data.success;
    $scope.errorMsg = "";
  };

}
