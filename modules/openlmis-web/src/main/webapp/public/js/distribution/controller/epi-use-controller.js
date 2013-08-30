/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function EPIUseController($scope, $routeParams, distributionService) {

  $scope.inputClass = "warning-error";
  $scope.selectedFacilityId = $routeParams.facility;

  $scope.$on('distributionReceived', function () {
    $scope.distribution = distributionService.distribution;
  });

  if ($scope.distribution == undefined && distributionService.distribution != undefined) {
    $scope.distribution = distributionService.distribution;
  }

};



function EpiUseRowController($scope) {
  $scope.getTotal = function() {
    if(isUndefined($scope.groupReading.reading)) {
      return 0;
    }
    return getValue($scope.groupReading.reading.stockAtFirstOfMonth) + getValue($scope.groupReading.reading.received);
  }

  $scope.clearError = function(notRecorded){
    if(notRecorded){
      $scope.inputClass = true;
    } else {
      $scope.inputClass = "warning-error";
    }
  }

  var getValue = function(object) {
    return (!isUndefined(object) && !isUndefined(object.value)) ? parseInt(object.value, 10) : 0;
  }
}
