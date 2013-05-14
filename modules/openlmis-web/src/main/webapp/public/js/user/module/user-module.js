/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

'use strict';
require(['../../shared/app', '../controller/user-controller', '../controller/user-search-controller', '../controller/user-role-assignment-controller'], function (app) {
  app.loadApp();
  angular.module('user', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dialog']).
    config(['$routeProvider', function ($routeProvider) {
      $routeProvider.
        when('/search', {controller: UserSearchController, templateUrl: 'partials/search.html'}).
        when('/create-user', {controller: UserController, templateUrl: 'partials/create.html'}).
        when('/edit/:userId', {controller: UserController, templateUrl: 'partials/create.html'}).
        otherwise({redirectTo: '/search'});
    }]).directive('onKeyup', function () {
      return function (scope, elm, attrs) {
        elm.bind("keyup", function () {
          scope.$apply(attrs.onKeyup);
        });
      };
    })
    .directive('select2Blur', function () {
      return function (scope, elm, attrs) {
        angular.element("body").on('mousedown', function (e) {
          $('.select2-dropdown-open').each(function () {
            if (!$(this).hasClass('select2-container-active')) {
              $(this).data("select2").blur();
            }
          })
        });
      };
    })
    .run(function ($rootScope, AuthorizationService) {
      $rootScope.userSelected = "selected";
      AuthorizationService.preAuthorize('MANAGE_USERS');
    });
  angular.bootstrap(document, ['user']);
});


