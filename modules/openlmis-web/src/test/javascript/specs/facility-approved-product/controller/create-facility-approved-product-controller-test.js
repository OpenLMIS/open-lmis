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

  var scope, parentScope, grandParentScope, ctrl, supplyLine, $httpBackend, location, programs, facilityTypeList, messageService;

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $location, _messageService_) {
    scope = $rootScope.$new();
    parentScope = $rootScope.$new();
    grandParentScope = $rootScope.$new();
    grandParentScope.loadProducts = function () {
    };
    grandParentScope.focusSuccessMessageDiv = function () {
    };
    scope.$parent = parentScope;
    parentScope.$parent = grandParentScope;
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

  describe("ProgramProducts", function () {
    var product1, product2, product3, productCategory1, productCategory2, programProductList;

    beforeEach(function () {
      product1 = {name: "product1", code: "1"};
      product2 = {name: "product2", code: "2"};
      product3 = {name: "product3", code: "3"};
      productCategory1 = {name: "second product category", id: 1, code: 1};
      productCategory2 = {name: "first product category", id: 2, code: 2};
      programProductList = [
        {"product": product1, "productCategory": productCategory1},
        {"product": product2, "productCategory": productCategory2},
        {"product": product3, "productCategory": productCategory1}
      ];
    });

    it("should filter products by category", function () {
      scope.programProductList = programProductList;
      scope.productCategorySelected = productCategory1;

      scope.filterProductsByCategory();

      expect(scope.products).toEqual([product1, product3]);
    });

    describe('Add and remove facility type approved product', function () {
      var facilityType, program, productSelectedString;

      beforeEach(function () {
        facilityType = {name: "facility type"};
        program = {name: "program"};
        productSelectedString = "{\"name\": \"product1\", \"code\": \"1\"}";

        grandParentScope.facilityType = facilityType;
        grandParentScope.program = program;
        scope.productSelected = productSelectedString;
        scope.newFacilityTypeApprovedProduct = {maxMonthsOfStock: 1, minMonthsOfStock: 13, eop: 12};
        scope.programProductList = programProductList;
        scope.productCategorySelected = productCategory2;
      });

      it('should add a new filled product to the added products list and clear modal data', function () {

        scope.addedFacilityTypeApprovedProducts = [
          {
            facilityType: facilityType,
            programProduct: {program: program, product: product1, productCategory: productCategory1},
            maxMonthsOfStock: 3,
            minMonthsOfStock: 3,
            eop: 124
          }
        ];

        scope.addFacilityTypeApprovedProduct();

        expect(scope.addedFacilityTypeApprovedProducts).toEqual([
          {
            facilityType: facilityType,
            programProduct: {program: program, product: product1, productCategory: productCategory2},
            maxMonthsOfStock: 1,
            minMonthsOfStock: 13,
            eop: 12
          },
          {
            facilityType: facilityType,
            programProduct: {program: program, product: product1, productCategory: productCategory1},
            maxMonthsOfStock: 3,
            minMonthsOfStock: 3,
            eop: 124
          }
        ]);
        expect(scope.$parent.$parent.programProductList).toEqual([
          {"product": product2, "productCategory": productCategory2},
          {"product": product3, "productCategory": productCategory1}
        ]);
        expect(scope.selectedProgramProductList).toEqual([
          {"product": product1, "productCategory": productCategory1}
        ]);
        expect(scope.productCategorySelected).toBeUndefined();
        expect(scope.productSelected).toBeUndefined();
        expect(scope.products).toBeUndefined();
        expect(scope.newFacilityTypeApprovedProduct).toBeUndefined();
      });

      it('should remove product from list', function () {
        scope.programProductList = [
          {"product": product3, "productCategory": productCategory1}
        ];
        scope.selectedProgramProductList = [
          {"product": product1, "productCategory": productCategory1},
          {"product": product2, "productCategory": productCategory2}
        ];
        var facilityTypeApprovedProduct1 = {
          facilityType: facilityType,
          programProduct: {program: program, product: product1},
          maxMonthsOfStock: 1,
          minMonthsOfStock: 13,
          eop: 12
        };
        var facilityTypeApprovedProduct2 = {
          facilityType: facilityType,
          programProduct: {program: program, product: product2},
          maxMonthsOfStock: 10,
          minMonthsOfStock: 12,
          eop: 18
        };
        scope.addedFacilityTypeApprovedProducts = [facilityTypeApprovedProduct1, facilityTypeApprovedProduct2];
        scope.removeFacilityTypeApprovedProduct(1);

        expect(scope.addedFacilityTypeApprovedProducts).toEqual([facilityTypeApprovedProduct1]);
        expect(scope.programProductList).toEqual([
          {"product": product3, "productCategory": productCategory1},
          {"product": product2, "productCategory": productCategory2}
        ]);
        expect(scope.selectedProgramProductList).toEqual([
          {"product": product1, "productCategory": productCategory1}
        ]);
      });
    });
  });

  it("should return headers", function () {
    spyOn(messageService, 'get');

    var header = scope.getHeader();
    expect(messageService.get).toHaveBeenCalledWith('header.code');
    expect(messageService.get).toHaveBeenCalledWith('header.name');
    expect(messageService.get).toHaveBeenCalledWith('header.strength');
    expect(messageService.get).toHaveBeenCalledWith('header.unit.of.measure');
    expect(messageService.get).toHaveBeenCalledWith('header.template.type');
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

  it("should save facility type approved products", function () {
    spyOn(grandParentScope, 'focusSuccessMessageDiv');
    var successMessage = "Saved successfully";
    scope.addedFacilityTypeApprovedProducts = [
      {
        facilityType: {name: "facility type"},
        programProduct: {program: {name: "program1", code: "1"}, product: {name: "product1", code: "1"}},
        maxMonthsOfStock: 1,
        minMonthsOfStock: 13,
        eop: 12
      }
    ];
    spyOn(scope.$parent.$parent, 'loadProducts');
    $httpBackend.expectPOST('/facilityApprovedProducts.json', scope.addedFacilityTypeApprovedProducts).respond(200, {"success": successMessage});

    scope.addFacilityTypeApprovedProducts();

    $httpBackend.flush();

    expect(grandParentScope.message).toEqual(successMessage);
    expect(grandParentScope.facilityApprovedProductsModal).toBeFalsy();
    expect(grandParentScope.loadProducts).toHaveBeenCalledWith(1);
    expect(scope.addedFacilityTypeApprovedProducts).toEqual([]);
    expect(grandParentScope.focusSuccessMessageDiv).toHaveBeenCalled();
  });

  it("should not save facility type approved products if maxMonthStock is missing", function () {
    var errorMessage = "error.correct.highlighted";
    scope.addedFacilityTypeApprovedProducts = [
      {
        facilityType: {name: "facility type"},
        programProduct: {program: {name: "program1", code: "1"}, product: {name: "product1", code: "1"}},
        maxMonthsOfStock: 1,
        minMonthsOfStock: 13,
        eop: 12
      },
      {
        facilityType: {name: "facility type"},
        programProduct: {program: {name: "program1", code: "1"}, product: {name: "product1", code: "1"}},
        minMonthsOfStock: 13,
        eop: 12
      }
    ];
    scope.addFacilityTypeApprovedProducts();

    expect(scope.modalError).toEqual(errorMessage);
  });

  it("should not save facility type approved products if facilityType is missing", function () {
    var errorMessage = "error.correct.highlighted";
    scope.addedFacilityTypeApprovedProducts = [
      {
        facilityType: {name: "facility type"},
        programProduct: {program: {name: "program1", code: "1"}, product: {name: "product1", code: "1"}},
        maxMonthsOfStock: 1,
        minMonthsOfStock: 13,
        eop: 12
      },
      {
        programProduct: {program: {name: "program1", code: "1"}, product: {name: "product1", code: "1"}},
        minMonthsOfStock: 13,
        eop: 12
      }
    ];
    scope.addFacilityTypeApprovedProducts();

    expect(scope.modalError).toEqual(errorMessage);
  });

  it("should not save facility type approved products if programProduct is missing", function () {
    var errorMessage = "error.correct.highlighted";
    scope.addedFacilityTypeApprovedProducts = [
      {
        facilityType: {name: "facility type"},
        maxMonthsOfStock: 1,
        minMonthsOfStock: 13,
        eop: 12
      },
      {
        facilityType: {name: "facility type"},
        programProduct: {program: {name: "program1", code: "1"}, product: {name: "product1", code: "1"}},
        minMonthsOfStock: 13,
        eop: 12
      }
    ];
    scope.addFacilityTypeApprovedProducts();

    expect(scope.modalError).toEqual(errorMessage);
  });

  it("should set error message when error returned", function () {
    var errorMessage = "An error occurred. Please contact system administrator.";
    scope.addedFacilityTypeApprovedProducts = [
      {
        facilityType: {name: "facility type"},
        programProduct: {program: {name: "program1", code: "1"}, product: {name: "product1", code: "1"}},
        maxMonthsOfStock: 1,
        minMonthsOfStock: 13,
        eop: 12
      }
    ];
    $httpBackend.when("POST", '/facilityApprovedProducts.json').respond(404, {"error": errorMessage});

    scope.addFacilityTypeApprovedProducts();

    $httpBackend.flush();

    expect(scope.$parent.$parent.message).toBeUndefined();
    expect(scope.modalError).toEqual(errorMessage);
  });

  it("should not save facility type approved products if undefined", function () {
    var httpBackendSpy = spyOn($httpBackend, 'expectPOST');
    scope.addedFacilityTypeApprovedProducts = undefined;

    scope.addFacilityTypeApprovedProducts();

    expect(httpBackendSpy).not.toHaveBeenCalled();
  });

  it("should not save facility type approved products if empty", function () {
    var httpBackendSpy = spyOn($httpBackend, 'expectPOST');
    scope.addedFacilityTypeApprovedProducts = [];

    scope.addFacilityTypeApprovedProducts();

    expect(httpBackendSpy).not.toHaveBeenCalled();
  });


  it("should cancel save action", function () {

    scope.cancelFacilityTypeApprovedProducts();
    expect(grandParentScope.facilityApprovedProductsModal).toBeFalsy();
    expect(scope.addedFacilityTypeApprovedProducts).toEqual([]);
  });
});
