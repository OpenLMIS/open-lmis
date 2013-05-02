/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function CreateReportController($scope) {


  $scope.$on('$viewContentLoaded', function () {
    var options = {
      beforeSubmit:validate,
      success:showResponse,
      error:showResponse
    };
    $('#reportForm').ajaxForm(options);

  });
  function validate(formData, jqForm, options) {
    var queryString = $.param(formData);
    return true;
  }

  function showResponse(responseText, statusText, xhr, $form) {
    var responseJson = JSON.parse(responseText) ;
    $scope.$apply(function () {
      $scope.errorMessage = responseJson.error;
      $scope.successMessage = responseJson.success;
    });
 }
}