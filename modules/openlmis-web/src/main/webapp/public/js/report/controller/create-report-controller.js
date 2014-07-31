/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function CreateReportController($scope, $location, loginConfig, $window) {
  $scope.$parent.successMessage = "";

  var successHandler = function (data) {
    $scope.$parent.successMessage = data.success;
    $scope.$parent.$error = "";
    $location.path('list');
  };

  $scope.$on('$viewContentLoaded', function () {
    var options = {
      beforeSubmit: validate,
      success: processResponse,
      error: failureHandler
    };
    $('#reportForm').ajaxForm(options);
  });

  function validate(formData) {
    $scope.showError = false;
    _.each(formData, function (input) {
      $scope.$apply(function () {
        if (input.name !== "description" && utils.isEmpty(input.value)) {
          $scope.showError = true;
          $scope.reportForm[input.name].$error.required = true;
        } else {
          $scope.reportForm[input.name].$error.required = false;
        }
      });
    });

    return !$scope.showError;
  }

  function processResponse(responseText) {
    var responseJson = JSON.parse(responseText);
    $scope.$apply(function () {
      if (responseJson.success) successHandler(responseJson);
      $scope.errorMessage = responseJson.error;
    });
  }

  function failureHandler(response) {
    $scope.$apply(function () {
      if (response.status == 401) {
        loginConfig.modalShown = loginConfig.preventReload = true;
      }
      if (response.status == 403) {
        $window.location = "/public/pages/access-denied.html";
      }
    });
  }

}