/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function RefrigeratorController($scope, refrigerators, distribution) {

  $scope.refrigerators = refrigerators;
  $scope.distribution = distribution;

  $scope.closeRefrigeratorModal = function () {
    $scope.addRefrigeratorModal = false;
    $scope.newRefrigerator = undefined;
  }

  $scope.showAddRefrigeratorModal = function () {
    $scope.addRefrigeratorModal = true;
    $scope.newRefrigerator = null;
  };

  $scope.editRefrigerator = function (refrigerator) {
    $scope.edit = true;
    $scope.refrigeratorReading = _.where($scope.refrigeratorReadings, {
      facilityId: refrigerator.facilityId,
      distributionId: $scope.distribution.id,
      refrigeratorSerialNumber: refrigerator.serialNumber
    })[0];
  };

  $scope.closeRefrigeratorEdit = function () {
    $scope.edit = false;
  };


  $scope.deleteOtherProblems = function (refrigeratorReading) {
    if (refrigeratorReading.problemSinceLastTimed || !refrigeratorReading.problems) return;

    for (var key in refrigeratorReading.problems.problemMap) {
      refrigeratorReading.problems.problemMap[key] = undefined
    }
    refrigeratorReading.problems.other = undefined;
  };

}

RefrigeratorController.resolve = {

  refrigerators: function ($q, IndexedDB, $route) {
    var waitOn = $q.defer();
    var distributionId = $route.current.params.distribution;
    var facilityId = $route.current.params.facility;

    IndexedDB.get('distributionReferenceData', utils.parseIntWithBaseTen(distributionId), function (event) {
      waitOn.resolve(_.where(event.target.result.refrigerators, {facilityId: utils.parseIntWithBaseTen(facilityId)}));
    }, {});

    return waitOn.promise;
  },

  distribution: function ($q, IndexedDB, $route) {
    var waitOn = $q.defer();
    IndexedDB.get('distributions', utils.parseIntWithBaseTen($route.current.params.distribution), function (e) {
      waitOn.resolve(e.target.result);
    }, {});

    return waitOn.promise;
  }
};


