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
      ctrl = $controller('ProductController', {$scope: scope, productGroups: [], productForms: [], dosageUnits: [], programs: [], categories: [], PriceSchCategories: [], productDTO: productDTO});
    }));

    it('should set product last updated date in scope', function () {
      expect(scope.productLastUpdated).toEqual("23/12/2014");
    });

    it('should set newProgramProduct as empty object in scope', function () {
      expect(scope.newProgramProduct).toEqual({active: false});
    });

    it('should not set selected product form, group and dosage unit in scope if program product is undefined', function () {
      expect(scope.selectedProductFormCode).toBeUndefined();
      expect(scope.selectedProductGroupCode).toBeUndefined();
      expect(scope.selectedProductDosageUnitCode).toBeUndefined();
    });

    it('should not set selected product form, group and dosage unit in scope if product values are undefined', function () {
      var productDTO = {product: {form: undefined, productGroup: undefined, dosageUnit: undefined}};

      ctrl = controller('ProductController', {$scope: scope, productGroups: [], productForms: [], dosageUnits: [], programs: [], categories: [], PriceSchCategories: [], productDTO: productDTO});

      expect(scope.selectedProductFormCode).toBeUndefined();
      expect(scope.selectedProductGroupCode).toBeUndefined();
      expect(scope.selectedProductDosageUnitCode).toBeUndefined();
    });

    it('should filter already added programs from the list', function () {
      var vaccineProgram = {id: 1, code: 'Vaccines', name: 'Vaccines'};
      var TBProgram = {id: 2, code: 'TB', name: "TB"};
      var hivProgram = {id: 3, code: 'HIV', name: "HIV"};
      var productDTO = {product: {form: {code: "p10"}}, programProducts: [
        {program: hivProgram}
      ]};

      ctrl = controller('ProductController', {$scope: scope, productGroups: [], productForms: [], dosageUnits: [], programs: [vaccineProgram, hivProgram, TBProgram], categories: [], PriceSchCategories: [], productDTO: productDTO});

      expect(scope.programs).toEqual([vaccineProgram, TBProgram]);
    });

    it('should set selected product form, group and dosage unit in scope if product values are defined', function () {
      var productDTO = {product: {form: {code: "Form"}, productGroup: {code: "Group"}, dosageUnit: {code: "Unit"}}};

      ctrl = controller('ProductController', {$scope: scope, productGroups: [], productForms: [], dosageUnits: [], programs: [], categories: [], PriceSchCategories: [], productDTO: productDTO});

      expect(scope.selectedProductFormCode).toEqual("Form");
      expect(scope.selectedProductGroupCode).toEqual("Group");
      expect(scope.selectedProductDosageUnitCode).toEqual("Unit");
    });

    it('should not set productLastUpdated and initialize product, programProducts in scope if productDTO is undefined', function () {
      scope = rootScope.$new();
      ctrl = controller('ProductController', {$scope: scope, productGroups: [], productForms: [], dosageUnits: [], programs: [], categories: [], PriceSchCategories: [], productDTO: undefined});

      expect(scope.productLastUpdated).toBeUndefined();
      expect(scope.programProducts).toEqual([]);
      expect(scope.product).toEqual({});
    });

    it('should set product groups, forms, dosage units, programs and categories in scope', function () {
      var productGroup1 = {code: 'group1'};
      var productForm1 = {code: 'form1'};
      var dosageUnit1 = {code: 'unit1'};
      var vaccineProgram = {id: 1, code: 'Vaccines', name: 'Vaccines'};
      var hivProgram = {id: 2, code: 'HIV', name: "HIV"};

      var productCategory1 = {code: 'category1'};
      var programProduct1 = {program: vaccineProgram, productCategory: productCategory1, active: true,
        displayOrder: 23, dosesPerMonth: 1234, currentPrice: "67.67"};

       var productScheduleCategory1 = {id:1, price_category: "A"};

      var productDTO = {product: {code: 'p10'}, productLastUpdated: "23/12/2014", programProducts: [programProduct1]};

      ctrl = controller('ProductController', {$scope: scope, productGroups: [productGroup1], productForms: [ productForm1],
        dosageUnits: [dosageUnit1], programs: [hivProgram, vaccineProgram ], categories: [ productCategory1], PriceSchCategories: [ productScheduleCategory1 ], productDTO: productDTO});

      expect(scope.productGroups).toEqual([productGroup1]);
      expect(scope.productForms).toEqual([productForm1]);
      expect(scope.dosageUnits).toEqual([dosageUnit1]);
      expect(scope.programs).toEqual([hivProgram]);
      expect(scope.categories).toEqual([productCategory1]);
      expect(scope.priceScheduleCategories).toEqual([productScheduleCategory1]);
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

      it('should not save product if program products are under edit', function () {
        scope.productForm = {"$error": {"required": false}};
        scope.programProducts = [
          {program: {code: 'P1'}, underEdit: true},
          {program: {code: 'P2'}, underEdit: false}
        ];

        scope.save();

        expect(scope.error).toEqual("error.program.products.not.done");
      });

      it('should insert product', function () {
        scope.product = {"code": 'P10'};
        scope.selectedProductFormCode = "form2";
        scope.selectedProductGroupCode = "group2";
        scope.selectedProductDosageUnitCode = "unit2";
        scope.productForm = {"$error": {"required": false}};

        $httpBackend.expectPOST('/products.json', {product: scope.product, programProducts: scope.programProducts, productPriceSchedules: scope.priceSchedules}).respond(200, {"success": "Saved successfully", "productId": 5});
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

        $httpBackend.expectPOST('/products.json', {product: scope.product, programProducts: scope.programProducts, productPriceSchedules: scope.priceSchedules}).respond(400, {"error": "Some error occurred"});
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

        $httpBackend.expectPUT('/products/1.json', {product: scope.product, programProducts: scope.programProducts, productPriceSchedules: scope.priceSchedules}).respond(200, {"success": "Updated successfully", "productId": 5});
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

        $httpBackend.expectPUT('/products/1.json', {product: scope.product, programProducts: scope.programProducts, productPriceSchedules: scope.priceSchedules}).respond(400, {"error": "Some error occurred"});
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
      var productCategory1, productCategory2, vaccineProgram, hivProgram;

      beforeEach(function () {
        productCategory1 = {id: 12, name: "Category1"};
        productCategory2 = {id: 23, name: "Category2"};
        vaccineProgram = {name: "Vaccines"};
        hivProgram = {name: "HIV"};
        scope.programProducts = [
          {program: vaccineProgram, productCategory: productCategory1, active: true,
            displayOrder: 23, dosesPerMonth: 1234, currentPrice: "67.67"}
        ];
      });

      it('should edit vaccineProgram product and retain all previous values', function () {
        scope.edit(0);

        expect(scope.programProducts[0].underEdit).toBeTruthy();
        expect(scope.programProducts[0].previousProgramProduct.program).toEqual(vaccineProgram);
        expect(scope.programProducts[0].previousProgramProduct.productCategory).toEqual(productCategory1);
        expect(scope.programProducts[0].previousProgramProduct.active).toBeTruthy();
        expect(scope.programProducts[0].previousProgramProduct.displayOrder).toEqual(23);
        expect(scope.programProducts[0].previousProgramProduct.dosesPerMonth).toEqual(1234);
        expect(scope.programProducts[0].previousProgramProduct.currentPrice).toEqual("67.67");
      });

      it('should cancel editing', function () {
        scope.programProducts[0].previousProgramProduct = {program: hivProgram, productCategory: productCategory2, active: false,
          displayOrder: 22, dosesPerMonth: 333, currentPrice: "12.5"};

        scope.cancelEdit(0);

        expect(scope.programProducts[0].underEdit).toBeFalsy();
        expect(scope.programProducts[0].program).toEqual(hivProgram);
        expect(scope.programProducts[0].productCategory).toEqual(productCategory2);
        expect(scope.programProducts[0].active).toBeFalsy();
        expect(scope.programProducts[0].displayOrder).toEqual(22);
        expect(scope.programProducts[0].dosesPerMonth).toEqual(333);
        expect(scope.programProducts[0].currentPrice).toEqual("12.5");
        expect(scope.programProducts[0].previousProgramProduct).toBeUndefined();
      });

      it("should update category", function () {
        scope.categories = [productCategory1, productCategory2];
        scope.programProducts = [
          {program: {name: "Vaccines"}, productCategory: {id: 23, name: "other category"}, active: true,
            displayOrder: 23, dosesPerMonth: 1234, currentPrice: "67.67"}
        ];

        scope.updateCategory(0);

        expect(scope.programProducts[0].productCategory).toEqual(productCategory2);
      });
    });

    describe('Add', function () {

      it('should add new program product', function () {
        var productCategory1 = {name: "Category1"};
        var productCategory2 = {name: "Category2"};
        var hivProgram = {name: "HIV"};
        var TBProgram = {id: 2, code: 'TB', name: "TB"};
        var vaccineProgram = {name: "Vaccines"};
        scope.programs = [hivProgram, vaccineProgram, TBProgram];
        var programProduct1 = {program: vaccineProgram, productCategory: productCategory1, active: true,
          displayOrder: 23, dosesPerMonth: 1234, currentPrice: "67.67"};

        var programProduct2 = {program: hivProgram, productCategory: productCategory2, active: false,
          displayOrder: 22, dosesPerMonth: 333, currentPrice: "12.5"};

        scope.newProgramProduct = programProduct2;

        scope.programProducts = [programProduct1];

        scope.addNewProgramProduct();

        expect(scope.programProducts).toEqual([programProduct1, programProduct2]);
        expect(scope.programs).toEqual([TBProgram]);
        expect(scope.newProgramProduct).toEqual({active: false});
      });
    });

    describe('Mandatory fields', function () {
      it('should return false if all mandatory fields are filled', function () {
        var programProduct = {program: {name: "Vaccines"}, productCategory: {name: "Category1"}, active: true, dosesPerMonth: 1234};

        var areMandatoryFieldsNotFilled = scope.mandatoryFieldsNotFilled(programProduct);

        expect(areMandatoryFieldsNotFilled).toBeFalsy();
      });

      it('should return true if program not selected', function () {
        var programProduct = {productCategory: {name: "Category1"}, active: true, dosesPerMonth: 1234};

        var areMandatoryFieldsNotFilled = scope.mandatoryFieldsNotFilled(programProduct);

        expect(areMandatoryFieldsNotFilled).toBeTruthy();
      });

      it('should return true if category not selected', function () {
        var programProduct = {program: {name: "Vaccines"}, active: false, dosesPerMonth: 1234};

        var areMandatoryFieldsNotFilled = scope.mandatoryFieldsNotFilled(programProduct);

        expect(areMandatoryFieldsNotFilled).toBeTruthy();
      });

      it('should return true if dosesPerMonth is undefined', function () {
        var programProduct = {program: {name: "Vaccines"}, active: false};

        var areMandatoryFieldsNotFilled = scope.mandatoryFieldsNotFilled(programProduct);

        expect(areMandatoryFieldsNotFilled).toBeTruthy();
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
