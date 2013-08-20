/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

var migrationFunc = function (event) {
  var connection = event.currentTarget.result;

  var dropDatastores = function () {
    $(connection.objectStoreNames).each(function (index, objectStore) {
      connection.deleteObjectStore(objectStore);
    });
  };

  if (!event.oldVersion || event.oldVersion < 4) {
    //TODO remove drop database logic before release
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
}
