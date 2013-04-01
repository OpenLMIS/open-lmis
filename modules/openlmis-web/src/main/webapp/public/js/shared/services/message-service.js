/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

services.factory('messageService', function (Messages, localStorageService) {

  var populate = function () {
    var messagesInStorage = localStorageService.get("messagesAvailable");
    if (!messagesInStorage) {
      Messages.get({}, function (data) {
        localStorageService.add('messagesAvailable', true);
        for (var attr in data.messages) {
          localStorageService.add('message.' + attr, data.messages[attr]);
        }
      }, {});
    }
  };

  var get = function (key) {
    return localStorageService.get('message.' + key);
  };

  return{
    populate:populate,
    get:get
  }
});
