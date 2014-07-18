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

    var ctrl, scope, $httpBackend, location, controller, rootScope;
    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $location) {
      rootScope = $rootScope;
      scope = $rootScope.$new();
      $httpBackend = _$httpBackend_;
      location = $location;
      controller = $controller;
      var productDTO = {product: undefined, productLastUpdated: "23/12/2014"};
      ctrl = $controller('ProductController', {$scope: scope, productGroups: [], productForms: [], dosageUnits: [], programs: [], categories: [], productDTO: productDTO});
    }));

    it('should set product last updated date in scope', function () {
      expect(scope.productLastUpdated).toEqual("23/12/2014");
    });

    it('should not set selected product form, group and dosage unit in scope if program product is undefined', function () {
      expect(scope.selectedProductFormCode).toBeUndefined();
      expect(scope.selectedProductGroupCode).toBeUndefined();
      expect(scope.selectedProductDosageUnitCode).toBeUndefined();
    });

    it('should not set selected product form, group and dosage unit in scope if product values are undefined', function () {
      var productDTO = {product: {form: undefined, productGroup: undefined, dosageUnit: undefined}};

      ctrl = controller('ProductController', {$scope: scope, productGroups: [], productForms: [], dosageUnits: [], programs: [], categories: [], productDTO: productDTO});

      expect(scope.selectedProductFormCode).toBeUndefined();
      expect(scope.selectedProductGroupCode).toBeUndefined();
      expect(scope.selectedProductDosageUnitCode).toBeUndefined();
    });

    it('should set selected product form, group and dosage unit in scope if product values are defined', function () {
      var productDTO = {product: {form: {code: "Form"}, productGroup: {code: "Group"}, dosageUnit: {code: "Unit"}}};

      ctrl = controller('ProductController', {$scope: scope, productGroups: [], productForms: [], dosageUnits: [], programs: [], categories: [], productDTO: productDTO});

      expect(scope.selectedProductFormCode).toEqual("Form");
      expect(scope.selectedProductGroupCode).toEqual("Group");
      expect(scope.selectedProductDosageUnitCode).toEqual("Unit");
    });

    it('should not set product and productLastUpdated in scope if productDTO is undefined', function () {
      scope = rootScope.$new();
      ctrl = controller('ProductController', {$scope: scope, productGroups: [], productForms: [], dosageUnits: [], programs: [], categories: [], productDTO: undefined});

      expect(scope.product).toBeUndefined();
      expect(scope.productLastUpdated).toBeUndefined();
    });

    it('should set product groups, forms, dosage units, programs and catgeories in scope', function () {
      ctrl = controller('ProductController', {$scope: scope, productGroups: [
        {code: 'group1'}
      ], productForms: [
        {code: 'form1'}
      ],
        dosageUnits: [
          {code: 'unit1'}
        ], programs: [
          {code: 'program1'}
        ], categories: [
          {code: 'category1'}
        ], productDTO: undefined});

      expect(scope.productGroups).toEqual([
        {code: 'group1'}
      ]);
      expect(scope.productForms).toEqual([
        {code: 'form1'}
      ]);
      expect(scope.dosageUnits).toEqual([
        {code: 'unit1'}
      ]);
      expect(scope.programs).toEqual([
        {code: 'program1'}
      ]);
      expect(scope.categories).toEqual([
        {code: 'category1'}
      ]);
    });

    describe("Save", function () {
      beforeEach(function () {
        scope.productGroups = [
          {code: 'group1'},
          {code: 'group2'},
          {code: 'group3'}
        ];
        scope.productForms = [
          {code: 'form1'},
          {code: 'form2'},
          {code: 'form3'}
        ];
        scope.dosageUnits = [
          {code: 'unit1'},
          {code: 'unit2'},
          {code: 'unit3'}
        ];
        scope.product = {id: 1, code: 'code'};
      });

      it('should not save product if invalid', function () {
        scope.productForm = {"$error": {"required": true}};

        scope.save();

        expect(scope.error).toEqual("form.error");
        expect(scope.showError).toBeTruthy();
      });

      it('should insert product', function () {
        scope.product = {"code": 'P10'};
        scope.selectedProductFormCode = "form2";
        scope.selectedProductGroupCode = "group2";
        scope.selectedProductDosageUnitCode = "unit2";
        scope.productForm = {"$error": {"required": false}};

        $httpBackend.expectPOST('/products.json', {product: scope.product}).respond(200, {"success": "Saved successfully", "productId": 5});
        scope.save();
        $httpBackend.flush();

        expect(scope.product.productGroup.code).toEqual("group2");
        expect(scope.product.form.code).toEqual("form2");
        expect(scope.product.dosageUnit.code).toEqual("unit2");
        expect(scope.error).toEqual("");
        expect(scope.showError).toBeFalsy();
        expect(scope.$parent.productId).toEqual(5);
        expect(scope.$parent.message).toEqual("Saved successfully");
      });

      it('should not insert product', function () {
        scope.product = {"code": 'P10'};
        scope.productForm = {"$error": {"required": false}};

        $httpBackend.expectPOST('/products.json', {product: scope.product}).respond(400, {"error": "Some error occurred"});
        scope.save();
        $httpBackend.flush();

        expect(scope.error).toEqual("Some error occurred");
        expect(scope.showError).toBeTruthy();
        expect(scope.$parent.message).toEqual("");
      });

      it('should update product', function () {
        scope.product = {"id": 1, "code": 'P10'};
        scope.selectedProductFormCode = "form3";
        scope.selectedProductGroupCode = "group3";
        scope.selectedProductDosageUnitCode = "unit3";
        scope.productForm = {"$error": {"required": false}};

        $httpBackend.expectPUT('/products/1.json', {product: scope.product}).respond(200, {"success": "Updated successfully", "productId": 5});
        scope.save();
        $httpBackend.flush();

        expect(scope.product.productGroup.code).toEqual("group3");
        expect(scope.product.form.code).toEqual("form3");
        expect(scope.product.dosageUnit.code).toEqual("unit3");
        expect(scope.error).toEqual("");
        expect(scope.showError).toBeFalsy();
        expect(scope.$parent.productId).toEqual(5);
        expect(scope.$parent.message).toEqual("Updated successfully");
      });

      it('should not update product', function () {
        scope.product = {"id": 1, "code": 'P10'};
        scope.productForm = {"$error": {"required": false}};

        $httpBackend.expectPUT('/products/1.json', {product: scope.product}).respond(400, {"error": "Some error occurred"});
        scope.save();
        $httpBackend.flush();

        expect(scope.error).toEqual("Some error occurred");
        expect(scope.showError).toBeTruthy();
        expect(scope.$parent.message).toEqual("");
      });
    });

    it('should take to search page on cancel', function () {
      scope.cancel();
      expect(scope.$parent.parentId).toBeUndefined();
      expect(scope.$parent.message).toEqual("");
      expect(location.path()).toEqual('/#/search');
    });

    describe('Edit', function () {
      it('should edit program product and retain all previous values', function () {
        var productCategory = {name: "Category1"};
        var program = {name: "Vaccines"};
        scope.programProducts = [
          {program: program, productCategory: productCategory, active: true,
            displayOrder: 23, dosesPerMonth: 1234, currentPrice: "67.67"}
        ];

        scope.edit(0);

        expect(scope.programProducts[0].underEdit).toBeTruthy();
        expect(scope.currentProgramProduct.program).toEqual(program);
        expect(scope.currentProgramProduct.productCategory).toEqual(productCategory);
        expect(scope.currentProgramProduct.active).toBeTruthy();
        expect(scope.currentProgramProduct.displayOrder).toEqual(23);
        expect(scope.currentProgramProduct.dosesPerMonth).toEqual(1234);
        expect(scope.currentProgramProduct.currentPrice).toEqual("67.67");
      });

      it('should cancel editing', function () {
        var productCategory1 = {name: "Category1"};
        var productCategory2 = {name: "Category2"};
        var hivProgram = {name: "HIV"};
        scope.programProducts = [
          {program: {name: "Vaccines"}, productCategory: productCategory1, active: true,
            displayOrder: 23, dosesPerMonth: 1234, currentPrice: "67.67"}
        ];
        scope.currentProgramProduct = {program: hivProgram, productCategory: productCategory2, active: false,
          displayOrder: 22, dosesPerMonth: 333, currentPrice: "12.5"};

        scope.cancelEdit(0);

        expect(scope.programProducts[0].underEdit).toBeFalsy();
        expect(scope.programProducts[0].program).toEqual(hivProgram);
        expect(scope.programProducts[0].productCategory).toEqual(productCategory2);
        expect(scope.programProducts[0].active).toBeFalsy();
        expect(scope.programProducts[0].displayOrder).toEqual(22);
        expect(scope.programProducts[0].dosesPerMonth).toEqual(333);
        expect(scope.programProducts[0].currentPrice).toEqual("12.5");
        expect(scope.currentProgramProduct).toBeUndefined();
      });

      it("should update category", function () {
        var productCategory1 = {id: 12, name: "Category1"};
        var productCategory2 = {id: 23, name: "Category2"};
        scope.categories = [productCategory1, productCategory2];
        scope.programProducts = [
          {program: {name: "Vaccines"}, productCategory: {id: 23, name: "other category"}, active: true,
            displayOrder: 23, dosesPerMonth: 1234, currentPrice: "67.67"}
        ];

        scope.updateCategory(0);

        expect(scope.programProducts[0].productCategory).toEqual(productCategory2);
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

    it('should get programs', function () {
      $httpBackend.expect('GET', '/programs.json').respond({programs: [
        {code: 'p1'},
        {code: 'p2'}
      ]});
      ctrl(ProductController.resolve.programs, {$q: $q});
      $timeout.flush();
      $httpBackend.flush();
      expect(deferredObject.resolve).toHaveBeenCalled();
    });

    it('should get categories', function () {
      $httpBackend.expect('GET', '/products/categories.json').respond({productCategoryList: [
        {code: 'p1'},
        {code: 'p2'}
      ]});
      ctrl(ProductController.resolve.categories, {$q: $q});
      $timeout.flush();
      $httpBackend.flush();
      expect(deferredObject.resolve).toHaveBeenCalled();
    });

    it('should get productDTO if edit route contains id', function () {
      var productDTO = {product: {'id': '23'}, productLastUpdated: undefined};
      $route = {current: {params: {id: 1}}};
      $httpBackend.expect('GET', '/products/1.json').respond(productDTO);
      ctrl(ProductController.resolve.productDTO, {$q: $q, $route: $route});
      $timeout.flush();
      $httpBackend.flush();
      expect(deferredObject.resolve).toHaveBeenCalled();
    });

    it('should not get productDTO if edit route does not contains id', function () {
      var httpBackendSpy = spyOn($httpBackend, 'expectGET');

      $route = {current: {params: {id: undefined}}};
      ctrl(ProductController.resolve.productDTO, {$route: $route});

      expect(httpBackendSpy).not.toHaveBeenCalled();
    });
  });
});