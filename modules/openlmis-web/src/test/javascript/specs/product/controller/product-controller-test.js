/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe("Product Controller", function () {

  var scope, httpBackend, ctrl, location, product, response;
  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $location) {
    scope = $rootScope.$new();
    httpBackend = _$httpBackend_;
    location = $location;

    product = {"id": 1, "code": "state", "name": "state", "level": {"code": "state", "name": "state"}};

    response = {"ProductList": [
      {"id": 2, "code": "Mozambique", "name": "Mozambique", "level": {"name": "Country", "levelNumber": 1}},
      {"id": 1, "code": "Root", "name": "Root", "level": {"name": "Country", "levelNumber": 1}}
    ]};

    ctrl = $controller;
    ctrl('ProductController', {$scope: scope, product: product});

    httpBackend.when("GET", '/products/id.json').respond(response);
    httpBackend.flush();
  }));

  xit('should set product in scope', function () {
    expect(scope.product).toEqual(product);
  });

  xit('should take to search page on cancel', function () {
    scope.cancel();
    expect(scope.$parent.productId).toBeUndefined();
    expect(scope.$parent.message).toEqual("");
    expect(location.path()).toEqual('/#/search');
  });

  xit('should save product', function () {
    ctrl('ProductController', {$scope: scope, product: undefined});
    var newProduct = {"code": "state", "name": "state", "level": {"code": "state", "name": "state"}};
    scope.product = newProduct;
    scope.productForm = {"$error": {"pattern": false, "required": false}};

    httpBackend.expectPOST('/products.json', newProduct).respond(200, {"success": "Saved successfully", "product": product});
    scope.save();
    httpBackend.flush();

    expect(scope.error).toEqual("");
    expect(scope.showError).toBeFalsy();
    expect(scope.$parent.message).toEqual("Saved successfully");
    expect(scope.$parent.productId).toEqual(product.id);
  });

  xit('should update product', function () {
    scope.productForm = {"$error": {"pattern": false, "required": false}};

    httpBackend.expectPUT('/products/' + product.id + '.json', product).respond(200, {"success": "Saved successfully", "product": product});
    scope.save();
    httpBackend.flush();

    expect(scope.error).toEqual("");
    expect(scope.showError).toBeFalsy();
    expect(scope.$parent.message).toEqual("Saved successfully");
    expect(scope.$parent.productId).toEqual(product.id);
  });

  xit('should throw error if product invalid', function () {
    scope.productForm = {"$error": {"pattern": false, "required": false}};

    httpBackend.expectPUT('/products/' + product.id + '.json', product).respond(400, {"error": "failed to update"});
    scope.save();
    httpBackend.flush();

    expect(scope.error).toEqual("failed to update");
    expect(scope.showError).toBeTruthy();
    expect(scope.$parent.message).toEqual("");
  });

  xit('should not save product if invalid', function () {
    scope.productForm = {"$error": {"pattern": true, "required": false}};

    scope.save();

    expect(scope.error).toEqual("form.error");
    expect(scope.showError).toBeTruthy();
    expect(scope.message).toEqual("");
  });

  xit('should not save product if invalid', function () {
    scope.productForm = {"$error": {"pattern": false, "required": true}};

    scope.save();

    expect(scope.error).toEqual("form.error");
    expect(scope.showError).toBeTruthy();
    expect(scope.message).toEqual("");
  });

});