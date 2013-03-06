describe('RequisitionNonFullSupplyController', function () {

  var scope, ctrl, httpBackend, location, routeParams, controller, localStorageService;

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
    scope.$parent.program = {code:"programCode", "id":1};

    scope.saveRnrForm = {$error:{ rnrError:false }};
    localStorageService = _localStorageService_;
    routeParams = {"facility":"1", "program":"1", "period":2};
    scope.$parent.rnr = {"id":"rnrId", "lineItems":[]};

    httpBackend.expect('GET', '/facilityApprovedProducts/facility/1/program/1/nonFullSupply.json').respond(200);
    $rootScope.fixToolBar = function () {
    };
    ctrl = controller(RequisitionNonFullSupplyController, {$scope:scope, $location:location, $routeParams:routeParams, localStorageService:localStorageService});

    scope.allTypes = [
      {"name":"some name"},
      {"name":"some other name"}
    ];
  }));

  it('should display non full supply addition modal window', function () {
    scope.nonFullSupplyLineItems = [];
    scope.nonFullSupplyProducts = [];
    scope.showAddNonFullSupplyModal();
    expect(scope.nonFullSupplyProductsModal).toBeTruthy();
    expect(scope.newNonFullSupply).toBeUndefined();
  });

  it('should add non full supply line item to the list', function () {
    scope.$parent.rnr = {"id":1, "period":{}, "nonFullSupplyLineItems":[]};
    scope.nonFullSupplyProducts = [];
    scope.fillPagedGridData = function(){};
    var product = {
      "form":{"code":"Tablet"},
      "dosageUnit":{"code":"mg"},
      "strength":"600", "code":"P999", "primaryName":"Antibiotics",
      "dosesPerDispensingUnit":3, "packSize":10, "roundToZero":"false",
      "packRoundingThreshold":"true", "dispensingUnit":"Strip", "fullSupply":false};

    var programProduct = {"dosesPerMonth":5, "currentPrice":8, "product":product};

    scope.facilityApprovedProduct = {"programProduct":programProduct, "maxMonthsOfStock":3};
    scope.newNonFullSupply = {"quantityRequested":20, "reasonForRequestedQuantity":"Bad Weather"};
    spyOn(scope, 'fillPagedGridData');
    scope.addNonFullSupplyLineItem();

    expect(scope.$parent.rnr.nonFullSupplyLineItems[0].quantityRequested).toEqual(20);
    expect(scope.$parent.rnr.nonFullSupplyLineItems[0].reasonForRequestedQuantity).toEqual("Bad Weather");
    expect(scope.$parent.rnr.nonFullSupplyLineItems[0].cost).toEqual(16.00.toFixed(2));
    expect(scope.nonFullSupplyProductsToDisplay).toEqual([]);
    expect(scope.$parent.rnr.nonFullSupplyItemsSubmittedCost).toEqual(16.00.toFixed(2));
    expect(scope.fillPagedGridData).toHaveBeenCalled();
  });

  it('should disable add button if any of the required fields have not been set or have errors', function () {
    expect(scope.shouldDisableAddButton()).toEqual(true);

    scope.newNonFullSupply = {};
    expect(scope.shouldDisableAddButton()).toEqual(true);

    scope.newNonFullSupply = {"quantityRequested":10};
    expect(scope.shouldDisableAddButton()).toEqual(true);

    scope.newNonFullSupply = {"quantityRequested":10, "reasonForRequestedQuantity":"test"};
    expect(scope.shouldDisableAddButton()).toEqual(true);

    scope.newNonFullSupply = {"quantityRequested":10, "reasonForRequestedQuantity":"test"};
    scope.facilityApprovedProduct = {};
    expect(scope.shouldDisableAddButton()).toEqual(false);
  });
});

