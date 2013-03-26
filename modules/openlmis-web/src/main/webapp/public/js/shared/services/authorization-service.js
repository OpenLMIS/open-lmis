/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

services.factory('AuthorizationService', function (localStorageService, $window) {

  var rights = localStorageService.get(localStorageKeys.RIGHT);

  var hasPermission = function () {
    var permissions = Array.prototype.slice.call(arguments);
    var permitted = false;
    $(permissions).each(function (i, permission) {
      if (rights && rights.indexOf(permission) > -1) {
        permitted = true;
        return false;
      }
    });
    if (permitted)
      return true;
    $window.location = "/public/pages/access-denied.html";
  };

  return{
    hasPermission:hasPermission
  }
});
