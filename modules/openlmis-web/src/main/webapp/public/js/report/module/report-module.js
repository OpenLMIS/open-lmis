/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

'use strict';
angular.module('report', ['openlmis', 'ui.bootstrap.modal', 'ui.bootstrap.dialog']).
  config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/create-report', {controller:CreateReportController, templateUrl:'partials/create.html', resolve:CreateReportController.resolve}).
    otherwise({redirectTo:'/create-report'});
}]).run(function ($rootScope, AuthorizationService) {
    AuthorizationService.preAuthorize('MANAGE_REPORTS');
  });
