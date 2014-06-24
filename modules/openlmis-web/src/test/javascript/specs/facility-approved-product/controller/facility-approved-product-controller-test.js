/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("Facility Approved Product", function () {

  beforeEach(module('openlmis'));

  describe("Controller", function () {
    var scope, ctrl, supplyLine, $httpBackend, location, programs, facilityTypeList;

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $location) {
      scope = $rootScope.$new();
      $httpBackend = _$httpBackend_;
      location = $location;
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
      ctrl = $controller('FacilityApprovedProductController', {$scope: scope, facilityTypes: facilityTypeList, programs: programs});
    }));

    it("should set currentPage, programs and facility types in scope", function () {
      expect(scope.facilityTypes).toEqual(facilityTypeList);
      expect(scope.programs).toEqual(programs);
      expect(scope.currentPage).toEqual(1);
      expect(scope.showResults).toEqual(false);
    });

    it("should clear search and load products", function () {
      spyOn(scope, 'loadProducts');

      scope.clearSearch();

      expect(scope.query).toEqual("");
      expect(scope.loadProducts).toHaveBeenCalledWith(1);
    });

    it('should trigger search on enter key', function () {
      var event = {"keyCode": 13};
      var searchSpy = spyOn(scope, 'loadProducts');

      scope.triggerSearch(event);

      expect(searchSpy).toHaveBeenCalledWith(1);
    });

    it('should not trigger search on any other key then enter', function () {
      var event = {"keyCode": 65};
      var searchSpy = spyOn(scope, 'loadProducts');

      scope.triggerSearch(event);

      expect(searchSpy).not.toHaveBeenCalled();
    });

    it('should get results according to specified page', function () {
      scope.currentPage = 5;
      var searchSpy = spyOn(scope, 'loadProducts');

      scope.$apply(function () {
        scope.currentPage = 6;
      });

      expect(searchSpy).toHaveBeenCalledWith(6);
    });

    it('should not get results if specified page is 0', function () {
      scope.currentPage = 3;
      var searchSpy = spyOn(scope, 'loadProducts');

      scope.$apply(function () {
        scope.currentPage = 0;
      });

      expect(searchSpy).not.toHaveBeenCalled();
    });

    it("should load products based on facilityType, program and search query", function () {
      scope.program = {"id": 2};
      scope.facilityType = {"id": 6};

      var response = {"facilityApprovedProducts": [
        {"name": "fap"}
      ], "pagination": {"totalRecords": 2, "page": 1}};

      $httpBackend.when("GET", '/facilityApprovedProducts.json?facilityTypeId=6&page=1&programId=2&searchParam=P10').respond(response);
      scope.loadProducts(1);
      $httpBackend.flush();

      expect(scope.facilityApprovedProducts).toEqual([
        {"name": "fap"}
      ]);
      expect(scope.pagination).toEqual(response.pagination);
      expect(scope.currentPage).toEqual(1);
      expect(scope.totalItems).toEqual(2);
      expect(scope.showResults).toEqual(true);
    });

    it("should load all products based on facilityType and program if no query specified", function () {
      scope.program = {"id": 2};
      scope.facilityType = {"id": 6};
      scope.query = "";

      var response = {"facilityApprovedProducts": [
        {"name": "fap"}
      ], "pagination": {"totalRecords": 2, "page": 1}};

      $httpBackend.when("GET", '/facilityApprovedProducts.json?facilityTypeId=6&page=1&programId=2&searchParam=').respond(response);
      scope.loadProducts(1);
      $httpBackend.flush();

      expect(scope.facilityApprovedProducts).toEqual([
        {"name": "fap"}
      ]);
      expect(scope.pagination).toEqual(response.pagination);
      expect(scope.currentPage).toEqual(1);
      expect(scope.totalItems).toEqual(2);
      expect(scope.showResults).toEqual(true);
    });

    it("should not load products if facilityType not selected", function () {
      spyOn($httpBackend, 'expectGET');
      scope.program = {"id": 2};

      scope.loadProducts(1);

      expect($httpBackend.expectGET).not.toHaveBeenCalled();
    });

    it("should not load products if program not selected", function () {
      spyOn($httpBackend, 'expectGET');
      scope.facilityType = {"id": 2};

      scope.loadProducts(1);

      expect($httpBackend.expectGET).not.toHaveBeenCalled();
    });

    it("should show category of first element", function () {
      var product1, product2, product3, productCategory1, productCategory2, programProductList;

      product1 = {name: "product1", code: "1"};
      product2 = {name: "product2", code: "2"};
      product3 = {name: "product3", code: "3"};
      productCategory1 = {name: "first product category", id: 1, code: 1};
      productCategory2 = {name: "second product category", id: 2, code: 2};
      programProductList = [
        {"programProduct": {"product": product1, "productCategory": productCategory1}},
        {"programProduct": {"product": product3, "productCategory": productCategory1}},
        {"programProduct": {"product": product2, "productCategory": productCategory2}}
      ];

      expect(scope.showCategory(programProductList, 0)).toBeTruthy();
    });

    it("should not show category of second element", function () {
      var product1, product2, product3, productCategory1, productCategory2, programProductList;

      product1 = {name: "product1", code: "1"};
      product2 = {name: "product2", code: "2"};
      product3 = {name: "product3", code: "3"};
      productCategory1 = {name: "first product category", id: 1, code: 1};
      productCategory2 = {name: "second product category", id: 2, code: 2};
      programProductList = [
        {"programProduct": {"product": product1, "productCategory": productCategory1}},
        {"programProduct": {"product": product3, "productCategory": productCategory1}},
        {"programProduct": {"product": product2, "productCategory": productCategory2}}
      ];

      expect(scope.showCategory(programProductList, 1)).toBeFalsy();
    });

    it('should edit facility approved product', function () {
      var facilityApprovedProduct = {
        facilityType: {},
        programProduct: {program: {}, product: {}, productCategory: {}},
        maxMonthsOfStock: 3,
        minMonthsOfStock: 4,
        eop: 124
      };

      scope.edit(facilityApprovedProduct);

      expect(facilityApprovedProduct.underEdit).toBeTruthy();
      expect(facilityApprovedProduct.previousMaxMonthsOfStock).toEqual(3);
      expect(facilityApprovedProduct.previousMinMonthsOfStock).toEqual(4);
      expect(facilityApprovedProduct.previousEop).toEqual(124);
    });

    it('should cancel editing of facility approved product', function () {
      var facilityApprovedProduct = {
        facilityType: {},
        programProduct: {program: {}, product: {}, productCategory: {}},
        maxMonthsOfStock: 30,
        minMonthsOfStock: 40,
        eop: 120,
        previousMaxMonthsOfStock: 3,
        previousMinMonthsOfStock: 4,
        previousEop: 124
      };

      scope.cancel(facilityApprovedProduct);

      expect(facilityApprovedProduct.underEdit).toBeFalsy();
      expect(facilityApprovedProduct.maxMonthsOfStock).toEqual(3);
      expect(facilityApprovedProduct.minMonthsOfStock).toEqual(4);
      expect(facilityApprovedProduct.eop).toEqual(124);
    });

    it('should update facility approved product', function () {
      var successMessage = "Updated successfully";
      scope.currentPage = 0;
      scope.program = {"id": 1};
      scope.facilityType = {"id": 2};
      var facilityApprovedProduct = {
        facilityType: {},
        programProduct: {program: {}, product: {}, productCategory: {}},
        maxMonthsOfStock: 30,
        minMonthsOfStock: 40,
        eop: 120,
        previousMaxMonthsOfStock: 3,
        previousMinMonthsOfStock: 4,
        previousEop: 124
      };
      facilityApprovedProduct.underEdit = true;
      $httpBackend.expectPUT('/facilityApprovedProducts.json', facilityApprovedProduct).respond(200, {"success": successMessage});

      scope.update(facilityApprovedProduct);

      $httpBackend.flush();

      expect(facilityApprovedProduct.facilityType).toEqual({"id": 2});
      expect(facilityApprovedProduct.programProduct.program).toEqual({"id": 1});
      expect(facilityApprovedProduct.underEdit).toBeFalsy();
      expect(scope.message).toEqual("Updated successfully");
    });

    it('should not update facility approved product', function () {
      var errorMessage = "some error occurred. Please contact system admin.";
      scope.currentPage = 0;
      scope.program = {"id": 1};
      scope.facilityType = {"id": 2};

      var facilityApprovedProduct = {
        facilityType: {},
        programProduct: {program: {}, product: {}, productCategory: {}},
        maxMonthsOfStock: 30,
        minMonthsOfStock: 40,
        eop: 120,
        previousMaxMonthsOfStock: 3,
        previousMinMonthsOfStock: 4,
        previousEop: 124
      };
      $httpBackend.expectPUT('/facilityApprovedProducts.json', facilityApprovedProduct).respond(400, {"error": errorMessage});

      scope.update(facilityApprovedProduct);

      $httpBackend.flush();

      expect(facilityApprovedProduct.facilityType).toEqual({"id": 2});
      expect(facilityApprovedProduct.programProduct.program).toEqual({"id": 1});
      expect(scope.message).toEqual("some error occurred. Please contact system admin.");
    });

    it('should not update facility approved product if mandatory field missing', function () {
      spyOn($httpBackend, 'expectPUT');

      var facilityApprovedProduct = {
        facilityType: {},
        programProduct: {program: {}, product: {}, productCategory: {}},
        minMonthsOfStock: 40,
        eop: 120,
        previousMaxMonthsOfStock: 3,
        previousMinMonthsOfStock: 4,
        previousEop: 124
      };

      scope.update(facilityApprovedProduct);

      expect(scope.error).toEqual("error.correct.highlighted");
      expect(facilityApprovedProduct.facilityType).toEqual({});
      expect(facilityApprovedProduct.programProduct.program).toEqual({});
      expect($httpBackend.expectPUT).not.toHaveBeenCalled();
    });
  });
});
