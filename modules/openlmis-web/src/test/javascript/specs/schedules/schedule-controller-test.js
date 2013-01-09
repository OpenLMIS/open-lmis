describe("Schedule", function () {
  beforeEach(module('openlmis.services'));

  describe("Schedule Controller", function () {

    var scope, $httpBackend, ctrl, routeParams, facility, location;
    var existingSchedule = {"id":1, "name":"name", "code":"code", "description":"description"};
    var editScheduleForm = {$invalid:false};

    beforeEach(
        inject(function ($rootScope, _$httpBackend_, $controller, $location, $routeParams) {
          scope = $rootScope.$new();
          routeParams = $routeParams;
          $httpBackend = _$httpBackend_;
          location = $location;

          $httpBackend.expectGET('/schedules.json').respond(200, {"schedules":[existingSchedule]});
          ctrl = $controller(ScheduleController, {$scope:scope, $routeParams:routeParams});
        })
    );

    it('should fetch all schedules', function () {
      $httpBackend.flush();
      expect(scope.schedules).toEqual([existingSchedule]);
    });

    xit('should create a new schedule', function () {
      scope.newSchedule = {"code":"newCode", "name":"newName", "description":"newDescription"};
      var newScheduleWithId = {"id":2, "code":"newCode", "name":"newName", "description":"newDescription"};
      var expectedScheduleBackupMap = [];
      scope.schedulesBackupMap[0]="";
      expectedScheduleBackupMap[0] = "";
      expectedScheduleBackupMap[1] = {code:existingSchedule.code, name:existingSchedule.name, description:existingSchedule.description};
      expectedScheduleBackupMap[2] = {"code":"newCode", "name":"newName", "description":"newDescription"};

      $httpBackend.expectPOST('/schedules.json').respond(200, {"schedule":newScheduleWithId, "success":"success message"});
      scope.createScheduleForm = {$invalid:false};
      scope.createSchedule();
      $httpBackend.flush();
      expect(scope.schedules.length).toEqual(2);


      expect(scope.schedulesBackupMap).toEqual(expectedScheduleBackupMap);

      expect(scope.schedules).toEqual([newScheduleWithId, existingSchedule]);
      expect(scope.message).toEqual("success message");
    });

    it('should show error on failure of creation of a new schedule', function () {
      scope.newSchedule = {"code":"newCode", "name":"newName", "description":"newDescription"};
      scope.createScheduleForm = {$invalid:false};
      $httpBackend.expectPOST('/schedules.json').respond(400, {"error":"errorMsg"});

      scope.createSchedule();
      $httpBackend.flush();
      expect(scope.message).toEqual("");
      expect(scope.creationError).toEqual("errorMsg");
    });

    xit('should update an existing schedule', function () {
      var updatedSchedule = {"id":1, "code":"newCode", "name":"newName", "description":"newDescription", "modifiedBy":"", "modifiedDate":"12345"};
      var expectedScheduleBackupMap = [];
      expectedScheduleBackupMap[1] = {"code":"newCode", "name":"newName", "description":"newDescription"};
      $httpBackend.expectPUT('/schedules/1.json').respond(200, {"schedule":updatedSchedule, "success":"success message"});

      scope.updateSchedule(existingSchedule, editScheduleForm);
      $httpBackend.flush();

      expect(scope.schedules.length).toEqual(1);
      expect(scope.schedules).toEqual([
        {"id":1, "code":"newCode", "name":"newName", "description":"newDescription", "modifiedBy":"", "modifiedDate":"12345"}
      ]);
      expect(scope.schedulesBackupMap).toEqual(expectedScheduleBackupMap);
      expect(scope.message).toEqual("success message");
    });

    it('should show failure error on updating an existing schedule', function () {
      var updatedSchedule = {"id":1, "code":"newCode", "name":"newName", "description":"newDescription"};
      $httpBackend.expectPUT('/schedules/1.json').respond(400, {"error":"errorMsg"});

      var editScheduleForm = {$invalid:false};
      scope.updateSchedule(updatedSchedule, editScheduleForm);
      $httpBackend.flush();
      expect(scope.message).toEqual("");
      expect(scope.error).toEqual("errorMsg");
    });

    it('should set correct schedule in the scope and navigate to period', function () {
      var selectedSchedule = {"id":1, "code":"newCode", "name":"newName", "description":"newDescription"};
      scope.navigateToPeriodFor(selectedSchedule);
      expect(location.path()).toEqual("/manage-period/1");
    });

  });
});