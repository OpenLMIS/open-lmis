/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
angular.module('distribution', ['openlmis', 'IndexedDB']).config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/manage', {controller: DistributionController, templateUrl: 'partials/init.html', resolve: DistributionController.resolve}).
    when('/view-load-amounts/:deliveryZoneId/:programId/:periodId', {controller: ViewLoadAmountController, templateUrl: 'partials/view-load-amount.html', resolve: ViewLoadAmountController.resolve}).
    otherwise({redirectTo: '/manage'});

}]);