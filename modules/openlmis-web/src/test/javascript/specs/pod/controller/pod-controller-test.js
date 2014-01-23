/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe('PODController', function () {
  var scope, controller, $httpBackend, routeParams, responseData;

  beforeEach(module('openlmis.services'));
  beforeEach(inject(function ($rootScope, $controller, _$httpBackend_) {
    var podId = '1234';

    scope = $rootScope.$new();
    controller = $controller;
    $httpBackend = _$httpBackend_;

    routeParams = { id: podId};
    scope.$parent.pod = undefined;
    responseData = {
      orderPOD: {
        podLineItems: [
          {productCode: 'P10',
            productCategory: 'antibiotics'},
          {productCode: 'P11',
            productCategory: 'anti-fungal'}
        ]
      },
      order: { facilityCode: 'F10',
        emergency: true
      }
    };
    $httpBackend.expect('GET', '/pod-orders/' + podId + '.json').respond(200, responseData);
    controller(PODController, {$scope: scope, $routeParams: routeParams});
    $httpBackend.flush();
  }));

  it('should set order, pod in scope if not present in parent scope', function () {
    expect(scope.pod).toEqual(responseData.orderPOD);
    expect(scope.order).toEqual(responseData.order);
    expect(scope.requisitionType).toEqual("requisition.type.emergency");
  });

  it('should return false if category is same for the current and previous line item same', function () {
    scope.pod.podLineItems[0].productCategory = 'anti-fungal';
    expect(scope.isCategorySameAsPreviousLineItem(1)).toBeFalsy();
  });

  it('should return true if category are different', function () {
    expect(scope.isCategorySameAsPreviousLineItem(1)).toBeTruthy();
  });

});