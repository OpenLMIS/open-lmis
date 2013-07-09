/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

'use strict';
angular.module('report', ['openlmis', 'ui.bootstrap.modal', 'ui.bootstrap.dialog']).
  config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/create', {controller:CreateReportController, templateUrl:'partials/create.html'}).
    when('/list', {controller:ListReportController, templateUrl:'partials/list.html', resolve:ListReportController.resolve}).
    otherwise({redirectTo:'/list'});
}]).run(function ($rootScope, AuthorizationService) {
    AuthorizationService.preAuthorize('VIEW_REPORT');
  });
