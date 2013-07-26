/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
function DistributionListController($scope, $location, IndexedDB) {

  var loadDistributionsFromCache = function () {
    var transaction = IndexedDB.getConnection().transaction('distributions');
    var cursorRequest = transaction.objectStore('distributions').openCursor();

    var aggregate = [];
    cursorRequest.onsuccess = function (event) {
      if (event.target.result) {
        aggregate.push(event.target.result.value);
        event.target.result['continue']();
      }
    };

    transaction.oncomplete = function (e) {
      $scope.$parent.distributionList = aggregate;
      $scope.$apply();
    };
  };

  if (IndexedDB.getConnection() == null) {
    $scope.$on('indexedDBReady', function () {
      loadDistributionsFromCache();
    });
  }
  else {
    loadDistributionsFromCache();
  }

  $scope.$on('distributionAdded', function () {
    loadDistributionsFromCache();
  });


  $scope.recordFacilityData = function (zpp) {
    $location.path("/record-facility-data/" + zpp);
  }

}

