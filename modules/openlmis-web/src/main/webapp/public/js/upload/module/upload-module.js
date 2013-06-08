/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

'use strict';
require(['../../shared/app','../controller/upload-controller'], function (app) {
  app.loadApp();
  angular.module('upload', ['openlmis']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/upload', {controller: UploadController, templateUrl: 'partials/form.html'}).
      otherwise({redirectTo: '/upload'});
  }]);
  angular.bootstrap(document, ['upload']);
});
