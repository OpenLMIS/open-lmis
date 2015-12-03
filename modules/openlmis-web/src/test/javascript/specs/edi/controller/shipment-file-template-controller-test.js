/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("Shipment File Template Controller", function () {
  var scope, controller, httpBackend, messageService, shipmentFileTemplate, $q, timeout, deferredObject, dateFormats, location;

  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, $controller, $httpBackend, _messageService_, $location) {
    messageService = _messageService_;
    scope = $rootScope.$new();
    controller = $controller;
    httpBackend = $httpBackend;
    location = $location;

    shipmentFileTemplate = {
      "configuration": {
        "id": null,
        "createdBy": null,
        "modifiedBy": null,
        "createdDate": 1378965630040,
        "modifiedDate": 1378965630040,
        "headerInFile": false
      },
      "columns": [
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
    spyOn(location, 'path');
    spyOn(messageService, 'get').andReturn("saved successfully");
    scope.saveShipmentFileTemplate();
    httpBackend.flush();
    expect(scope.message).toEqual("saved successfully");
    expect(scope.error).toEqual("");
    expect(location.path).toHaveBeenCalledWith("configure-system-settings");
  });

  it('should not save shipment file template if position is invalid', function () {
    shipmentFileTemplate.columns[0].position = "";
    scope.saveShipmentFileTemplate();
    expect(scope.message).toEqual("");
    expect(scope.error).toEqual("file.invalid.position");
  });

  it('should not save shipment file template if position is zero', function () {
    shipmentFileTemplate.columns[0].position = 0;
    scope.saveShipmentFileTemplate();
    expect(scope.message).toEqual("");
    expect(scope.error).toEqual("file.invalid.position");
  });

  it('should not save shipment file template if position is duplicate', function () {
    shipmentFileTemplate.columns[0].position = shipmentFileTemplate.columns[1].position;
    scope.saveShipmentFileTemplate();
    expect(scope.message).toEqual("");
    expect(scope.error).toEqual("file.duplicate.position");
  });

});