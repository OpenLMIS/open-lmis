/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('ViewOrderListController', function () {
  var orders, scope, controller, messageService;

  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));
  beforeEach(inject(function ($rootScope, $controller, _messageService_) {
    scope = $rootScope.$new();
    controller = $controller;
    messageService = _messageService_;
    orders = {'orders': [
      {"id": 1}
    ]};
    controller(ViewOrderListController, {$scope: scope, orders: orders});
  }));

  it('should initialize orders', function () {
    expect(orders).toEqual(scope.orders);
  });
});
