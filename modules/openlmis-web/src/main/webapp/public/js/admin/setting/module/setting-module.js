/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

'use strict';
angular.module('setting', ['openlmis', 'ui.bootstrap.modal', 'ui.bootstrap.dialog']).config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/list', {controller:ListSettingController, templateUrl:'partials/list.html'}).
    otherwise({redirectTo:'/list'});
}]).run(function($rootScope, AuthorizationService) {
    $rootScope.roleSelected = "selected";
    AuthorizationService.preAuthorize('MANAGE_SETTING');
  });