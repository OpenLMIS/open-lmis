/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

var IndexedDB = angular.module('IndexedDB', []);

IndexedDB.factory('IndexedDB', function () {

  var request = indexedDB.open("open_lmis", 1);
  var indexedDBConnection;

  request.onsuccess = function (event) {
    indexedDBConnection = event.currentTarget.result;
    console.log('IndexedDB connection open for version: ' + indexedDBConnection.version);
  }

  request.onupgradeneeded = function (event) {
    if (event.oldVersion < 2) {
      var facilityStore = db.createObjectStore("facilityData", {
        "keyPath": "distributionId"
      });

      var distributionStore = db.createObjectStore("distribution", {
        "keyPath": "id"
      });

      distributionStore.createIndex("index_zpp", ["deliverZone.id", "program.id", "period.id"], {"unique": true});

    }
  }

  return indexedDBConnection;
});
