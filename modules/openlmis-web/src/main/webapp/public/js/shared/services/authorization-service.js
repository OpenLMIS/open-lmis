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
    var permissions = Array.prototype.slice.call(arguments);
    if(!hasRight(permissions)){
      $window.location = "/public/pages/access-denied.html";
      return false;
    }
    return true;
  };

  var hasRight = function(permissions){
    if (rights) {
      var rightNames = _.pluck(JSON.parse(rights), 'name');
      var hasRight = _.intersection(permissions, rightNames);
      if(hasRight.length > 0){
        return true;
      }}
    else{
      return false;
    }
  };

  var preAuthorizeReporting = function () {
    return rights && _.find(JSON.parse(rights), function (right) {
      return right.type === 'REPORTING';
    });
  };

  var hasPermission = function () {
    var permissions = Array.prototype.slice.call(arguments);
    return hasRight(permissions);
  };

  return{
    preAuthorize: preAuthorize,
    hasPermission: hasPermission,
    preAuthorizeReporting: preAuthorizeReporting
  };
});
