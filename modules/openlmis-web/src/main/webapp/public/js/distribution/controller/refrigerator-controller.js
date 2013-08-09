/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function RefrigeratorController(IndexedDB, $scope, $route) {

  $scope.refrigeratorReading = null;

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

  $scope.updateProblems = function () {
    /* var problems = $scope.refrigeratorReading.problems == undefined ? [] : $scope.refrigeratorReading.problems ;
     if ($(this).is(":checked")) {
     problems.push()
     }
     */
  };

  function fetchReferenceData() {
    var zpp = $route.current.params.zpp;
    IndexedDB.transaction(function (connection) {
      var transaction = connection.transaction(['distributionReferenceData', 'distributions']);
      var distributionReferenceDataRequest = transaction.objectStore('distributionReferenceData').get(zpp);
      var by_zpp = transaction.objectStore('distributions').index('index_zpp');
      by_zpp.get(zpp).onsuccess = function (event) {
        $scope.distribution = event.target.result;
        $scope.$apply();
      };
      distributionReferenceDataRequest.onsuccess = function (event) {
        $scope.refrigerators = distributionReferenceDataRequest.result.refrigerators;
        $scope.refrigeratorReadings = distributionReferenceDataRequest.result.refrigeratorReadings;
        $scope.$apply();
      }
    });
  }

  fetchReferenceData();

}


