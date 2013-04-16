/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('ViewOrderListController', function () {
  var orders, scope, controller;

  beforeEach(module('openlmis.services'));
  beforeEach(inject(function ($rootScope, $controller) {
    scope = $rootScope.$new();
    controller = $controller;
    orders = {'orders': [
      {"id": 1}
    ]};
    controller(ViewOrderListController, {$scope: scope, orders: orders});
  }));

  it('should initialize orders', function () {
    expect(orders).toEqual(scope.orders);
  });
});
