/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function RefrigeratorController(IndexedDB, $scope, $route) {


  $scope.closeRefrigeratorModal = function() {
    $scope.addRefrigeratorModal = false;
    $scope.newRefrigerator = undefined;
  }


  $scope.addRefrigerator = function (rowNum, roleList) {
    $scope.addRefrigeratorModal = true;
  };


  function fetchReferenceData() {
    var zpp = $route.current.params.zpp;
    IndexedDB.transaction(function (connection) {
      var transaction = connection.transaction(['distributionReferenceData', 'distributions']);
      var request = transaction.objectStore('distributionReferenceData').get(zpp);
      var by_zpp = transaction.objectStore('distributions').index('index_zpp');
      request.onsuccess = function (event) {
        $scope.refrigerators = request.result.refrigerators;
        $scope.$apply();
      }
    });
  }

  fetchReferenceData();

}


