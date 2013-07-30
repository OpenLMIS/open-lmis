/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
function DistributionListController($scope, $location, IndexedDB, SharedDistributions) {

  $scope.sharedDistributions = SharedDistributions;

  if (IndexedDB.getConnection() == null) {
    $scope.$on('indexedDBReady', function () {
      $scope.sharedDistributions.update();
    });
  }
  else {
    $scope.sharedDistributions.update();
  }

  $scope.recordFacilityData = function (zpp) {
    $location.path("/record-facility-data/" + zpp);
  }

}

