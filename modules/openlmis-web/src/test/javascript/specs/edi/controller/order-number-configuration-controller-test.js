/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("Order Number Configuration Controller", function () {
  var scope, controller, httpBackend, messageService, orderNumberConfiguration, location;

  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, $controller, $httpBackend, _messageService_, $location) {
    messageService = _messageService_;
    scope = $rootScope.$new();
    controller = $controller;
    httpBackend = $httpBackend;
    location = $location;

    orderNumberConfiguration = {
      "orderNumberPrefix": "Order",
      "includeOrderNumberPrefix": "true",
      "includeProgramCode": "false",
      "includeSequenceCode": "true",
      "includeRnrTypeSuffix": "true"
    };

    controller(OrderNumberConfigurationController, {$scope: scope, orderNumberConfiguration: orderNumberConfiguration});
  }));

  it('should save order number configuration', function () {
    httpBackend.expect('POST', '/order-number-configuration.json').respond(200, {"success": "saved successfully"});
    spyOn(location, 'path');
    spyOn(messageService, 'get').andReturn("saved successfully");
    scope.saveOrderNumberConfiguration();
    httpBackend.flush();
    expect(scope.message).toEqual("saved successfully");
    expect(scope.error).toEqual("");
    expect(location.path).toHaveBeenCalledWith("configure-system-settings");
  });


});