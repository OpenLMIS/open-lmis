/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 * + *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function OpenLmisFooterController($scope, localStorageService, loginConfig,$cookies,$route,  $location, $window) {
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

}
