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
      scope.createScheduleForm = {$invalid : false};
      scope.createSchedule(newSchedule);
      $httpBackend.flush();
      expect(scope.schedules.length).toEqual(2);
      expect(scope.schedules).toEqual([newScheduleWithId, existingSchedule]);
      expect(scope.message).toEqual("Schedule Saved Successfully");
    });

    it('should show error on failure of creation of a new schedule', function() {
      var schedule = {"name":"newName", "code":"newCode", "description":"newDescription"};
      $httpBackend.expectPOST('/schedules.json').respond(400, {"error":"errorMsg"});
      scope.createScheduleForm = {$invalid : false};

      scope.createSchedule(schedule);
      $httpBackend.flush();

      expect(scope.message).toEqual("");
      expect(scope.error).toEqual("errorMsg");
    });

    it('should update an existing schedule', function() {
      var updatedSchedule = {"id":1, "name":"newName", "code":"newCode", "description":"newDescription", "modifiedBy":"", "modifiedDate":"12345"};
      $httpBackend.expectPUT('/schedules/1.json').respond(200,{"schedule":updatedSchedule});

      scope.editScheduleForm = {$invalid : false};
      scope.updateSchedule(existingSchedule);
      $httpBackend.flush();

      expect(scope.schedules.length).toEqual(1);
      expect(scope.schedules).toEqual([{"id":1, "name":"newName", "code":"newCode", "description":"newDescription", "modifiedBy":"", "modifiedDate":"12345"}]);
      expect(scope.message).toEqual("Schedule Saved Successfully");
    });

    it('should show failure error on updating an existing schedule', function() {
      var updatedSchedule = {"id":1, "name":"newName", "code":"newCode", "description":"newDescription"};
      $httpBackend.expectPUT('/schedules/1.json').respond(400, {"error":"errorMsg"});

      scope.editScheduleForm = {$invalid : false};
      scope.updateSchedule(updatedSchedule);
      $httpBackend.flush();
      expect(scope.message).toEqual("");
      expect(scope.error).toEqual("errorMsg");
    });
  });
});