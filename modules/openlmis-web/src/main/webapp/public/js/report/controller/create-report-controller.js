/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function CreateReportController($scope) {

  $scope.$on('$viewContentLoaded', function () {
    var options = {
      target:'#output1',
      beforeSubmit:validate,
      success:showResponse,
      error: showResponse
    };
    $('#reportForm').ajaxForm(options);

  });
  function validate(formData, jqForm, options) {
    var queryString = $.param(formData);
    alert('About to submit: \n\n' + queryString);
    return true;
  }

  function showResponse(responseText, statusText, xhr, $form) {
    alert('status: ' + statusText + '\n\nresponseText: \n' + responseText+
      '\n\nThe output div should have already been updated with the responseText.');
  }
}