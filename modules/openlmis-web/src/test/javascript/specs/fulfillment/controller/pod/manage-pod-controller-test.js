/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('ManagePODController', function () {
  var scope, controller, messageService, $httpBackend, location;

  beforeEach(module('openlmis'));
  beforeEach(inject(function ($rootScope, $controller, _messageService_, _$httpBackend_, $location) {
    scope = $rootScope.$new();
    scope.option = {all : true};
    scope.filter = {program : 1};
    location = $location;
    spyOn(location, 'url');

    controller = $controller;
    messageService = _messageService_;
    $httpBackend = _$httpBackend_;
    spyOn(messageService, 'get');

    data = {ordersForPOD: [
      {id: 1}
    ]};

    var programs = {programList: [{id: 1, name: 'ARV'}]};

    $httpBackend.when('GET', '/create/requisition/programs.json').respond(200, programs);
    $httpBackend.when('GET', '/manage-pod-orders').respond(200, data);
    controller(ManagePODController, {$scope: scope});
    $httpBackend.flush();

  }));

  it('should set orders for manage pod in scope', function () {
    //
    scope.option.all = true;
    var facilities = {facilities :[]};
    $httpBackend.when('GET', '/create/requisition/supervised/1/facilities.json').respond(200, facilities);
    $httpBackend.when('GET', '/manage-pod-orders?program=1').respond(200, data);
    scope.onParamChanged();
    $httpBackend.flush();
    expect(scope.orders).toEqual(data.ordersForPOD);
  });

  it('should get order status message based on status', function () {
    scope.getStatus('STATUS');

    expect(messageService.get).toHaveBeenCalledWith('label.order.STATUS');
  });

  it('should get orderPOD from order id', function () {
    var orderId = 1;
    data = {
      orderPOD: {id: 1},
      order: {emergency: true}
    };

    $httpBackend.expectPOST('/pods.json?orderId=1').respond(200, data);

    scope.createPOD(orderId);
    $httpBackend.flush();

    expect(location.url).toHaveBeenCalledWith('/pods/1?page=1');
  });
});