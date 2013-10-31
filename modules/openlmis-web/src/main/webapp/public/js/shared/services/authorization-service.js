/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

services.factory('AuthorizationService', function (localStorageService, $window) {

  var rights = localStorageService.get(localStorageKeys.RIGHT);

  var preAuthorize = function () {
    if (rights === undefined || rights === null) return false;

    var permissions = Array.prototype.slice.call(arguments);
    var permitted = false;
    $(permissions).each(function (i, permission) {
      if (rights.indexOf(permission) > -1) {
        permitted = true;
        return false;
      }
      return true;
    });
    if (permitted) return true;

    $window.location = "/public/pages/access-denied.html";
    return false;
  };

  var hasPermission = function () {
    if (rights === undefined || rights === null) return false;

    var permissions = Array.prototype.slice.call(arguments);
    var permitted = false;

    $(permissions).each(function (i, permission) {
      if (rights.indexOf(permission) > -1) {
        permitted = true;
        return false;
      }
      return true;
    });

    return permitted;
  };

  return{
    preAuthorize: preAuthorize,
    hasPermission: hasPermission
  };
});
