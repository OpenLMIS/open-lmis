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
  var scope, controller, $httpBackend, routeParams, response, location, podId;
  beforeEach(module('fulfillment'));
  beforeEach(inject(function ($rootScope, $controller, _$httpBackend_, _$location_) {
    podId = '1234';

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

  describe('Update POD Page', function () {

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
      expect(scope.pod).toEqual(new ProofOfDelivery(response.orderPOD));
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
      scope.podForm = {$dirty: true};
      spyOn(location, 'search');

      scope.$broadcast('$routeUpdate');

      expect(scope.currentPage).toEqual(2);
      expect(scope.pageLineItems).toEqual([
        {productCode: 'P12', productCategory: 'anti-histamine'},
        {productCode: 'P13', productCategory: 'anti-septic'}
      ]);
    });

    it('should return false if category is same for the current and previous line item same', function () {
      scope.pageLineItems = [
        {},
        {},
        {},
        {},
        {productCategory: 'anti-fungal'},
        {productCategory: 'anti-fungal'}
      ];
      expect(scope.isCategoryDifferentFromPreviousLineItem(5)).toBeFalsy();
    });

    it('should return true if category are different', function () {
      scope.pageLineItems = [
        {},
        {},
        {},
        {},
        {productCategory: 'anti-fungal'},
        {productCategory: 'antibiotic'}
      ];
      expect(scope.isCategoryDifferentFromPreviousLineItem(5)).toBeTruthy();
    });

    it('should return true if first line item on page', function () {
      scope.pageLineItems = [
        {}
      ];
      expect(scope.isCategoryDifferentFromPreviousLineItem(0)).toBeTruthy();
    });

    it('should return error class if quantity received is undefined or null and errors shown', function () {
      scope.showSubmitErrors = true;
      expect(scope.cssClassForQuantityReceived(undefined)).toEqual('required-error');
      expect(scope.cssClassForQuantityReceived(null)).toEqual('required-error');
    });

    it('should not return error class if quantity received is valid', function () {
      scope.showSubmitErrors = true;
      expect(scope.cssClassForQuantityReceived(67)).toEqual('');
    });

    it('should not return error class if errors not shown', function () {
      scope.showSubmitErrors = false;
      expect(scope.cssClassForQuantityReceived(undefined)).toEqual('');
      expect(scope.cssClassForQuantityReceived(null)).toEqual('');
      expect(scope.cssClassForQuantityReceived(65)).toEqual('');
    });
  });

  describe('Pod Save', function () {
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
      expect(scope.error).toBeUndefined()
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
      expect(scope.message).toBeUndefined()
      expect(scope.podForm.$setPristine).not.toHaveBeenCalled();
    });

    it('should auto save on page change', function () {
      spyOn(scope, 'save');
      scope.podForm = {$dirty: false};

      scope.$broadcast('$routeUpdate');

      expect(scope.save).toHaveBeenCalled();
    })
  });

  describe('POD submit', function () {
    beforeEach(inject(function ($q) {
      var defer = $q.defer();
      defer.resolve();
      spyOn(scope, 'save').andReturn(defer.promise);
    }));

    it('should set submit flag if user is submitting the form', function () {
      scope.submit();
      scope.$apply();

      expect(scope.showSubmitErrors).toBeTruthy();
    });

    it('should save pod before submission', function () {
      scope.submit();

      expect(scope.save).toHaveBeenCalled();
    });

    it('should confirm before submission', inject(function (_$dialog_) {
      spyOn(OpenLmisDialog, 'newDialog');
      spyOn(ProofOfDelivery.prototype, 'error').andCallFake(function () {
        return {errorPages: null};
      });

      scope.submit();
      scope.$apply();

      expect(OpenLmisDialog.newDialog).toHaveBeenCalled();
      expect(OpenLmisDialog.newDialog.calls[0].args[0]).toEqual({id: 'confirmDialog', header: 'label.confirm.action', body: 'msg.question.confirmation'});
      expect(OpenLmisDialog.newDialog.calls[0].args[2]).toEqual(_$dialog_);
    }));

    it('should submit pod upon confirmation', function () {
      spyOn(OpenLmisDialog, 'newDialog');
      spyOn(ProofOfDelivery.prototype, 'error').andCallFake(function () {
        return {errorPages: null};
      });
      $httpBackend.expect('PUT', '/pods/submit/' + podId + '.json').respond(200, {success: 'successful'});

      scope.submit();
      scope.$apply();
      OpenLmisDialog.newDialog.calls[0].args[1](true);
      $httpBackend.flush();

      expect(scope.message).toEqual('successful');
      expect(scope.podAlreadySubmitted).toBeTruthy();
    });

    it('should not submit pod if not confirmed', function () {
      spyOn(OpenLmisDialog, 'newDialog');
      spyOn(ProofOfDelivery.prototype, 'error').andCallFake(function () {
        return {errorPages: null};
      });

      scope.submit();
      scope.$apply();
      OpenLmisDialog.newDialog.calls[0].args[1](false);
      scope.$apply();

      $httpBackend.verifyNoOutstandingExpectation();
      $httpBackend.verifyNoOutstandingRequest();
      expect(scope.order.alreadyConfirmed).toBeFalsy();
    });

    it('should validate pod before submitting and not proceed if invalid', function () {
      spyOn(OpenLmisDialog, 'newDialog');
      spyOn(ProofOfDelivery.prototype, 'error').andCallFake(function () {
        return {errorPages: [1, 2]};
      });

      scope.submit();
      scope.$apply();

      expect(ProofOfDelivery.prototype.error).toHaveBeenCalled();
      expect(OpenLmisDialog.newDialog).not.toHaveBeenCalled();
      expect(scope.error).toEqual('error.quantity.received.invalid');
      expect(scope.message).toBeUndefined();
    });

    it('should show error if submit fails', function () {
      spyOn(OpenLmisDialog, 'newDialog');
      spyOn(ProofOfDelivery.prototype, 'error').andCallFake(function () {
        return {errorPages: null};
      });
      $httpBackend.expect('PUT', '/pods/submit/' + podId + '.json').respond(404, {error: 'error'});

      scope.submit();
      scope.$apply();

      OpenLmisDialog.newDialog.calls[0].args[1](true);
      $httpBackend.flush();

      expect(scope.error).toEqual('error');
      expect(scope.message).toBeUndefined();
    });

  })

});