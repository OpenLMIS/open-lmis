/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

distributionModule.service('SharedDistributions', function (IndexedDB, $rootScope) {

  this.distributionList = [];

  var thisService = this;

  this.update = function () {
    IndexedDB.execute(function (connection) {
      var transaction = connection.transaction('distributions');

      var cursorRequest = transaction.objectStore('distributions').openCursor();
      var aggregate = [];

      cursorRequest.onsuccess = function (event) {
        var cursor = event.target.result;
        if (cursor) {
          aggregate.push(new Distribution(cursor.value));
          cursor['continue']();
        }
      };

      transaction.oncomplete = function (e) {
        thisService.distributionList = aggregate;
        if (!$rootScope.$$phase)$rootScope.$apply();
      };
    });
  };
});
