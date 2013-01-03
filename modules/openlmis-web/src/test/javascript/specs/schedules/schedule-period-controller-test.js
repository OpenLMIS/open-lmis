describe("Period", function () {
  beforeEach(module('openlmis.services'));

  describe("View Schedule Periods", function () {

    var scope, $httpBackend, ctrl, routeParams;
    var existingPeriod = {"id":10, "name":"name", "description":"description"};


    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $routeParams) {
      scope = $rootScope.$new();
      routeParams = $routeParams;
      routeParams.id = "123";
      $httpBackend = _$httpBackend_;
      $httpBackend.expectGET('/schedules/123/periods.json').respond(200, {"periods":[existingPeriod]});
      ctrl = $controller(SchedulePeriodController, {$scope:scope, $routeParams:routeParams});
    }));

    it('should show all the periods for given schedule', function() {
      $httpBackend.flush();
      expect(scope.periodList).toEqual([existingPeriod]);
    });
  });
});