/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe("Order File Template Controller", function () {
  var scope, controller, httpBackend, messageService, orderFileTemplate, $q, timeout, deferredObject;

  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));

  beforeEach(inject(function ($rootScope, $controller, $httpBackend,
                              _messageService_, _$timeout_) {
    messageService = _messageService_;
    scope = $rootScope.$new();
    controller = $controller;
    httpBackend = $httpBackend;
    timeout = _$timeout_;

    orderFileTemplate = {"configuration": {"id": null, "orderFilePrefix": "O", "orderDatePattern": "dd/MM/yy", "periodDatePattern": "MM/yy", "headerInOrderFile": false},
      "orderFileColumns": [
        {"id": null, "dataFieldLabel": "header.order.number", "openLmisField": true, "position": 1, "columnLabel": "Order number", "includeInOrderFile": true},
        {"id": null, "dataFieldLabel": "create.facility.code", "openLmisField": true, "position": 2, "columnLabel": "Facility code", "includeInOrderFile": true},
        {"id": null, "dataFieldLabel": "header.product.code", "openLmisField": true, "position": 3, "columnLabel": "Product code", "includeInOrderFile": true},
        {"id": null, "dataFieldLabel": "header.quantity.approved", "openLmisField": true, "position": 4, "columnLabel": "Approved quantity", "includeInOrderFile": true},
        {"id": null, "dataFieldLabel": "label.period", "openLmisField": true, "position": 5, "columnLabel": "Period", "includeInOrderFile": true},
        {"id": null, "dataFieldLabel": "header.order.date", "openLmisField": true, "position": 6, "columnLabel": "Order date", "includeInOrderFile": true}
      ]};

    controller(OrderFileTemplateController, {$scope: scope, orderFileTemplate: orderFileTemplate});
  }));

  it('should get order file template in scope', function () {
    $q = {defer: function () {
    }};
    deferredObject = {promise: {id: 1}, resolve: function () {
    }};
    spyOn(deferredObject, 'resolve');

    spyOn($q, 'defer').andCallFake(function () {
      return deferredObject;
    });

    httpBackend.expect('GET', "/order-file-template.json").respond({orderFileTemplate: orderFileTemplate});
    controller(OrderFileTemplateController.resolve.orderFileTemplate, {$q: $q});
    expect($q.defer).toHaveBeenCalled();
    timeout.flush();
    httpBackend.flush();
    expect(deferredObject.resolve).toHaveBeenCalledWith(orderFileTemplate);
    expect(scope.orderFileTemplate).toEqual(orderFileTemplate);
  });

  it('should save order file template', function() {
    httpBackend.expect('POST', "/order-file-template.json").respond({success: "Saved successfully"}, 200);
    console.log(scope.orderFileTemplate);
    scope.saveOrderFileTemplate();
    httpBackend.flush();
    expect(scope.message).toEqual("Saved successfully");
  });
})