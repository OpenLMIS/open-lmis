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

    it('should show all the periods for given schedule', function () {
      $httpBackend.flush();
      expect(scope.periodList).toEqual([existingPeriod]);
    });

    it('should calculate no. of days', function () {
      $httpBackend.flush();
      expect(scope.calculateDays(new Date(2011,3,1,0,0).getTime(), new Date(2011,4,1,0,0).getTime())).toEqual(30);
      expect(scope.calculateDays(new Date(2011,3,1,0,0).getTime(), new Date(2011,3,1,0,0).getTime())).toEqual(0);
      expect(scope.calculateDays(new Date(2011,3,1,23,59,59).getTime(), new Date(2011,4,1,0,0).getTime())).toEqual(30);
      expect(scope.calculateDays(new Date(2011,3,1,23,59,59).getTime(), new Date(2011,3,2,0,0).getTime())).toEqual(1);
    });

  });
});