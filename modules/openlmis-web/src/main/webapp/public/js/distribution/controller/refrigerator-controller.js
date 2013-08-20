/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function RefrigeratorController($scope, $dialog, messageService, refrigerators, distribution, IndexedDB, $routeParams) {

  $scope.refrigerators = refrigerators;
  $scope.distribution = distribution;
  $scope.selectedFacilityId = $routeParams.facility;
  $scope.edit = {};

  $scope.setEdit = function (serialNum) {
    angular.forEach($scope.edit, function (value, key) {
      $scope.edit[key] = false;
    });
    $scope.edit[serialNum] = true;
  };

  $scope.addRefrigeratorToStore = function () {
    var exists = _.find($scope.distribution.facilityDistributionData[$scope.selectedFacilityId].refrigeratorReadings, function (reading) {
      return reading.refrigerator.serialNumber.toLowerCase() === $scope.newRefrigerator.serialNumber.toLowerCase();
    });
    if (exists) {
      $scope.isDuplicateSerialNumber = true;
      return;
    }
    $scope.distribution.facilityDistributionData[$scope.selectedFacilityId].refrigeratorReadings.push({'refrigerator': angular.copy($scope.newRefrigerator)});
    IndexedDB.put('distributions', $scope.distribution);
    $scope.addRefrigeratorModal = $scope.isDuplicateSerialNumber = $scope.newRefrigerator = undefined;
  };

  $scope.showDeleteRefrigeratorConfirmationModel = function (serialNum) {
    var dialogOpts = {
      id: "deleteRefrigeratorInfo",
      header: messageService.get('delete.refrigerator.readings.header'),
      body: messageService.get('delete.refrigerator.readings.confirm')
    };
    $scope.serialNum = serialNum;
    OpenLmisDialog.newDialog(dialogOpts, $scope.deleteRefrigeratorReading, $dialog, messageService);
  };

  $scope.deleteRefrigeratorReading = function (result) {
    if (!result) return;
    $scope.distribution.facilityDistributionData[$scope.selectedFacilityId].refrigeratorReadings.splice(getRefrigeratorIndex(), 1);
    IndexedDB.put('distributions', $scope.distribution);
  };

  function getRefrigeratorIndex() {
    var position = null;
    angular.forEach($scope.distribution.facilityDistributionData[$scope.selectedFacilityId].refrigeratorReadings, function (refrigeratorReading, index) {
      if ($scope.serialNum != null && $scope.serialNum == refrigeratorReading.refrigerator.serialNumber) {
        position = index;
      }
    });
    return position;
  }

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


