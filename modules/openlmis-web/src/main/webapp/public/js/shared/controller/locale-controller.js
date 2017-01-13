/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function LocaleController($scope, $rootScope, $cookies, Locales, ChangeLocale, Messages, messageService, localStorageService) {
  $scope.selectedLocale = $cookies.lang === undefined ? "en" : $cookies.lang;

  Locales.get({}, function (data) {
    $scope.locales = data.locales;
    messageService.populate();
  }, {});

  $scope.setDatepickerDefaults = function(localeKey) {
    if ($.datepicker) {
      switch (localeKey) {
        case 'pt':
          $.datepicker.setDefaults($.datepicker.regional.pt);
          break;
        case 'fr':
          $.datepicker.setDefaults($.datepicker.regional.fr);
          break;
        case 'es':
          $.datepicker.setDefaults($.datepicker.regional.es);
          break;
        default:
          $.datepicker.setDefaults($.datepicker.regional.en);
      }
    }
  };

  $scope.changeLocale = function (localeKey) {
    $scope.selectedLocale = localeKey;
    $scope.setDatepickerDefaults(localeKey);
    ChangeLocale.update({locale: localeKey}, {}, function (data) {
      Messages.get({}, function (data) {
        for (var attr in data.messages) {
          var key = 'message.' + attr;
          localStorageService.remove(key);
          localStorageService.add(key, data.messages[attr]);
        }
        $rootScope.$broadcast('messagesPopulated');
      }, {});
    });
  };

  $rootScope.$on('$routeChangeSuccess', function () {
    $scope.setDatepickerDefaults($cookies.lang);
  });
}