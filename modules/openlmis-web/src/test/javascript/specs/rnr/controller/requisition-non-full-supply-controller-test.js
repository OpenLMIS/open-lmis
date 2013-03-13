describe('RequisitionNonFullSupplyController', function () {

  var scope, ctrl, httpBackend, location, routeParams, controller, localStorageService, productList, categoryList, facilityApprovedProducts;

  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));
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
    scope.$parent.rnr = {"id": "rnrId", "lineItems": []};

    var category1 = {"id": 1, "name": "cat1", "code": "cat1Code"};
    var category2 = {"id": 2, "name": "cat2", "code": "cat2Code"};
    var category3 = {"id": 3, "name": "cat3", "code": "cat3Code"};
    categoryList = [category1, category2, category3];

    var product1 = {"id": 1, "code": "product1", "category": category1};
    var product2 = {"id": 2, "code": "product2", "category": category2};
    var product3 = {"id": 3, "code": "product3", "category": category3};
    var product4 = {"id": 4, "code": "product4", "category": category1};
    var product5 = {"id": 5, "code": "product5", "category": category2};

    var facilityApprovedProduct1 = {"programProduct": {"product": product1}};
    var facilityApprovedProduct2 = {"programProduct": {"product": product2}};
    var facilityApprovedProduct3 = {"programProduct": {"product": product3}};
    var facilityApprovedProduct4 = {"programProduct": {"product": product4}};
    var facilityApprovedProduct5 = {"programProduct": {"product": product5}};

    facilityApprovedProducts = [facilityApprovedProduct1, facilityApprovedProduct2, facilityApprovedProduct3, facilityApprovedProduct4, facilityApprovedProduct5];

    productList = [product1, product2, product3, product4, product5];

    $rootScope.fixToolBar = function () {
    };
    scope.facilityApprovedProducts = facilityApprovedProducts;
    ctrl = controller(RequisitionNonFullSupplyController, {$scope: scope, $location: location, $routeParams: routeParams, localStorageService: localStorageService});

    scope.allTypes = [
      {"name": "some name"},
      {"name": "some other name"}
    ];

  }));

  it('should create category list during page load', function () {
    expect(scope.nonFullSupplyProductsCategories).toEqual(categoryList);
  });

  it('should display non full supply addition modal window', function () {
    scope.nonFullSupplyLineItems = [];
    scope.facilityApprovedProducts = [];
    spyOn(scope,'resetNonFullSupplyModal')

    scope.showAddNonFullSupplyModal();

    expect(scope.nonFullSupplyProductsModal).toBeTruthy();
    expect(scope.newNonFullSupply).toBeUndefined();
    expect(scope.facilityApprovedProduct).toBeUndefined();
    expect(scope.resetNonFullSupplyModal).toHaveBeenCalled();
  });

