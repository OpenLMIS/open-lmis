/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function HeaderController($scope, localStorageService, loginConfig, ConfigSettingsByKey, $window) {
  $scope.loginConfig = loginConfig;
  $scope.user = localStorageService.get(localStorageKeys.USERNAME);
  $scope.userId = localStorageService.get(localStorageKeys.USER_ID);

  var isGoogleAnalyticsEnabled  = localStorageService.get('ENABLE_GOOGLE_ANALYTICS');
  // load this only once
  if(isGoogleAnalyticsEnabled === null){

    ConfigSettingsByKey.get({key: 'ENABLE_GOOGLE_ANALYTICS'}, function (data){
      localStorageService.add('ENABLE_GOOGLE_ANALYTICS', data.settings.value == 'true');
    });

    ConfigSettingsByKey.get({key: 'GOOGLE_ANALYTICS_TRACKING_CODE'}, function (data){
      localStorageService.add('GOOGLE_ANALYTICS_TRACKING_CODE', data.settings.value);
    });
  }



  $scope.logout = function () {
    localStorageService.remove(localStorageKeys.RIGHT);
    localStorageService.remove(localStorageKeys.USERNAME);
    localStorageService.remove(localStorageKeys.USER_ID);
    localStorageService.remove('ENABLE_GOOGLE_ANALYTICS');
    localStorageService.remove('GOOGLE_ANALYTICS_TRACKING_CODE');

    $.each(localStorageKeys.REPORTS, function(itm,idx){

          localStorageService.remove(idx);
      });
      $.each(localStorageKeys.PREFERENCE, function(item, idx){
          localStorageService.remove(idx);

      });
      $.each(localStorageKeys.DASHBOARD_FILTERS, function(item, idx){
          localStorageService.remove(idx);

      });
    document.cookie = 'JSESSIONID' + '=;expires=Thu, 01 Jan 1970 00:00:01 GMT; path=/';
    $window.location = "/j_spring_security_logout";
  };
}