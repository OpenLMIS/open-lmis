describe('CreateRequisitionController', function () {

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
    var mockedRequisition = {'status':"INITIATED", fullSupplyItemsSubmittedCost : 100, nonFullSupplyItemsSubmittedCost: 14};
    httpBackend.when('GET', '/requisitions.json?facilityId=1&periodId=2&programId=1').respond({'rnr':mockedRequisition});

    controller(CreateRequisitionController, {$scope:scope, $routeParams:routeParams});

    httpBackend.flush();

    expect(scope.rnr).toEqual(mockedRequisition);
  });


  it('should prepare period display name', function () {
    scope.rnr = {'status':"INITIATED"};
    scope.rnr.period = {"name":"Period 1", "startDate":1358274600000, "endDate":1367260200000};
    httpBackend.when('GET', '/requisitions.json?facilityId=1&periodId=2&programId=1').respond({'rnr':scope.rnr});
    controller(CreateRequisitionController, {$scope:scope, $routeParams:routeParams});
    expect(scope.periodDisplayName()).toEqual('16/01/2013 - 30/04/2013');
  });
});
