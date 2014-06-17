/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("Create Facility Approved Product Controller", function () {

  beforeEach(module('openlmis'));

  var scope, parentScope, ctrl, supplyLine, $httpBackend, location, programs, facilityTypeList, messageService;

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $location, _messageService_) {
    scope = $rootScope.$new();
    parentScope = $rootScope.$new();
    scope.$parent = parentScope;
    $httpBackend = _$httpBackend_;
    location = $location;
    messageService = _messageService_;
    scope.query = "P10";
    supplyLine = {"program": {"name": "TB"}, "supplyingFacility": {"name": "supplying"}};

    programs = [
      {"name": "TB", "id": 1},
      {"name": "MALARIA", "id": 2}
    ];

    facilityTypeList = [
      {"id": 1, "name": "district1"},
      {"id": 2, "name": "district2"}
    ];
    ctrl = $controller('CreateFacilityApprovedProductController', {$scope: scope, facilityTypes: facilityTypeList, programs: programs, messageService: messageService});
  }));


  it("should load facility approved product", function () {
    parentScope.program = {"id": 2};
    parentScope.facilityType = {"id": 1};
    parentScope.facilityApprovedProductsModal = false;
    var product1 = {name: "n", code: "2 "};
    var product2 = {name: "a", code: "5 "};
    var product3 = {name: "n", code: "1 "};
    var productCategory1 = {name: "second product category", id: 1};
    var productCategory2 = {name: "first product category", id: 2};
    var programProductList = [
      {"product": product1, "productCategory": productCategory1},
      {"product": product2, "productCategory": productCategory2},
      {"product": product3, "productCategory": productCategory1}
    ];
    var data = {"programProductList": programProductList};

    $httpBackend.when("GET", '/programProducts/filter/programId/2/facilityTypeId/1.json').respond(data);
    scope.$parent.$apply(function () {
      scope.$parent.facilityApprovedProductsModal = true;
    });

    $httpBackend.flush();
    expect(scope.programProductList).toEqual(programProductList);
    expect(scope.productCategories).toEqual([productCategory1, productCategory2]);
  });

  it("should not load facility approved product if modal window is closed", function () {
    var httpBackendSpy = spyOn($httpBackend, 'expectGET');
    parentScope.facilityApprovedProductsModal = true;

    scope.$parent.$apply(function () {
      scope.$parent.facilityApprovedProductsModal = false;
    });

    expect(httpBackendSpy).not.toHaveBeenCalled();
  });

  it("should return headers", function () {
    spyOn(messageService, 'get');

    var header = scope.getHeader();
    expect(messageService.get).toHaveBeenCalledWith('header.code');
    expect(messageService.get).toHaveBeenCalledWith('header.name');
    expect(messageService.get).toHaveBeenCalledWith('header.strength');
    expect(messageService.get).toHaveBeenCalledWith('header.unit.of.measure');
    expect(messageService.get).toHaveBeenCalledWith('header.template.type');
    expect(header).toEqual("");
  });

  describe("Format Result and Selection", function () {
    it("should format results for full supply product", function () {
      spyOn(messageService, 'get').andReturn('Full supply');

      var product = {text: "productCode | productName | productStrength | productUnitOfMeasure | true"};
      var result = scope.formatResult(product);
      expect(result).toEqual("<div class='row-fluid'><div class='span2'>productCode </div><div class='span4'> productName </div><div class='span2'> productStrength </div><div class='span2'> productUnitOfMeasure </div><div class='span2'>Full supply</div></div>");
    });

    it("should format results for non full supply product", function () {
      spyOn(messageService, 'get').andReturn('Non full supply');

      var product = {text: "productCode | productName | productStrength | productUnitOfMeasure | false"};
      var result = scope.formatResult(product);
      expect(result).toEqual("<div class='row-fluid'><div class='span2'>productCode </div><div class='span4'> productName </div><div class='span2'> productStrength </div><div class='span2'> productUnitOfMeasure </div><div class='span2'>Non full supply</div></div>");
    });

    it("should format results for header", function () {
      var product = {text: "productCode | productName | productStrength | productUnitOfMeasure | type"};
      var result = scope.formatResult(product);
      expect(result).toEqual("<div class='row-fluid'><div class='span2'>productCode </div><div class='span4'> productName </div><div class='span2'> productStrength </div><div class='span2'> productUnitOfMeasure </div><div class='span2'>type</div></div>");
    });

    it("should return false for result if product is undefined", function () {
      var product = undefined;
      var result = scope.formatResult(product);
      expect(result).toBeFalsy();
    });

    it("should format selected value", function () {
      var product = {text: "productCode|productName|productStrength|productUnitOfMeasure|productType"};
      var result = scope.formatSelection(product);
      expect(result).toEqual("productCode - productName");
    });

    it("should return false for selection if product is undefined", function () {
      var product = undefined;
      var result = scope.formatSelection(product);
      expect(result).toBeFalsy();
    });
  });

  it("should filter products by category", function () {
    var product1 = {name: "n", code: "2 "};
    var product2 = {name: "a", code: "5 "};
    var product3 = {name: "n", code: "1 "};
    var productCategory1 = {name: "second product category", id: 1};
    var productCategory2 = {name: "first product category", id: 2};
    scope.programProductList = [
      {"product": product1, "productCategory": productCategory1},
      {"product": product2, "productCategory": productCategory2},
      {"product": product3, "productCategory": productCategory1}
    ];
    scope.productCategorySelected = productCategory1;

    scope.filterProductsByCategory();

    expect(scope.products).toEqual([product1, product3]);
  });

  describe("Add facility approved products button", function () {

    it("should enable when required fields are present", function () {
      scope.newFacilityTypeApprovedProduct = {maxMonthsOfStock: 12.34};
      scope.productCategorySelected = {};
      scope.productSelected = {};

      expect(scope.isAddDisabled()).toBeFalsy();
    });

    it("should disable when maxMonthStock not present", function () {
      scope.newFacilityTypeApprovedProduct = {maxMonthsOfStock: undefined};
      scope.productCategorySelected = {};
      scope.productSelected = {};

      expect(scope.isAddDisabled()).toBeTruthy();
    });

    it("should disable when product category is not selected", function () {
      scope.newFacilityTypeApprovedProduct = {maxMonthsOfStock: 12.34};
      scope.productCategorySelected = undefined;
      scope.productSelected = {};

      expect(scope.isAddDisabled()).toBeTruthy();
    });

    it("should disable when product not selected", function () {
      scope.newFacilityTypeApprovedProduct = {maxMonthsOfStock: 12.34};
      scope.productCategorySelected = {};
      scope.productSelected = undefined;

      expect(scope.isAddDisabled()).toBeTruthy();
    });

    it("should disable newFacilityTypeApprovedProduct is not present", function () {
      scope.newFacilityTypeApprovedProduct = undefined;
      scope.productCategorySelected = {};
      scope.productSelected = undefined;

      expect(scope.isAddDisabled()).toBeTruthy();
    });
  });

  describe('Add facility type approved product', function () {
    var facilityType, program, productSelectedString, product1, product2, product3, productCategory1, productCategory2;

    beforeEach(function () {
      product1 = {name: "n", code: "2"};
      product2 = {name: "a", code: "5"};
      product3 = {name: "n", code: "1"};
      productCategory1 = {name: "second product category", id: 1};
      productCategory2 = {name: "first product category", id: 2};

      facilityType = {name: "facility type"};
      program = {name: "program"};
      productSelectedString = "{\"name\": \"n\", \"code\": \"2\"}";

      parentScope.facilityType = facilityType;
      parentScope.program = program;
      scope.productSelected = productSelectedString;
      scope.newFacilityTypeApprovedProduct = {maxMonthsOfStock: 1, minMonthsOfStock: 13, eop: 12};
      scope.programProductList = [
        {"product": product1, "productCategory": productCategory1},
        {"product": product2, "productCategory": productCategory2},
        {"product": product3, "productCategory": productCategory1}
      ];

    });

    it('should add a new filled product to the added products list and clear modal data', function () {

      scope.addFacilityTypeApprovedProduct();

      expect(scope.addedFacilityTypeApprovedProducts).toEqual([
        {
          facilityType: facilityType,
          programProduct: {program: program, product: product1},
          maxMonthsOfStock: 1,
          minMonthsOfStock: 13,
          eop: 12
        }
      ]);
      expect(scope.programProductList).toEqual([
        {"product": product2, "productCategory": productCategory2},
        {"product": product3, "productCategory": productCategory1}
      ]);
      expect(scope.productCategorySelected).toBeUndefined();
      expect(scope.productSelected).toBeUndefined();
      expect(scope.products).toBeUndefined();
      expect(scope.newFacilityTypeApprovedProduct).toBeUndefined();
    });
  });
});
