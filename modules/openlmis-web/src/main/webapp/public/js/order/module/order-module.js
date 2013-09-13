/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

var orderModule = angular.module('order', ['openlmis', 'ngGrid', 'ui.bootstrap']).config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/view-orders', {controller:ViewOrderListController, templateUrl:'partials/view-order.html', resolve:ViewOrderListController.resolve}).
    otherwise({redirectTo:'/view-orders'});
}]);