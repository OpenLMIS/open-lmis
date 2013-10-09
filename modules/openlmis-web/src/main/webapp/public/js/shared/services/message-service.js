/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

services.factory('messageService', function (Messages, localStorageService, $rootScope, version) {

  var populate = function () {
    if (localStorageService.get('version') != version) {
      localStorageService.add('version', version);
      Messages.get({}, function (data) {
        for (var attr in data.messages) {
          localStorageService.add('message.' + attr, data.messages[attr]);
        }
        $rootScope.$broadcast('messagesPopulated');
      }, {});
    }
  };

  var get = function () {
    var keyWithArgs = Array.prototype.slice.call(arguments);
    var displayMessage =  localStorageService.get('message.' + keyWithArgs[0]);
    if(keyWithArgs.length > 1 && displayMessage) {
      $.each(keyWithArgs, function (index, arg) {
        if (index > 0) {
          displayMessage = displayMessage.replace("{" + (index-1) + "}", arg);
        }
      });
    }
    return displayMessage || keyWithArgs[0];
  };

  return{
    populate:populate,
    get:get
  };
});
