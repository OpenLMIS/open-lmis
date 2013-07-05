/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

'use strict';
angular.module('createRnRTemplate', ['openlmis', 'ui.sortable']).config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/select-program', {
      controller:ConfigureRnRTemplateController,
      templateUrl:'partials/select-program.html',
      resolve:ConfigureRnRTemplateController.resolve }).

    when('/create-rnr-template/:programId', {
      controller:SaveRnrTemplateController,
      templateUrl:'partials/form.html',
      resolve:SaveRnrTemplateController.resolve }).

    otherwise({redirectTo:'/select-program'});
}]);