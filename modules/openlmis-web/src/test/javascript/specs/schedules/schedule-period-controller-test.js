describe("Period", function () {
  beforeEach(module('openlmis.services'));

  describe("View Schedule Periods", function () {
    var scheduleId = 123;
    var scope, $httpBackend, ctrl, routeParams;
    var existingPeriod = {"id":10, "name":"name", "description":"description"};
    var schedule = {"id":scheduleId, "name":"name", "description":"description"};

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $routeParams) {
      scope = $rootScope.$new();
      routeParams = $routeParams;
      routeParams.id = scheduleId;
      $httpBackend = _$httpBackend_;

      $httpBackend.expectGET('/schedules/123.json').respond(200, {"schedule": schedule});
      $httpBackend.expectGET('/schedules/123/periods.json').respond(200, {"periods":[existingPeriod]});
      ctrl = $controller(SchedulePeriodController, {$scope:scope, $routeParams:routeParams});
    }));

    it('should show all the periods for given schedule and the schedule for these periods', function () {
      $httpBackend.flush();
      expect(scope.schedule).toEqual(schedule);
      expect(scope.periodList).toEqual([existingPeriod]);
    });

    it('should calculate no. of days', function () {
      $httpBackend.flush();
      expect(scope.calculateDays(new Date(2011,3,1,0,0).getTime(), new Date(2011,4,1,0,0).getTime())).toEqual(30);
      expect(scope.calculateDays(new Date(2011,3,1,0,0).getTime(), new Date(2011,3,1,0,0).getTime())).toEqual(0);
      expect(scope.calculateDays(new Date(2011,3,1,23,59,59).getTime(), new Date(2011,4,1,0,0).getTime())).toEqual(30);
      expect(scope.calculateDays(new Date(2011,3,1,23,59,59).getTime(), new Date(2011,3,2,0,0).getTime())).toEqual(1);
    });

    it('should calculate no. of months', function () {
      scope.newPeriod = {"name":"newName", "startDate":new Date(2011,3,1,0,0).getTime(), "endDate":new Date(2011,4,1,0,0).getTime(), "description":"newDescription"};
      $httpBackend.flush();
      expect(scope.calculateMonths()).toEqual(1);
    });

    it('should create a new period', function() {
      scope.newPeriod = {"name":"newName", "startDate":new Date(2011,3,1,0,0).getTime(), "endDate":new Date(2011,4,1,0,0).getTime(), "description":"newDescription"};
      var newPeriodWithId = {"id":11, "name":"newName", "startDate":new Date(2011,3,1,0,0).getTime(), "endDate":new Date(2011,4,1,0,0).getTime(), "description":"newDescription"};
      $httpBackend.expectPOST('/schedules/123/periods.json').respond(200, {"period":newPeriodWithId, "success":"success message"});
      scope.createPeriodForm = {$invalid : false};
      scope.createPeriod();
      $httpBackend.flush();
      expect(scope.periodList.length).toEqual(2);
      expect(scope.periodList).toEqual([newPeriodWithId, existingPeriod]);
      expect(scope.message).toEqual("success message");
    });

    it('should not create a new period if Start Date is greater than End Date', function() {
      scope.newPeriod = {"name":"newName", "startDate":new Date(2011,3,1,0,0).getTime(), "endDate":new Date(2011,2,1,0,0).getTime(), "description":"newDescription"};
      scope.createPeriodForm = {$invalid : false};
      scope.createPeriod();
      $httpBackend.flush();
      expect(scope.periodList.length).toEqual(1);
      expect(scope.periodList).toEqual([existingPeriod]);
      expect(scope.error).toEqual("End Date must be greater than Start Date");
      expect(scope.message).toEqual("");
    });
  });
});