//  it('should add non full supply line item to the list', function () {
//    scope.$parent.rnr = {"id": 1, "period": {}, "nonFullSupplyLineItems": []};
//    scope.facilityApprovedProducts = [];
//    scope.fillPagedGridData = function () {
//    };
//    var product = {
//      "form": {"code": "Tablet"},
//      "dosageUnit": {"code": "mg"},
//      "strength": "600", "code": "P999", "primaryName": "Antibiotics",
//      "dosesPerDispensingUnit": 3, "packSize": 10, "roundToZero": "false",
//      "packRoundingThreshold": "true", "dispensingUnit": "Strip", "fullSupply": false};
//
//    var programProduct = {"dosesPerMonth": 5, "currentPrice": 8, "product": product};
//
//    scope.facilityApprovedProduct = {"programProduct": programProduct, "maxMonthsOfStock": 3};
//    scope.newNonFullSupply = {"quantityRequested": 20, "reasonForRequestedQuantity": "Bad Weather"};
//    spyOn(scope, 'fillPagedGridData');
//    scope.addNonFullSupplyLineItemToRnr();
//
//    expect(scope.$parent.rnr.nonFullSupplyLineItems[0].quantityRequested).toEqual(20);
//    expect(scope.$parent.rnr.nonFullSupplyLineItems[0].reasonForRequestedQuantity).toEqual("Bad Weather");
//    expect(scope.$parent.rnr.nonFullSupplyLineItems[0].cost).toEqual(16.00.toFixed(2));
//    expect(scope.nonFullSupplyProductsToDisplay).toEqual(undefined);
//    expect(scope.$parent.rnr.nonFullSupplyItemsSubmittedCost).toEqual(16.00.toFixed(2));
//    expect(scope.fillPagedGridData).toHaveBeenCalled();
//  });

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
    var category1 = {"id": 1, "name": "cat1", "code": "cat1Code"};
    var category2 = {"id": 2, "name": "cat2", "code": "cat2Code"};
    var category3 = {"id": 3, "name": "cat3", "code": "cat3Code"};
    categoryList = [category1, category2, category3];

    scope.nonFullSupplyProductCategory = {name:"cat1"};

    var product1 = {"id": 1, "code": "product1", "category": category1};
    var product2 = {"id": 2, "code": "product2", "category": category1};
    var product3 = {"id": 3, "code": "product3", "category": category1};
    var product4 = {"id": 4, "code": "product4", "category": category1};
    var product5 = {"id": 5, "code": "product5", "category": category1};

    var facilityApprovedProduct1 = {"programProduct": {"product": product1}};
    var facilityApprovedProduct2 = {"programProduct": {"product": product2}};
    var facilityApprovedProduct3 = {"programProduct": {"product": product3}};
    var facilityApprovedProduct4 = {"programProduct": {"product": product4}};
    var facilityApprovedProduct5 = {"programProduct": {"product": product5}};

    scope.facilityApprovedProducts = [facilityApprovedProduct1, facilityApprovedProduct2, facilityApprovedProduct3, facilityApprovedProduct4, facilityApprovedProduct5];
    scope.addedNonFullSupplyProducts =[{"productCode":"product1" }];
    scope.rnr.nonFullSupplyLineItems = [{"id":5, "productCode":"product3"}];

    scope.updateNonFullSupplyProductsToDisplay();

    expect(scope.nonFullSupplyProductsToDisplay.length).toEqual(3);
    expect(scope.nonFullSupplyProductsToDisplay[0].programProduct).toEqual(facilityApprovedProduct2.programProduct);
    expect(scope.nonFullSupplyProductsToDisplay[1].programProduct).not.toEqual(facilityApprovedProduct1.programProduct);
    expect(scope.nonFullSupplyProductsToDisplay[2].programProduct).not.toEqual(facilityApprovedProduct3.programProduct);
  });

  it("Should add selected product to the list of products displayed in the addedNonFullSupplyProducts table",function(){
    var category1 = {"id": 1, "name": "cat1", "code": "cat1Code"};
    var product1 = {"id": 1, "code": "product1", "category": category1,"primaryName":"Product 1","form": {"code": "Tablet"},"dosageUnit": {"code": "mg"},"strength": "600", "code": "P999", "primaryName": "Antibiotics"};
    scope.facilityApprovedProduct = {"programProduct": {"product": product1}};
    scope.newNonFullSupply = {"quantityRequested": 20, "reasonForRequestedQuantity": "Bad Weather"};
    scope.addedNonFullSupplyProducts=[];
    spyOn(scope,'updateNonFullSupplyProductsToDisplay');
    var addedNonFullSupplyProductPrimaryName = scope.facilityApprovedProduct.programProduct.product.primaryName;
    var addedNonFullSupplyProductCode = scope.facilityApprovedProduct.programProduct.product.code;

    scope.addNonFullSupplyProductsByCategory();

    expect(scope.addedNonFullSupplyProducts[0].productCode).toEqual(addedNonFullSupplyProductCode);
    expect(scope.addedNonFullSupplyProducts[0].productName).toEqual(addedNonFullSupplyProductPrimaryName);
    expect(scope.addedNonFullSupplyProducts[0].quantityRequested).toEqual(20);
    expect(scope.addedNonFullSupplyProducts[0].reasonForRequestedQuantity).toEqual("Bad Weather");
    expect(scope.updateNonFullSupplyProductsToDisplay).toHaveBeenCalled();
  });

  it("Should delete the current non full supply line item when delete is clicked against the addedNonFullSupplyLineItem",function(){
    scope.addedNonFullSupplyProducts =[{"code":"code1" , "name":"Product1"}];
    spyOn(scope,'updateNonFullSupplyProductsToDisplay');

    scope.deleteCurrentNonFullSupplyLineItem(0);

    expect(scope.addedNonFullSupplyProducts.length == 0).toBeTruthy();
    expect(scope.updateNonFullSupplyProductsToDisplay).toHaveBeenCalled();

    var nonFullSupplyLineItem1 = {"code":"code2" , "name":"Product2"};
    var nonFullSupplyLineItem2 = {"code":"code3" , "name":"Product3"};
    scope.addedNonFullSupplyProducts.push(nonFullSupplyLineItem1);
    scope.addedNonFullSupplyProducts.push(nonFullSupplyLineItem2);

    scope.deleteCurrentNonFullSupplyLineItem(1);

    expect(scope.addedNonFullSupplyProducts.length == 1).toBeTruthy();
    expect(scope.addedNonFullSupplyProducts[0]==nonFullSupplyLineItem1).toBeTruthy();
  });

});

