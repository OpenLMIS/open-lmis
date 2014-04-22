/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('CreateNonFullSupplyController', function () {

  var scope, ctrl, httpBackend, location, routeParams, controller, localStorageService, productList, categoryList, facilityApprovedProducts;
  var facilityApprovedProduct1, facilityApprovedProduct2, facilityApprovedProduct3, facilityApprovedProduct4, facilityApprovedProduct5;
  var category1, category2, category3;

  beforeEach(module('openlmis'));
  beforeEach(inject(function ($httpBackend, $rootScope, $location, $controller, $routeParams, _localStorageService_) {
    scope = $rootScope.$new();
    $rootScope.hasPermission = function () {
    };
    location = $location;
    controller = $controller;
    httpBackend = $httpBackend;
    scope.$parent.facility = "10134";
    scope.$parent.program = {"code": "programCode", "id": 1};

    scope.saveRnrForm = {$error: { rnrError: false }};
    localStorageService = _localStorageService_;
    routeParams = {"facility": "1", "program": "1", "period": 2};
    scope.$parent.rnr = {"id": "rnrId", "fullSupplyLineItems": [], "nonFullSupplyLineItems": []};

    category1 = {"id": 1, "name": "cat1", "code": "cat1Code"};
    category2 = {"id": 2, "name": "cat2", "code": "cat2Code"};
    category3 = {"id": 3, "name": "cat3", "code": "cat3Code"};
    categoryList = [category1, category2, category3];

    var product1 = {"id": 1, "code": "product1", "primaryName": "Product 1", "form": {"code": "Tablet"},
      "dosageUnit": {"code": "mg"}, "strength": "600"};
    var product2 = {"id": 2, "code": "product2"};
    var product3 = {"id": 3, "code": "product3"};
    var product4 = {"id": 4, "code": "product4"};
    var product5 = {"id": 5, "code": "product5"};

    facilityApprovedProduct1 = {"programProduct": {"product": product1, "dosesPerMonth": 3, "currentPrice": 4,"displayOrder": 2,  "productCategory": category1}, "maxMonthsOfStock": 3};
    facilityApprovedProduct2 = {"programProduct": {"product": product2, "productCategory": category2}};
    facilityApprovedProduct3 = {"programProduct": {"product": product3, "productCategory": category3}};
    facilityApprovedProduct4 = {"programProduct": {"product": product4, "productCategory": category1}};
    facilityApprovedProduct5 = {"programProduct": {"product": product5, "productCategory": category2}};

    facilityApprovedProducts = [facilityApprovedProduct1, facilityApprovedProduct2, facilityApprovedProduct3,
      facilityApprovedProduct4, facilityApprovedProduct5];

    productList = [product1, product2, product3, product4, product5];

    $rootScope.fixToolBar = function () {
    };

    scope.facilityApprovedProducts = facilityApprovedProducts;
    ctrl = controller(CreateNonFullSupplyController, {$scope: scope, $location: location, $routeParams: routeParams,
      localStorageService: localStorageService});

  }));

  it('should create category list during page load', function () {
    expect(scope.nonFullSupplyProductsCategories).toEqual(categoryList);
  });

  it('should display non full supply addition modal window', function () {
    scope.facilityApprovedProducts = [];
    spyOn(scope, 'resetNonFullSupplyModal');

    scope.showAddNonFullSupplyModal();

    expect(scope.nonFullSupplyProductsModal).toBeTruthy();
    expect(scope.newNonFullSupply).toBeUndefined();
    expect(scope.facilityApprovedProduct).toBeUndefined();
    expect(scope.resetNonFullSupplyModal).toHaveBeenCalled();
  });

  it('should add non full supply products to the rnr non full supply line items when Done is clicked', function () {
    scope.$parent.$parent = {$parent: {}};
    scope.page = {"nonFullSupply": []};
    scope.pageSize = 2;
    scope.currentPage = 1;
    var fillPacksToShip = function () {
    };
    scope.fillPagedGridData = function () {
    };
    scope.$parent.rnr = {"id": 1, "period": {}, "nonFullSupplyLineItems": [], "fillPacksToShip": fillPacksToShip};

    scope.addedNonFullSupplyProducts = [new RegularRnrLineItem({"code": "code2", "name": "Product2", "quantityRequested": 20, "reasonForRequestedQuantity": "rain", "isNonNumeric": false})];
    spyOn(scope, '$emit');
    scope.addNonFullSupplyLineItemsToRnr();

    expect(scope.$parent.rnr.nonFullSupplyLineItems.length).toEqual(1);
    expect(scope.$parent.rnr.nonFullSupplyLineItems[0].quantityRequested).toEqual(20);
    expect(scope.$parent.rnr.nonFullSupplyLineItems[0].reasonForRequestedQuantity).toEqual("rain");
    expect(scope.page.nonFullSupply.length).toEqual(1);
    expect(scope.page.nonFullSupply[0].reasonForRequestedQuantity).toEqual("rain");
    expect(scope.nonFullSupplyProductsModal).toBeFalsy();
  });

  it('should add non full supply products to the rnr non full supply line items  in sorted order when Done is clicked', function () {
    scope.$parent.$parent = {$parent: {}};
    scope.page = {"nonFullSupply": []};
    scope.pageSize = 2;
    scope.currentPage = 1;
    var fillPacksToShip = function () {
    };
    scope.fillPagedGridData = function () {
    };
    scope.$parent.rnr = {"id": 1, "period": {}, "nonFullSupplyLineItems": [], "fillPacksToShip": fillPacksToShip};

    var rnrLineItem1 = new RegularRnrLineItem({productCategoryDisplayOrder: 2});
    var rnrLineItem2 = new RegularRnrLineItem({productCategoryDisplayOrder: 1});
    var rnrLineItem3 = new RegularRnrLineItem({productCategoryDisplayOrder: 3});
    spyOn(scope, '$emit');
    scope.addedNonFullSupplyProducts = [rnrLineItem1, rnrLineItem2, rnrLineItem3];

    spyOn(rnrLineItem1, "validateQuantityRequestedAndReason").andReturn(false);
    spyOn(rnrLineItem2, "validateQuantityRequestedAndReason").andReturn(false);
    spyOn(rnrLineItem3, "validateQuantityRequestedAndReason").andReturn(false);

    scope.addNonFullSupplyLineItemsToRnr();

    expect(scope.$parent.rnr.nonFullSupplyLineItems.length).toEqual(3);
    expect(scope.$parent.rnr.nonFullSupplyLineItems[0]).toEqual(rnrLineItem2);
    expect(scope.$parent.rnr.nonFullSupplyLineItems[1]).toEqual(rnrLineItem1);
    expect(scope.$parent.rnr.nonFullSupplyLineItems[2]).toEqual(rnrLineItem3);

    expect(scope.page.nonFullSupply.length).toEqual(2);
    expect(scope.page.nonFullSupply[0]).toEqual(rnrLineItem2);
    expect(scope.page.nonFullSupply[1]).toEqual(rnrLineItem1);
  });

  it('should not add non full supply products to the rnr non full supply line items if any product is not valid', function () {
    var fillPacksToShip = function () {
    };
    scope.fillPagedGridData = function () {
    };
    scope.nonFullSupplyProductsModal = true;
    scope.$parent.rnr = {"id": 1, "period": {}, "nonFullSupplyLineItems": [], "fillPacksToShip": fillPacksToShip};

    scope.addedNonFullSupplyProducts = [new RegularRnrLineItem({"code": "code2", "name": "Product2", "quantityRequested": "", "reasonForRequestedQuantity": "rain", "isNonNumeric": false})];

    scope.addNonFullSupplyLineItemsToRnr();

    expect(scope.$parent.rnr.nonFullSupplyLineItems.length).toEqual(0);
    expect(scope.nonFullSupplyProductsModal).toBeTruthy();

    scope.addedNonFullSupplyProducts = [new RegularRnrLineItem({"code": "code2", "name": "Product2", "quantityRequested": "3", "reasonForRequestedQuantity": "", "isNonNumeric": false})];

    scope.addNonFullSupplyLineItemsToRnr();

    expect(scope.$parent.rnr.nonFullSupplyLineItems.length).toEqual(0);
    expect(scope.nonFullSupplyProductsModal).toBeTruthy();
  });

  it('should disable add button if any of the required fields have not been set or have errors', function () {
    expect(scope.shouldDisableAddButton()).toEqual(true);

    scope.newNonFullSupply = {};
    expect(scope.shouldDisableAddButton()).toEqual(true);

    scope.newNonFullSupply = {"quantityRequested": 10};
    expect(scope.shouldDisableAddButton()).toEqual(true);

    scope.newNonFullSupply = {"quantityRequested": 10, "reasonForRequestedQuantity": "test"};
    expect(scope.shouldDisableAddButton()).toEqual(true);

    scope.newNonFullSupply = {"quantityRequested": 10, "reasonForRequestedQuantity": "test"};
    scope.facilityApprovedProduct = {};
    expect(scope.shouldDisableAddButton()).toEqual(false);
  });

  it('should filter non full supply products based on selected category and previously added products(modal window) and products added to the non-full supply tab', function () {
    scope.nonFullSupplyProductCategory = {name: "cat1"};

    scope.addedNonFullSupplyProducts = [
      {"productCode": "product1" }
    ];
    scope.rnr.nonFullSupplyLineItems = [
      {"id": 5, "productCode": "product3"}
    ];

    scope.updateNonFullSupplyProductsToDisplay();

    expect(scope.nonFullSupplyProductsToDisplay.length).toEqual(1);
    expect(scope.nonFullSupplyProductsToDisplay[0].programProduct).toEqual(facilityApprovedProduct4.programProduct);
  });

  it("Should add selected product to the list of non full supply products displayed on the modal", function () {
    scope.facilityApprovedProduct = facilityApprovedProduct1;
    scope.rnr = {period: {numberOfMonths: 3}};
    scope.nonFullSupplyProductCategory = {displayOrder: 5};

    scope.facilityApprovedProduct = facilityApprovedProduct1;
    scope.programRnrColumnList = [
      {"name": "newPatientCount", "source": {"name": "USER_INPUT"}, "configuredOption": {"name": "newPatientCount"}}
    ];
    scope.newNonFullSupply = new RegularRnrLineItem({"quantityRequested": 20, "reasonForRequestedQuantity": "Bad Weather"}, null, scope.programRnrColumnList);
    scope.addedNonFullSupplyProducts = [];
    spyOn(scope, 'updateNonFullSupplyProductsToDisplay').andReturn(true);
    scope.addNonFullSupplyProductsByCategory();

    expect(scope.addedNonFullSupplyProducts[0].productCode).toEqual("product1");
    expect(scope.addedNonFullSupplyProducts[0].productName).toEqual("Product 1");
    expect(scope.addedNonFullSupplyProducts[0].quantityRequested).toEqual(20);
    expect(scope.addedNonFullSupplyProducts[0].reasonForRequestedQuantity).toEqual("Bad Weather");
    expect(scope.addedNonFullSupplyProducts[0].maxMonthsOfStock).toEqual(3);
    expect(scope.addedNonFullSupplyProducts[0].dosesPerMonth).toEqual(3);
    expect(scope.addedNonFullSupplyProducts[0].price).toEqual(4);
    expect(scope.addedNonFullSupplyProducts[0].productCategory).toEqual("cat1");
    expect(scope.addedNonFullSupplyProducts[0].totalLossesAndAdjustments).toEqual(0);
    expect(scope.addedNonFullSupplyProducts[0].rnrId).toEqual("rnrId");
    expect(scope.updateNonFullSupplyProductsToDisplay).toHaveBeenCalled();
  });

  it("Should delete non full supply product when delete is clicked against that added non full supply product", function () {
    scope.addedNonFullSupplyProducts = [
      {"code": "code1", "name": "Product1"}
    ];
    spyOn(scope, 'updateNonFullSupplyProductsToDisplay');

    scope.deleteCurrentNonFullSupplyLineItem(0);

    expect(scope.addedNonFullSupplyProducts.length).toEqual(0);
    expect(scope.updateNonFullSupplyProductsToDisplay).toHaveBeenCalled();

    scope.addedNonFullSupplyProducts = [
      {"code": "code2", "name": "Product2"},
      {"code": "code3", "name": "Product3"}
    ];

    scope.deleteCurrentNonFullSupplyLineItem(1);

    expect(scope.addedNonFullSupplyProducts.length).toEqual(1);
    expect(scope.addedNonFullSupplyProducts[0].code).toEqual("code2");
  });

});

