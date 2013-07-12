/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

angular.module('IndexedDB', []).provider('IndexedDB', function () {

  var request = indexedDB.open("open_lmis", 1);
  var indexedDBConnection = null;

  request.onsuccess = function (event) {
    indexedDBConnection = event.currentTarget.result;
    console.log('IndexedDB connection open for version: ' + indexedDBConnection.version);
  }

  request.onupgradeneeded = function (event) {
    indexedDBConnection = event.currentTarget.result;

    var createDistributionStore = function () {
      var distributionStore = indexedDBConnection.createObjectStore("distributions", {"keyPath": "id"});
      distributionStore.createIndex("index_zpp", "zpp", {"unique": true});
    }

    var createDistributionReferenceData = function () {
      indexedDBConnection.createObjectStore("distributionReferenceData", {"keyPath": "id"});
    }

    if (event.oldVersion < 2) {
      createDistributionStore();
      createDistributionReferenceData();
    }
  };

  this.$get = function () {
    return indexedDBConnection;
  };
});
