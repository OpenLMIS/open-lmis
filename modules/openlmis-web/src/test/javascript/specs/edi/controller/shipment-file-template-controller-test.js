/*
 * Copyright � 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe("Shipment File Template Controller", function () {
  var scope, controller, httpBackend, messageService, shipmentFileTemplate, $q, timeout, deferredObject, dateFormats;

  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));

  beforeEach(inject(function ($rootScope, $controller, $httpBackend, _messageService_) {
    messageService = _messageService_;
    scope = $rootScope.$new();
    controller = $controller;
    httpBackend = $httpBackend;

    shipmentFileTemplate = {
      "shipmentConfiguration": {
        "id": null,
        "createdBy": null,
        "modifiedBy": null,
        "createdDate": 1378965630040,
        "modifiedDate": 1378965630040,
        "headerInFile": false
      },
      "shipmentFileColumns": [
        {
          "id": 1,
          "createdDate": 1378965630041,
          "modifiedDate": 1378965630041,
          "name": "orderId",
          "dataFieldLabel": "header.order.number",
          "position": 1,
          "include": true,
          "mandatory": true
        },
        {
          "id": 2,
          "createdDate": 1378965630041,
          "modifiedDate": 1378965630041,
          "name": "productCode",
          "dataFieldLabel": "header.product.code",
          "position": 2,
          "include": true,
          "mandatory": true
        },
        {
          "id": 3,
          "createdDate": 1378965630041,
          "modifiedDate": 1378965630041,
          "name": "quantityShipped",
          "dataFieldLabel": "header.quantity.shipped",
          "position": 3,
          "include": true,
          "mandatory": true
        },
        {
          "id": 4,
          "createdDate": 1378965630041,
          "modifiedDate": 1378965630041,
          "name": "cost",
          "dataFieldLabel": "header.cost",
          "position": 4,
          "include": false,
          "mandatory": false
        },
        {
          "id": 5,
          "createdDate": 1378965630041,
          "modifiedDate": 1378965630041,
          "name": "packedDate",
          "dataFieldLabel": "header.packed.date",
          "position": 5,
          "include": false,
          "mandatory": false,
          "datePattern": "dd/MM/yy"
        },
        {
          "id": 6,
          "createdDate": 1378965630041,
          "modifiedDate": 1378965630041,
          "name": "shippedDate",
          "dataFieldLabel": "header.shipped.date",
          "position": 6,
          "include": false,
          "mandatory": false,
          "datePattern": "dd/MM/yy"
        }
      ]
    }

    dateFormats = [
      {"format": "ddMMyyyy", "orderDate": true},
      {"format": "dd/MM/yyyy", "orderDate": true},
      {"format": "dd-MM-yyyy", "orderDate": true},
      {"format": "MMddyy", "orderDate": true},
      {"format": "yyyy/MM/dd", "orderDate": false}
    ];

    controller(ShipmentFileTemplateController, {$scope: scope, shipmentFileTemplate: shipmentFileTemplate, dateFormats: dateFormats});
  }));

  it('should save shipment file template', function () {
    httpBackend.expect('POST', '/shipment-file-template.json').respond(200, {"success": "saved successfully"});
    scope.saveShipmentFileTemplate();
    httpBackend.flush();
    expect(scope.message).toEqual("saved successfully");
    expect(scope.error).toEqual("");
  });

  it('should not save shipment file template if position is invalid', function () {
    shipmentFileTemplate.shipmentFileColumns[0].position = "";
    scope.saveShipmentFileTemplate();
    expect(scope.message).toEqual("");
    expect(scope.error).toEqual("shipment.file.invalid.position");
  });

  it('should not save shipment file template if position is zero', function () {
    shipmentFileTemplate.shipmentFileColumns[0].position = 0;
    scope.saveShipmentFileTemplate();
    expect(scope.message).toEqual("");
    expect(scope.error).toEqual("shipment.file.invalid.position");
  });

  it('should not save shipment file template if position is duplicate', function () {
    shipmentFileTemplate.shipmentFileColumns[0].position = shipmentFileTemplate.shipmentFileColumns[1].position;
    scope.saveShipmentFileTemplate();
    expect(scope.message).toEqual("");
    expect(scope.error).toEqual("shipment.file.duplicate.position");
  });

});