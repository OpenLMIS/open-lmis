describe('RequisitionFullSupplyController', function () {
  var scope, ctrl, httpBackend, location, routeParams, controller, localStorageService;

  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));
  beforeEach(inject(function ($httpBackend, $rootScope, $location, $controller, $routeParams, _localStorageService_) {
    scope = $rootScope.$new();
    $rootScope.hasPermission = function () {
    };

    scope.isFormDisabled = function () {
      return false;
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

    httpBackend.when('GET', '/facilityApprovedProducts/facility/1/program/1/nonFullSupply.json').respond(200);
    httpBackend.when('POST', '/requisitions.json?facilityId=1&periodId=2&programId=1').respond({"rnr":{"status":"CREATED"}});
    httpBackend.when('GET', '/logistics/rnr/1/columns.json').respond({"rnrColumnList":[
      {"testField":"test"}
    ]});
    httpBackend.when('GET', '/reference-data/currency.json').respond({"currency":"$"});
    httpBackend.expect('GET', '/requisitions/lossAndAdjustments/reference-data.json').respond({"lossAdjustmentTypes":{}});
    $rootScope.fixToolBar = function () {
    };
    ctrl = controller(RequisitionFullSupplyController, {$scope:scope, $location:location, $routeParams:routeParams, localStorageService:localStorageService});

    scope.allTypes = [
      {"name":"some name"},
      {"name":"some other name"}
    ];
  }));

  it('should get list of Rnr Columns for program', function () {
    httpBackend.flush();
    expect(scope.programRnrColumnList).toEqual([
      {"testField":"test"}
    ]);
  });

  it("should display modal window with appropriate type options to add losses and adjustments", function () {
    var lineItem = { "id":"1", lossesAndAdjustments:[
      {"type":{"name":"some name"}, "quantity":"4"}
    ]};
    scope.showLossesAndAdjustmentModalForLineItem(lineItem);
    expect(scope.lossesAndAdjustmentsModal[1]).toBeTruthy();
    expect(scope.lossesAndAdjustmentTypesToDisplay).toEqual([
      {"name":"some other name"}
    ]);
  });

  it('should save Losses and Adjustments and close modal if valid', function () {
    var lineItem = { "id":"1", "beginningBalance":1, lossesAndAdjustments:[
      {"type":{"name":"some name"}, "quantity":"4"}
    ]};
    var rnrLineItem = new RnrLineItem(lineItem);

    scope.$parent.rnr.lineItems.push(rnrLineItem);
    spyOn(rnrLineItem, 'reEvaluateTotalLossesAndAdjustments');

    scope.$parent.rnr = {"id":"rnrId", lineItems:[rnrLineItem]};
    scope.programRnrColumnList = [
      {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}}
    ];

    scope.lossesAndAdjustmentsModal[1] = true;
    scope.saveLossesAndAdjustmentsForRnRLineItem(rnrLineItem);

    expect(rnrLineItem.reEvaluateTotalLossesAndAdjustments).toHaveBeenCalled();
    expect(scope.lossesAndAdjustmentsModal[1]).toBeFalsy();
    expect(scope.modalError).toEqual('');
  });

  it('should not save Losses and Adjustments and close modal if not valid', function () {
    var lineItem = { "id":"1", "beginningBalance":1, lossesAndAdjustments:[
      {"type":{"name":"some name"}, "quantity":null}
    ]};
    var rnrLineItem = new RnrLineItem(lineItem);

    scope.$parent.rnr.lineItems.push(rnrLineItem);
    spyOn(rnrLineItem, 'reEvaluateTotalLossesAndAdjustments');

    scope.$parent.rnr = {"id":"rnrId", lineItems:[rnrLineItem]};
    scope.programRnrColumnList = [
      {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}}
    ];
    scope.lossesAndAdjustmentsModal[1] = true;
    scope.saveLossesAndAdjustmentsForRnRLineItem(rnrLineItem);

    expect(rnrLineItem.reEvaluateTotalLossesAndAdjustments).not.toHaveBeenCalledWith(scope.$parent.rnr, scope.programRnrColumnList);
    expect(scope.lossesAndAdjustmentsModal[1]).toBeTruthy();
    expect(scope.modalError).toEqual('Please correct the highlighted fields before submitting');
  });
});

