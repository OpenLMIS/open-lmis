/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function RefrigeratorController($scope, $dialog, messageService, IndexedDB, $routeParams, distributionService) {


  $scope.$on('distributionReceived', function () {
    $scope.distribution = distributionService.distribution;
  })

  if ($scope.distribution == undefined) {
    $scope.distribution = distributionService.distribution;
  }

  $scope.selectedFacilityId = $routeParams.facility;
  $scope.edit = {};

  $scope.setEdit = function (serialNum) {
    angular.forEach($scope.edit, function (value, key) {
      $scope.edit[key] = false;
    });
    $scope.edit[serialNum] = true;
  };

  $scope.showRefrigeratorModal = function () {
    $scope.addRefrigeratorModal = true;
    $scope.newRefrigerator = null;
  };

  $scope.addRefrigeratorToStore = function () {
    var exists = _.find($scope.distribution.facilityDistributionData[$scope.selectedFacilityId].refrigeratorReadings,
      function (reading) {
        return reading.refrigerator.serialNumber.toLowerCase() === $scope.newRefrigerator.serialNumber.toLowerCase();
      });
    if (exists) {
      $scope.isDuplicateSerialNumber = true;
      return;
    }
    $scope.distribution.facilityDistributionData[$scope.selectedFacilityId].refrigeratorReadings.push(
      {'refrigerator': angular.copy($scope.newRefrigerator)});
    IndexedDB.put('distributions', $scope.distribution);
    $scope.addRefrigeratorModal = $scope.isDuplicateSerialNumber = undefined;
  };

  $scope.showDeleteRefrigeratorConfirmationModel = function (serialNumberToDelete) {
    var dialogOpts = {
      id: "deleteRefrigeratorInfo",
      header: messageService.get('delete.refrigerator.readings.header'),
      body: messageService.get('delete.refrigerator.readings.confirm')
    };

    var callback = function (serialNumberToDelete) {
      return function (result) {
        if (!result) return;
        $scope.distribution.facilityDistributionData[$scope.selectedFacilityId].refrigeratorReadings =
          _.reject($scope.distribution.facilityDistributionData[$scope.selectedFacilityId].refrigeratorReadings,
            function (refrigeratorReading) {
              return serialNumberToDelete == refrigeratorReading.refrigerator.serialNumber;
            });
        IndexedDB.put('distributions', $scope.distribution);
      };
    };

    OpenLmisDialog.newDialog(dialogOpts, callback(serialNumberToDelete), $dialog, messageService);
  };

}



