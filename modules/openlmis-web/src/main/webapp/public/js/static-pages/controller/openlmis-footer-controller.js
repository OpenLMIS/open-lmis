/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

function OpenLmisFooterController($scope,SettingsByKey, localStorageService, loginConfig,$cookies,$route,SiteContent,  $location, $window) {
    $scope.loginConfig = loginConfig;
    $scope.user = localStorageService.get(localStorageKeys.USERNAME);
    $scope.userId = localStorageService.get(localStorageKeys.USER_ID);
    $scope.navigateToPage= function(relativePath){
       var locale_string=$cookies.lang;
        var fullPath;
        if(!locale_string || locale_string==="en" || locale_string==='en_TZ'){
            fullPath='/'+relativePath;
        }else {
            fullPath='/'+relativePath+'_' + locale_string;
        }


              $window.location.href= "/public/site/index.html#"+fullPath;


    };

    SettingsByKey.get({key: 'STATIC_PAGE_FOOTER_STATUS'}, function (data) {
        $scope.footerSetting = data.settings;
    });

}
