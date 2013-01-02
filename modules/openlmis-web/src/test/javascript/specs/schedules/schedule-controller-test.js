describe("Schedule", function () {
  beforeEach(module('openlmis.services'));

  describe("Schedule Controller", function () {

    var scope, $httpBackend, ctrl, routeParams, facility;
    var existingSchedule = {"id":1, "name":"name", "code":"code", "description":"description"};

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $routeParams) {
      scope = $rootScope.$new();
      routeParams = $routeParams;
      $httpBackend = _$httpBackend_;

      $httpBackend.expectGET('/schedules.json').respond(200, {"schedules":[existingSchedule]});
      ctrl = $controller(ScheduleController, {$scope:scope, $routeParams:routeParams});
    }));

    it('should fetch all schedules', function () {
      $httpBackend.flush();
      expect(scope.schedules).toEqual([existingSchedule]);
    });

    it('should create a new schedule', function() {
      var newSchedule = {"name":"newName", "code":"newCode", "description":"newDescription"};
      var newScheduleWithId = {"id":2, "name":"newName", "code":"newCode", "description":"newDescription"};
      $httpBackend.expectPOST('/schedules.json').respond(200, {"schedule":newScheduleWithId});

      scope.createSchedule(newSchedule);
      $httpBackend.flush();
      expect(scope.schedules.length).toEqual(2);
      expect(scope.schedules).toEqual([existingSchedule,newScheduleWithId]);
      expect(scope.message).toEqual("Schedule Saved Successfully");
    })
  });
});