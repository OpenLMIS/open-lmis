/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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
  }

  function createDistributionStore() {
    var distributionStore = connection.createObjectStore("distributions", {"keyPath": "id"});
    distributionStore.createIndex("index_zpp", "zpp", {"unique": true});
  }

};
