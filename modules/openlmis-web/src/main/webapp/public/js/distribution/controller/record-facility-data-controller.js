/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function RecordFacilityDataController(IndexedDB, $scope, $route) {

  function fetchReferenceData() {
    var zpp = $route.current.params.zpp;
    var connection = IndexedDB.getConnection();
    var distributionReferenceDataTransaction = connection.transaction('distributions');
    var distributionsTransaction = connection.transaction(['distributionReferenceData']);
    var request = distributionsTransaction.objectStore('distributionReferenceData').get(zpp);
    var by_zpp = distributionReferenceDataTransaction.objectStore('distributions').index('index_zpp');
    by_zpp.get(zpp).onsuccess = function (event) {
      var result = event.target.result;
      $scope.deliveryZoneName = result.deliveryZone.name;
      $scope.programName = result.program.name;
      $scope.periodName = result.period.name;
      $scope.$apply();
    }
    request.onsuccess = function (event) {
      $scope.facilityList = request.result.facilities;
      $scope.$apply();
    }
  }

  $scope.$on('indexedDBReady', function () {
    fetchReferenceData();
  })
};




