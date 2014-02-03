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
  var scope, controller, $httpBackend, routeParams, response, location;

  beforeEach(module('openlmis'));
  beforeEach(inject(function ($rootScope, $controller, _$httpBackend_, _$location_) {
    var podId = '1234';

    scope = $rootScope.$new();
    controller = $controller;
    $httpBackend = _$httpBackend_;
    location = _$location_;

    routeParams = { id: podId};
    scope.$parent.pod = undefined;
    response = {
      orderPOD: {
        podLineItems: [
          {productCode: 'P10', productCategory: 'antibiotics'},
          {productCode: 'P11', productCategory: 'anti-fungal'},
          {productCode: 'P12', productCategory: 'anti-histamine'},
          {productCode: 'P13', productCategory: 'anti-septic'},
          {productCode: 'P15', productCategory: 'pain-relief'}
        ]
      },
      order: { facilityCode: 'F10', emergency: true}
    };

    controller(PODController, {$scope: scope, $routeParams: routeParams, $location: location, orderPOD: response, pageSize: 2});
  }));

  it('should set pageSize in scope', function () {
    expect(scope.pageSize).toEqual(2);
  });

  it('should calculate and set number of pages in scope', function () {
    expect(scope.numberOfPages).toEqual(3);
  });

  it('should calculate and set current page in scope', function () {
    expect(scope.currentPage).toEqual(1);
  });

  it('should set lineItems in current page', function () {
    expect(scope.pageLineItems).toEqual([
      {productCode: 'P10', productCategory: 'antibiotics'},
      {productCode: 'P11', productCategory: 'anti-fungal'}
    ]);
  });

  it('should set order, pod and requisition type in scope', function () {
    expect(scope.pod).toEqual(response.orderPOD);
    expect(scope.order).toEqual(response.order);
    expect(scope.requisitionType).toEqual("requisition.type.emergency");
  });

  it('should update page number in url when current page is updated', function () {
    scope.currentPage = 3;
    spyOn(location, 'search');

    scope.$apply();

    expect(location.search).toHaveBeenCalledWith('page', 3);
  });

  it('should update line items in page when page in url is updated', function () {
    routeParams.page = 2;
    spyOn(location, 'search');

    scope.$broadcast('$routeUpdate');

    expect(scope.currentPage).toEqual(2);
    expect(scope.pageLineItems).toEqual([
      {productCode: 'P12', productCategory: 'anti-histamine'},
      {productCode: 'P13', productCategory: 'anti-septic'}
    ]);
  });

  it('should return false if category is same for the current and previous line item same', function () {
    scope.pod.podLineItems[0].productCategory = 'anti-fungal';
    expect(scope.isCategorySameAsPreviousLineItem(1)).toBeFalsy();
  });

  it('should return true if category are different', function () {
    expect(scope.isCategorySameAsPreviousLineItem(1)).toBeTruthy();
  });

});

describe('Pod Save', function () {
  var scope, controller, $httpBackend, routeParams, pod, location, podId;

  beforeEach(module('openlmis'));
  beforeEach(inject(function ($rootScope, $controller, _$httpBackend_, _$location_) {
    podId = '1234';

    scope = $rootScope.$new();
    controller = $controller;
    $httpBackend = _$httpBackend_;
    location = _$location_;

    routeParams = {id: podId};
    pod = {
      orderPOD: {
        podLineItems: [
          {productCode: 'P10', productCategory: 'antibiotics'},
          {productCode: 'P11', productCategory: 'anti-fungal'},
          {productCode: 'P12', productCategory: 'anti-histamine'},
          {productCode: 'P13', productCategory: 'anti-septic'},
          {productCode: 'P15', productCategory: 'pain-relief'}
        ]
      },
      order: {facilityCode: 'F10', emergency: true}
    };

    controller(PODController, {$scope: scope, $routeParams: routeParams, $location: location, orderPOD: pod, pageSize: 2});
  }));

  it('should save pod if form dirty and set pristine on successful save', function () {
    scope.pageLineItems = [
      {id: 2},
      {id: 4},
      {id: 8}
    ];
    scope.podForm = jasmine.createSpyObj('podForm', ['$setPristine']);
    scope.podForm.$dirty = true;
    $httpBackend.expect('PUT', '/pods/' + podId + '.json', {podLineItems: scope.pageLineItems}).respond(200, {success: 'successful'});

    scope.save();

    $httpBackend.flush();
    expect(scope.podForm.$setPristine).toHaveBeenCalled();
    expect(scope.message).toEqual('successful')
  });

  it('should not save pod if form not dirty', function () {
    scope.podForm = {$dirty: false};

    scope.save();

    scope.$apply();

    $httpBackend.verifyNoOutstandingRequest();
    $httpBackend.verifyNoOutstandingRequest();
  });

  it('should set error if save fails', function () {
    scope.pageLineItems = [
      {id: 2},
      {id: 4},
      {id: 8}
    ];
    scope.podForm = jasmine.createSpyObj('podForm', ['$setPristine']);
    scope.podForm.$dirty = true;
    $httpBackend.expect('PUT', '/pods/' + podId + '.json', {podLineItems: scope.pageLineItems}).respond(404, {error: 'error'});

    scope.save();

    $httpBackend.flush();

    expect(scope.error).toEqual('error');
    expect(scope.podForm.$setPristine).not.toHaveBeenCalled();
  });
});