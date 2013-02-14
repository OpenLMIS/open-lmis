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

    httpBackend.expect('GET', '/requisitions/lossAndAdjustments/reference-data.json').respond({"lossAdjustmentTypes":{}});
    $rootScope.fixToolBar = function () {
    };
    ctrl = controller(RequisitionFullSupplyController, {$scope:scope, $location:location, $routeParams:routeParams, localStorageService:localStorageService});

    scope.allTypes = [
      {"name":"some name"},
      {"name":"some other name"}
    ];
  }));

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

