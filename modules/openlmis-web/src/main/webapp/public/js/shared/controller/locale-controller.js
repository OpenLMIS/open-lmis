/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function LocaleController($scope, $rootScope, Locales, ChangeLocale, Messages, localStorageService) {

  Locales.get({}, function (data) {
    $scope.locales = data.locales;
  }, {});

  $scope.changeLocale = function (localeKey) {
    ChangeLocale.update({locale: localeKey}, {}, function (data) {
      Messages.get({}, function (data) {
        localStorageService.clearAll();
        for (var attr in data.messages) {
          localStorageService.add('message.' + attr, data.messages[attr]);
        }
        $rootScope.$broadcast('messagesPopulated');
      }, {});
    })
  }

};