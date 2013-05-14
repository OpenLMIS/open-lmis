/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

require(['../../shared/app', '../controller/facility-controller', '../controller/facility-search-controller'],
  function (app) {
    app.loadApp();
    'use strict';
    angular.module('facility', ['openlmis', 'ui.bootstrap.modal', 'ui.bootstrap.dialog']).
      config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
          when('/search', {controller: FacilitySearchController, templateUrl: 'partials/search.html'}).
          when('/create-facility', {controller: FacilityController, templateUrl: 'partials/create.html', resolve: FacilityController.resolve}).
          when('/edit/:facilityId', {controller: FacilityController, templateUrl: 'partials/create.html', resolve: FacilityController.resolve}).
          otherwise({redirectTo: '/search'});
      }]).run(function ($rootScope, AuthorizationService) {
        $rootScope.facilitySelected = "selected";
        AuthorizationService.preAuthorize('MANAGE_FACILITY');
      });
    angular.bootstrap(document, ['facility']);
  });