/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

'use strict';
angular.module('regimenTemplate', ['openlmis']).config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/select-program', {
      controller: ConfigureRegimenTemplateController,
      templateUrl: 'partials/select-program.html',
      resolve: ConfigureRegimenTemplateController.resolve }).

    when('/create-regimen-template/:programId', {
      controller: SaveRegimenTemplateController,
      templateUrl: 'partials/form.html',
      resolve: SaveRegimenTemplateController.resolve}).

    otherwise({redirectTo: '/select-program'});
}]);