/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

angular.module('IndexedDB', []).service('IndexedDB', function ($rootScope) {

  var request = indexedDB.open("open_lmis", 2);
  var indexedDBConnection = null;

  request.onsuccess = function (event) {
    indexedDBConnection = event.currentTarget.result;
    $rootScope.$broadcast('indexedDBReady', indexedDBConnection.version);
  }

  request.onupgradeneeded = function (event) {
    indexedDBConnection = event.currentTarget.result;

    var dropIfExist = function (storeName) {
      if (indexedDBConnection.objectStoreNames.contains(storeName)) {
        indexedDBConnection.deleteObjectStore(storeName);
      }
    }

    if (event.oldVersion < 2) {
      createDistributionStore();
      createDistributionReferenceData();
    }

    function createDistributionStore() {
      dropIfExist("distributions");
      var distributionStore = indexedDBConnection.createObjectStore("distributions", {"keyPath": "id"});
      distributionStore.createIndex("index_zpp", "zpp", {"unique": true});
    }

    function createDistributionReferenceData() {
      dropIfExist("distributionReferenceData");
      indexedDBConnection.createObjectStore("distributionReferenceData", {"keyPath": "id"});
    }
  };

  this.getConnection = function () {
    return indexedDBConnection;
  }

});
