/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

'use strict';
angular.module('edi', ['openlmis', 'ui.sortable']).config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/configure-order-file', { controller: OrderFileTemplateController, templateUrl: 'partials/order-file-template-form.html', resolve: OrderFileTemplateController.resolve }).
    when('/configure-edi-file', { templateUrl: 'partials/configure-edi-file.html' }).
    when('/configure-shipment-file', { controller: ShipmentFileTemplateController, templateUrl: 'partials/shipment-file-template-form.html',resolve: ShipmentFileTemplateController.resolve }).
    otherwise({redirectTo: '/configure-order-file'});
}]);