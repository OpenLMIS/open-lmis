/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

'use strict';
angular.module('resetPassword', ['openlmis']).
  config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/token/:token', {controller:ValidateTokenController, resolve:ValidateTokenController.resolve}).
    when('/reset/:token', {controller:ResetPasswordController, resolve:ResetPasswordController.resolve, templateUrl:'partials/reset-password-form.html'}).
    when('/reset/password/complete', {controller:ResetCompleteController, templateUrl:'partials/reset-password-complete.html'}).
    otherwise({redirectTo:'/token/:token'});
}]);
