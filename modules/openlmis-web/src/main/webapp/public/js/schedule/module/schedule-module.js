/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

'use strict';
angular.module('schedule', ['openlmis']).config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/list', {controller:ScheduleController, templateUrl:'partials/list.html'}).
    when('/manage-period/:id', {controller:SchedulePeriodController, templateUrl:'partials/period.html'}).
    otherwise({redirectTo:'/list'});
}]).run(function($rootScope, AuthorizationService) {
    $rootScope.schedulesSelected = "selected";
    AuthorizationService.preAuthorize('MANAGE_SCHEDULE');
  });