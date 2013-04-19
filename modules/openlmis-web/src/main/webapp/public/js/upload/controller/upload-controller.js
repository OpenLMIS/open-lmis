/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function UploadController($scope, $routeParams, SupportedUploads) {
  $scope.model = ($routeParams.model) ? $routeParams.model : "";
  $scope.errorMsg = ($routeParams.error) ? $routeParams.error : "";
  $scope.successMsg = ($routeParams.success) ? $routeParams.success : "";

  SupportedUploads.get({}, function (data) {
    $scope.supportedUploads = data.supportedUploads;
  }, {});

}
