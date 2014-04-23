/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("Order File Template Controller", function () {
  var scope, controller, httpBackend, messageService, orderFileTemplate, $q, timeout, deferredObject, dateFormats, location;

  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, $controller, $httpBackend, _messageService_, _$timeout_, $location) {
    messageService = _messageService_;
    scope = $rootScope.$new();
    controller = $controller;
    httpBackend = $httpBackend;
    timeout = _$timeout_;
    location = $location;
    orderFileTemplate = {"configuration": {"id": null, "orderFilePrefix": "O", "orderDatePattern": "dd/MM/yy", "periodDatePattern": "MM/yy", "headerInOrderFile": false},
      "orderFileColumns": [
        {"id": null, "dataFieldLabel": "header.order.number", "openLmisField": true, "position": 1, "columnLabel": "Order number", "includeInOrderFile": true},
        {"id": null, "dataFieldLabel": "create.facility.code", "openLmisField": true, "position": 2, "columnLabel": "Facility code", "includeInOrderFile": true},
        {"id": null, "dataFieldLabel": "header.product.code", "openLmisField": true, "position": 3, "columnLabel": "Product code", "includeInOrderFile": true},
        {"id": null, "dataFieldLabel": "header.quantity.approved", "openLmisField": true, "position": 4, "columnLabel": "Approved quantity", "includeInOrderFile": true},
        {"id": null, "dataFieldLabel": "label.period", "openLmisField": true, "position": 5, "columnLabel": "Period", "includeInOrderFile": true},
        {"id": null, "dataFieldLabel": "header.order.date", "openLmisField": true, "position": 6, "columnLabel": "Order date", "includeInOrderFile": true}
      ]};

    dateFormats = [
      {"format": "ddMMyyyy", "orderDate": true},
      {"format": "dd/MM/yyyy", "orderDate": true},
      {"format": "dd-MM-yyyy", "orderDate": true},
      {"format": "MMddyy", "orderDate": true},
      {"format": "yyyy/MM/dd", "orderDate": false}
    ];

    controller(OrderFileTemplateController, {$scope: scope, orderFileTemplate: orderFileTemplate, dateFormats: dateFormats});
  }));

  it('should get order file template in scope', function () {
    $q = {defer: function () {
    }};
    deferredObject = {promise: {}, resolve: function () {
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

  it('should get date formats in scope', function () {
    $q = {defer: function () {
    }};
    deferredObject = {promise: {}, resolve: function () {
    }};
    spyOn(deferredObject, 'resolve');

    spyOn($q, 'defer').andCallFake(function () {
      return deferredObject;
    });

    httpBackend.expect('GET', "/date-formats.json").respond({dateFormats: dateFormats});
    controller(OrderFileTemplateController.resolve.dateFormats, {$q: $q});
    expect($q.defer).toHaveBeenCalled();
    timeout.flush();
    httpBackend.flush();
    expect(deferredObject.resolve).toHaveBeenCalledWith(dateFormats);
    expect(scope.orderDateFormats).toEqual(["ddMMyyyy", "dd/MM/yyyy", "dd-MM-yyyy", "MMddyy"]);
    expect(scope.periodDateFormats).toEqual(["ddMMyyyy", "dd/MM/yyyy", "dd-MM-yyyy", "MMddyy", "yyyy/MM/dd"]);
  });

  it('should save order file template', function () {
    httpBackend.expect('POST', "/order-file-template.json").respond({success: "Saved successfully"}, 200);
    spyOn(location, 'path');
    spyOn(messageService, 'get').andReturn("Saved successfully");
    scope.saveOrderFileTemplate();
    httpBackend.flush();
    expect(scope.message).toEqual("Saved successfully");
    expect(location.path).toHaveBeenCalledWith("configure-system-settings");
  });

  it('should add new Order file column', function () {
    scope.newOrderFileColumn = {"dataFieldLabel": "header.order.number", "columnLabel": "Order number", "includeInOrderFile": true};
    scope.addNewOrderFileColumn();
    expect(scope.orderFileTemplate.orderFileColumns.length).toEqual(7);
    expect(scope.orderFileTemplate.orderFileColumns[6].position).toEqual(7);
    expect(scope.orderFileTemplate.orderFileColumns[6].openLmisField).toEqual(false);
    expect(scope.newOrderFileColumn).toEqual({"includeInOrderFile": true, dataFieldLabel: "label.not.applicable"});
  });

  it('should remove order file column', function () {
    scope.removeOrderFileColumn(5);
    expect(scope.orderFileTemplate.orderFileColumns.length).toEqual(5);
  });
})