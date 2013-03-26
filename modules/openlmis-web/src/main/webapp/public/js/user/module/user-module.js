/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

'use strict';
angular.module('user', ['openlmis', 'ngGrid', 'ui.bootstrap.modal']).
    config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
      when('/search', {controller:UserSearchController, templateUrl:'partials/search.html'}).
      when('/create-user', {controller:UserController, templateUrl:'partials/create.html'}).
      when('/edit/:userId', {controller:UserController, templateUrl:'partials/create.html'}).
    otherwise({redirectTo:'/search'});
}]).directive('onKeyup', function () {
      return function (scope, elm, attrs) {
        elm.bind("keyup", function () {
          scope.$apply(attrs.onKeyup);
        });
      };
    }).run(function($rootScope, AuthorizationService) {
    $rootScope.userSelected = "selected";
    AuthorizationService.preAuthorize('MANAGE_USERS');
  });


