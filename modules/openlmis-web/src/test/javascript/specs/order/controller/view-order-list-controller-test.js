/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

describe('ViewOrderListController', function () {
  var data, scope, controller, messageService, $routeParams, $location, $httpBackend;

  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));
  beforeEach(inject(function ($rootScope, $controller, _messageService_, _$routeParams_, _$location_, _$httpBackend_) {
    scope = $rootScope.$new();
    scope.currentPage = 1;
    $routeParams = _$routeParams_;
    controller = $controller;
    $location = _$location_;
    spyOn($location, 'search');
    messageService = _messageService_;
    spyOn(messageService, 'get');
    $httpBackend = _$httpBackend_;
    data = {'orders': [
      {"id": 1}
    ], pageSize: 5, numberOfPages: 10};

    $httpBackend.expect('GET', '/orders.json?page=1').respond(200, data);

    controller(ViewOrderListController, {$scope: scope});
    $httpBackend.flush();
  }));

  it('should set orders for first page in scope', function () {
    expect(scope.orders).toEqual(data.orders);
  });

  it('should set pageSize in scope', function () {
    expect(scope.pageSize).toEqual(5);
  });

  it('should set number of pages in scope', function () {
    expect(scope.numberOfPages).toEqual(10);
  });

  it('should set data for page 3 on update of page in url', function () {
    data.orders = [
      {id: 2},
      {id: 4}
    ];
    $httpBackend.expect('GET', '/orders.json?page=3').respond(200, data);
    $routeParams.page = 3;
    scope.$broadcast('$routeUpdate');

    $httpBackend.flush();

    expect(scope.orders).toEqual([
      {id: 2},
      {id: 4}
    ]);
  });

  it('should set current page equal to url', function () {
    $routeParams.page = 3;
    scope.$broadcast('$routeUpdate');

    expect(scope.currentPage).toEqual(3);
  });

  it('should redirect to page 1 if call returns 0 orders and current page not 1', function () {
    data.orders = [];
    $routeParams.page = 3;
    $httpBackend.expect('GET', '/orders.json?page=3').respond(200, data);
    scope.$broadcast('$routeUpdate');

    $httpBackend.flush();

    expect($location.search).toHaveBeenCalledWith('page', 1);
  });

  it('should update location on change of current page', function () {
    scope.currentPage = 5;

    scope.$digest();

    expect($location.search).toHaveBeenCalledWith('page', 5);
  });

  it('should get order status message according to status', function () {
    scope.getStatus('STATUS');

    expect(messageService.get).toHaveBeenCalledWith('label.order.STATUS')
  })

});
