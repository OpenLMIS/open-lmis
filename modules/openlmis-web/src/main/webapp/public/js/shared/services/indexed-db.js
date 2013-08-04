/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

angular.module('IndexedDB', []).service('IndexedDB', function ($rootScope, $q) {

  var request = indexedDB.open("open_lmis", 2);
  var indexedDBConnection = null;
  var deferred = $q.defer();

  request.onsuccess = function (event) {
    indexedDBConnection = event.currentTarget.result;
    deferred.resolve();
    $rootScope.$apply();
  }

  request.onupgradeneeded = function (event) {
    var connection = event.currentTarget.result;

    var dropIfExist = function (storeName) {
      if (connection.objectStoreNames.contains(storeName)) {
        connection.deleteObjectStore(storeName);
      }
    }

    if (!event.oldVersion || event.oldVersion < 2) {
      createDistributionStore();
      createDistributionReferenceData();
    }

    function createDistributionStore() {
      dropIfExist("distributions");
      var distributionStore = connection.createObjectStore("distributions", {"keyPath": "id"});
      distributionStore.createIndex("index_zpp", "zpp", {"unique": true});
    }

    function createDistributionReferenceData() {
      dropIfExist("distributionReferenceData");
      var distributionReferenceDataStore = connection.createObjectStore("distributionReferenceData", {"keyPath": "zpp"});
      distributionReferenceDataStore.createIndex("index_reference_data", "zpp", {"unique": true});
    }
  };

  this.getConnection = function () {
    return indexedDBConnection;
  }

  this.transaction = function (transactionFunction) {
    if (indexedDBConnection === null) {
      deferred.promise.then(function () {
        transactionFunction(indexedDBConnection);
      });
    } else {
      transactionFunction(indexedDBConnection);
    }
  }

});
