/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe("Product", function () {

  beforeEach(module('openlmis'));

  describe("Controller", function () {

    var ctrl, scope, $httpBackend, location;
    describe("Save", function () {

      beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $location) {
        scope = $rootScope.$new();
        $httpBackend = _$httpBackend_;
        location = $location;
        ctrl = $controller('ProductController', {$scope: scope, productGroups: [], productForms: [], dosageUnits: []});
      }));

      it('should not save product if invalid', function () {
        scope.productForm = {"$error": {"required": true}};

        scope.save();

        expect(scope.error).toEqual("form.error");
        expect(scope.showError).toBeTruthy();
      });

      it('should insert product', function () {
        scope.programProduct.product = {"code": 'P10'};
        scope.productForm = {"$error": {"required": false}};

        $httpBackend.expectPOST('/programProducts.json', scope.programProduct).respond(200, {"success": "Saved successfully", "productId": 5});
        scope.save();
        $httpBackend.flush();

        expect(scope.error).toEqual("");
        expect(scope.showError).toBeFalsy();
        expect(scope.$parent.productId).toEqual(5);
        expect(scope.$parent.message).toEqual("Saved successfully");
      });

      it('should not insert product', function () {
        scope.programProduct.product = {"code": 'P10'};
        scope.productForm = {"$error": {"required": false}};

        $httpBackend.expectPOST('/programProducts.json', scope.programProduct).respond(400, {"error": "Some error occurred"});
        scope.save();
        $httpBackend.flush();

        expect(scope.error).toEqual("Some error occurred");
        expect(scope.showError).toBeTruthy();
        expect(scope.$parent.message).toEqual("");
      });
    });
  });

  describe("Resolve", function () {
    var $httpBackend, ctrl, $timeout, $route, $q;
    var deferredObject;

    beforeEach(inject(function (_$httpBackend_, $controller, _$timeout_, _$route_) {
      $httpBackend = _$httpBackend_;
      deferredObject = {promise: {id: 1}, resolve: function () {
      }};
      spyOn(deferredObject, 'resolve');
      $q = {defer: function () {
        return deferredObject
      }};
      $timeout = _$timeout_;
      ctrl = $controller;
      $route = _$route_;
    }));

    it('should get product groups', function () {
      $httpBackend.expect('GET', '/products/groups.json').respond({productGroup: {'id': '23', 'code': 'PG'}});
      ctrl(ProductController.resolve.productGroups, {$q: $q});
      $timeout.flush();
      $httpBackend.flush();
      expect(deferredObject.resolve).toHaveBeenCalled();
    });

    it('should get product forms', function () {
      $httpBackend.expect('GET', '/products/forms.json').respond({productForm: {'id': '23', 'code': 'PF'}});
      ctrl(ProductController.resolve.productForms, {$q: $q});
      $timeout.flush();
      $httpBackend.flush();
      expect(deferredObject.resolve).toHaveBeenCalled();
    });

    it('should get dosage units', function () {
      $httpBackend.expect('GET', '/products/dosageUnits.json').respond({unit: {'id': '23', 'code': 'DU'}});
      ctrl(ProductController.resolve.dosageUnits, {$q: $q});
      $timeout.flush();
      $httpBackend.flush();
      expect(deferredObject.resolve).toHaveBeenCalled();
    });
  });
});