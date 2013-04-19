/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function HeaderController($scope, UserContext, $rootScope, localStorageService, messageService) {

 $scope.user = localStorageService.get(localStorageKeys.USERNAME);

  messageService.populate();

  $rootScope.fixToolBar = function () {

  };

  $scope.logout = function () {
    localStorageService.clearAll();
    window.location = "/j_spring_security_logout";
  };
}