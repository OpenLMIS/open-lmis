describe('RequisitionController', function () {

  var scope, ctrl, httpBackend, location, routeParams, requisitionHeader, controller;

  beforeEach(module('openlmis.services'));

  beforeEach(inject(function ($httpBackend, $rootScope, $location, $controller) {
    scope = $rootScope.$new();
    location = $location;
    controller = $controller;
    httpBackend = $httpBackend;
    $rootScope.hasPermission = function() {};
    scope.$parent.facility = "10134";
    scope.$parent.program = {code:"programCode", "id":1};
    scope.saveRnrForm = {$error:{ rnrError:false }};
    routeParams = {"facility":"1", "program":"1", "period": 2};
  }));

  it('should set rnr in scope after successful initialization', function () {
    var mockedRequisition = {'status':"INITIATED"};
    httpBackend.when('GET', '/requisitions.json?facilityId=1&periodId=2&programId=1').respond({'rnr':mockedRequisition});

    controller(RequisitionController, {$scope:scope, $routeParams:routeParams});

    httpBackend.flush();

    expect(scope.rnr).toEqual(mockedRequisition);
  });

  it('should calculated and set 2 decimal rounded cost in rnrLineItem', function () {
    var mockedRequisition = {'status':"INITIATED", 'lineItems':[
      {'id':456, 'product':'Name', 'lossesAndAdjustments':[], 'packsToShip':10.333, 'price':2 }
    ]};
    httpBackend.when('GET', '/requisitions.json?facilityId=1&periodId=2&programId=1').respond({'rnr':mockedRequisition});
    controller(RequisitionController, {$scope:scope, $routeParams:routeParams});
    httpBackend.flush();

    expect(scope.rnrLineItems[0].id).toEqual(mockedRequisition.lineItems[0].id);
    expect(scope.rnrLineItems[0].cost).toEqual(20.67);
  });

  it('should set cost to zero if dependent fields are not present', function () {
    var mockedRequisition = {'status':"INITIATED", 'lineItems':[
      {'id':400, 'product':'Name', 'lossesAndAdjustments':[], 'price':2 },
      {'id':500, 'product':'Name', 'lossesAndAdjustments':[], 'packsToShip':10.333 }
    ]};
    httpBackend.when('GET', '/requisitions.json?facilityId=1&periodId=2&programId=1').respond({'rnr':mockedRequisition});
    controller(RequisitionController, {$scope:scope, $routeParams:routeParams});
    httpBackend.flush();

    expect(scope.rnrLineItems[0].id).toEqual(mockedRequisition.lineItems[0].id);
    expect(scope.rnrLineItems[0].cost).toEqual(0);
    expect(scope.rnrLineItems[1].id).toEqual(mockedRequisition.lineItems[1].id);
    expect(scope.rnrLineItems[1].cost).toEqual(0);
  });


  it('should initialize losses and adjustments, if not present in R&R', function () {
    var mockedRequisition = {'status':"INITIATED",
      'lineItems':[
        {'id':123, 'product':'Commodity Name' },
        {'id':456, 'product':'2nd Commodity', 'lossesAndAdjustments':[
          {'quantity':33}
        ] }
      ]
    };
    httpBackend.when('GET', '/requisitions.json?facilityId=1&periodId=2&programId=1').respond({'rnr':mockedRequisition});
    controller(RequisitionController, {$scope:scope, $routeParams:routeParams});
    httpBackend.flush();

    expect(scope.rnrLineItems[0].lossesAndAdjustments).toEqual([]);
    expect(scope.rnrLineItems[1].lossesAndAdjustments).toEqual([
      {'quantity':33}
    ]);
  });

  it('should prepare period display name', function () {
    scope.rnr = {'status':"INITIATED"};
    scope.rnr.period = {"name":"Period 1", "startDate":1358274600000, "endDate":1367260200000};
    httpBackend.when('GET', '/requisitions.json?facilityId=1&periodId=2&programId=1').respond({'rnr':scope.rnr});
    controller(RequisitionController, {$scope:scope, $routeParams:routeParams});
    expect(scope.periodDisplayName()).toEqual('16/01/2013 - 30/04/2013');
  });
});
