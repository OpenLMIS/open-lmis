/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

angular.module('IndexedDB', []).service('IndexedDB', function ($rootScope, $q) {

  var request = indexedDB.open("open_lmis", 4);
  var indexedDBConnection = null;
  var deferred = $q.defer();
  var thisService = this;

  request.onsuccess = function (event) {
    indexedDBConnection = event.currentTarget.result;
    deferred.resolve();
    $rootScope.$apply();
  };

  request.onupgradeneeded = function (event) {
    var connection = event.currentTarget.result;

    var dropDatastores = function () {
      $(connection.objectStoreNames).each(function (index, objectStore) {
        connection.deleteObjectStore(objectStore);
      });
    };

    if (!event.oldVersion || event.oldVersion < 3) {
      dropDatastores();
      createDistributionStore();
      createDistributionReferenceData();
    }

    function createDistributionStore() {
      var distributionStore = connection.createObjectStore("distributions", {"keyPath": "id"});
      distributionStore.createIndex("index_zpp", "zpp", {"unique": true});
    }

    function createDistributionReferenceData() {
      var distributionReferenceDataStore = connection.createObjectStore("distributionReferenceData", {"keyPath": "distributionId"});
      distributionReferenceDataStore.createIndex("index_reference_data", "distributionId", {"unique": true});
    }
  };

  this.transaction = function (transactionFunction) {
    deferred.promise.then(function () {
      transactionFunction(indexedDBConnection);
    });
  };


  this.get = function (objectStore, operationKey, successFunc, errorFunc) {
    deferred.promise.then(function () {
      thisService.transaction(function (connection) {
          var transaction = connection.transaction(objectStore);
          transaction.oncomplete = function () {
            if (!$rootScope.$$phase) $rootScope.$apply();
          };
          var request = transaction.objectStore(objectStore).get(operationKey);
          request.onsuccess = function (e) {
            successFunc(e);
          };
          request.onerror = function (e) {
            console.log(e);
            errorFunc(e)
          };
        }
      )
    });
  };

  this.put = function (objectStore, data, successFunc, errorFunc, completeFunc) {
    deferred.promise.then(function () {
      thisService.transaction(function (connection) {
          var transaction = connection.transaction(objectStore, 'readwrite');
          transaction.oncomplete = function (e) {
            if (completeFunc) {
              completeFunc(e);
            }
            if (!$rootScope.$$phase) $rootScope.$apply();
          };

          var request = transaction.objectStore(objectStore).put(data);
          request.onsuccess = function (e) {
            successFunc(e);
          };
          request.onerror = function (e) {
            console.log(e);
            errorFunc(e)
          };

        }
      )
    });
  }

});
