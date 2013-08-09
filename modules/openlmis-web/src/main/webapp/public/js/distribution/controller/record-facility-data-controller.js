/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function RecordFacilityDataController(IndexedDB, $scope, $route) {


  $scope.format = function (facility) {
    if (facility.id) {
      return "<div class='is-empty'>" +
        "<span class='status-icon'></span>" + facility.text +
        "</div>";
    } else {
      return facility.text;
    }
  }

  function fetchReferenceData() {
    var zpp = $route.current.params.zpp;
    IndexedDB.transaction(function (connection) {
      var transaction = connection.transaction(['distributionReferenceData', 'distributions']);
      var request = transaction.objectStore('distributionReferenceData').get(zpp);
      var by_zpp = transaction.objectStore('distributions').index('index_zpp');
      by_zpp.get(zpp).onsuccess = function (event) {
        var result = event.target.result;
        $scope.deliveryZoneName = result.deliveryZone.name;
        $scope.programName = result.program.name;
        $scope.periodName = result.period.name;
        $scope.$apply();
      };
      request.onsuccess = function (event) {
        $scope.facilityList = request.result.facilities;
        $scope.$apply();
      }
    });
  }

  fetchReferenceData();
}




