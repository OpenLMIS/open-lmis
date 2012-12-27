describe('CreateRnrController', function () {

  var scope, ctrl, httpBackend, location, route, requisitionHeader, controller;

  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));

  beforeEach(inject(function ($httpBackend, $rootScope, $location, $controller, $route, _localStorageService_) {
    scope = $rootScope.$new();
    location = $location;
    controller = $controller;
    httpBackend = $httpBackend;
    scope.$parent.facility = "10134";
    scope.$parent.program = {code:"programCode", "id":1};
    scope.saveRnrForm = {$error:{ rnrError:false }};
    localStorageService = _localStorageService_;
    route = $route;
    route.current = {"params":{"facility":"1", "program":"1"}};

    requisitionHeader = {"requisitionHeader":{"facilityName":"National Warehouse",
      "facilityCode":"10134", "facilityType":{"code":"Warehouse"}, "facilityOperatedBy":"MoH", "maximumStockLevel":3, "emergencyOrderPoint":0.5,
      "zone":{"label":"state", "value":"Arusha"}, "parentZone":{"label":"state", "value":"Arusha"}}};


    httpBackend.when('GET', '/logistics/facility/1/requisition-header.json').respond(requisitionHeader);
    httpBackend.when('POST', '/logistics/rnr/facility/1/program/1.json').respond({"rnr":{"status":"CREATED"}});
    httpBackend.when('GET', '/logistics/rnr/1/columns.json').respond({"rnrColumnList":[
      {"testField":"test"}
    ]});
    httpBackend.when('GET', '/reference-data/currency.json').respond({"currency":"$"});
    httpBackend.expect('GET', '/logistics/rnr/facility/1/program/1.json').respond({"rnr":{"status":"CREATED"}});
    ctrl = controller(CreateRnrController, {$scope:scope, $location:location, $route:route, localStorageService:localStorageService});
  }));

  it('should set rnr in scope after successful initialization', function () {

    httpBackend.flush();
    expect(scope.rnr).toEqual({"status":"CREATED"});
  });

  it('should get list of Rnr Columns for program', function () {
    httpBackend.flush();
    expect([
      {"testField":"test"}
    ]).toEqual(scope.programRnRColumnList);
  });

  it('should save work in progress for rnr', function () {
    scope.rnr = {"id":"rnrId"};
    httpBackend.expect('PUT', '/logistics/rnr/facility/1/program/1.json').respond(200);
    scope.saveRnr();
    httpBackend.flush();
    expect(scope.message).toEqual("R&R saved successfully!");
  });

  it('should not save work in progress when invalid form', function () {
    scope.saveRnrForm.$error.rnrError = true;
    scope.saveRnr();
    expect(scope.error).toEqual("Please correct errors before saving.");
  });

  it('should get Currency from service', function () {
    httpBackend.flush();
    expect(scope.currency).toEqual("$");
  });

  it("should get undefined when the column name is quantityApproved and status is INITIATED", function () {
    scope.rnr = {"status":"INITIATED"};
    var isShown = scope.showSelectedColumn("quantityApproved");
    expect(isShown).toEqual(undefined);
  });

  it("should get undefined when the column name is quantityApproved and status is CREATED", function () {
    scope.rnr = {"status":"CREATED"};
    var isShown = scope.showSelectedColumn("quantityApproved");
    expect(isShown).toEqual(undefined);
  });

  it("should get 'defined' when the column name is not quantityApproved", function () {
    scope.rnr = {"status":"whatever"};
    var isShown = scope.showSelectedColumn("anyOtherColumn");
    expect(isShown).toEqual("defined");
  });

  it("should get 'defined' when the column name is quantityApproved and status is SUBMITTED", function () {
    scope.rnr = {"status":"SUBMITTED"};
    var isShown = scope.showSelectedColumn("quantityApproved");
    expect(isShown).toEqual("defined");
  });

  it("should get 'defined' when the column name is quantityApproved and status is SUBMITTED", function () {
    scope.rnr = {"status":"APPROVED"};
    var isShown = scope.showSelectedColumn("quantityApproved");
    expect(isShown).toEqual("defined");
  });
});
