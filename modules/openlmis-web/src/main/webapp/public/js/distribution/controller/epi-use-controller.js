/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function EPIUseController($scope, $routeParams, distributionService) {
  $scope.distribution = distributionService.distribution;
  $scope.inputClass = "warning-error";
  $scope.selectedFacilityId = $routeParams.facility;
}

function EpiUseRowController($scope) {
  $scope.getTotal = function () {
    if (!$scope.groupReading || !$scope.groupReading.reading) {
      return 0;
    }
    return getValue($scope.groupReading.reading.stockAtFirstOfMonth) + getValue($scope.groupReading.reading.received);
  };

  $scope.clearError = function (notRecorded) {
    $scope.inputClass = notRecorded ? true : "warning-error";
  };

  var getValue = function (object) {
    return (!isUndefined(object) && !isUndefined(object.value)) ? parseInt(object.value, 10) : 0;
  }
}
