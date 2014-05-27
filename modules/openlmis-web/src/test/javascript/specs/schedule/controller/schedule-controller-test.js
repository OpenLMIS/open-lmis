/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("Schedule", function () {
  beforeEach(module('openlmis'));

  describe("Schedule Controller", function () {

    var scope, $httpBackend, ctrl, routeParams, facility, location;
    var existingSchedule = {"id": 1, "name": "name", "code": "code", "description": "description"};
    var editScheduleForm = {$invalid: false};

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $location, $routeParams) {
      scope = $rootScope.$new();
      routeParams = $routeParams;
      $httpBackend = _$httpBackend_;
      location = $location;

      $httpBackend.expectGET('/schedules.json').respond(200, {"schedules": [existingSchedule]});
      ctrl = $controller(ScheduleController, {$scope: scope, $routeParams: routeParams});
    }));

    it('should fetch all schedules', function () {
      $httpBackend.flush();
      expect(scope.schedules).toEqual([existingSchedule]);
    });

    it('should create a new schedule', function () {
      scope.newSchedule = {"code": "newCode", "name": "newName", "description": "newDescription"};
      var newScheduleWithId = {"id": 2, "code": "newCode", "name": "newName", "description": "newDescription"};
      var expectedScheduleBackupMap = [];
      scope.schedulesBackupMap[0] = "";
      expectedScheduleBackupMap[0] = "";
      expectedScheduleBackupMap[1] = {code: existingSchedule.code, name: existingSchedule.name, description: existingSchedule.description};
      expectedScheduleBackupMap[2] = {"code": "newCode", "name": "newName", "description": "newDescription"};

      $httpBackend.expectPOST('/schedules.json').respond(200, {"schedule": newScheduleWithId, "success": "success message"});
      scope.createScheduleForm = {$invalid: false};
      scope.createSchedule();
      $httpBackend.flush();
      expect(scope.schedules.length).toEqual(2);


      expect(scope.schedulesBackupMap).toEqual(expectedScheduleBackupMap);

      expect(scope.schedules).toEqual([newScheduleWithId, existingSchedule]);
      expect(scope.message).toEqual("success message");
    });

    it('should show error on failure of creation of a new schedule', function () {
      scope.newSchedule = {"code": "newCode", "name": "newName", "description": "newDescription"};
      scope.createScheduleForm = {$invalid: false};
      $httpBackend.expectPOST('/schedules.json').respond(400, {"error": "errorMsg"});

      scope.createSchedule();
      $httpBackend.flush();
      expect(scope.message).toEqual("");
      expect(scope.creationError).toEqual("errorMsg");
    });

    xit('should update an existing schedule', function () {
      var updatedSchedule = {"id": 1, "code": "newCode", "name": "newName", "description": "newDescription", "modifiedBy": "", "modifiedDate": "12345"};
      var expectedScheduleBackupMap = [];
      expectedScheduleBackupMap[1] = {"code": "newCode", "name": "newName", "description": "newDescription"};
      $httpBackend.expectPUT('/schedules/1.json').respond(200, {"schedule": updatedSchedule, "success": "success message"});

      scope.updateSchedule(existingSchedule, editScheduleForm);
      $httpBackend.flush();

      expect(scope.schedules.length).toEqual(1);
      expect(scope.schedules).toEqual([
        {"id": 1, "code": "newCode", "name": "newName", "description": "newDescription", "modifiedBy": "", "modifiedDate": "12345"}
      ]);
      expect(scope.schedulesBackupMap).toEqual(expectedScheduleBackupMap);
      expect(scope.message).toEqual("success message");
    });

    it('should show failure error on updating an existing schedule', function () {
      var updatedSchedule = {"id": 1, "code": "newCode", "name": "newName", "description": "newDescription"};
      $httpBackend.expectPUT('/schedules/1.json').respond(400, {"error": "errorMsg"});
      scope.schedulesBackupMap[1] = updatedSchedule;
      var editScheduleForm = {$invalid: false};
      scope.updateSchedule(updatedSchedule, editScheduleForm);
      $httpBackend.flush();
      expect(scope.message).toEqual("");
      expect(scope.schedulesBackupMap[1].error).toEqual("errorMsg");
    });

    it('should set correct schedule in the scope and navigate to period', function () {
      var selectedSchedule = {"id": 1, "code": "newCode", "name": "newName", "description": "newDescription"};
      scope.navigateToPeriodFor(selectedSchedule);
      expect(location.path()).toEqual("/manage-period/1");
    });

    it('should do nothing on canceling schedule edit', function () {
      var scheduleUnderEdit = {"id": 1, "code": "editedCode", "name": "editedName", "description": "editedDescription"};
      scope.schedulesBackupMap = [
        {},
        {"id": 1, "name": "name", "code": "code", "description": "description"}
      ];

      scope.cancelScheduleEdit(scheduleUnderEdit);

      expect(scheduleUnderEdit.code).toEqual("code");
      expect(scheduleUnderEdit.name).toEqual("name");
      expect(scheduleUnderEdit.description).toEqual("description");
      expect(scope.schedulesBackupMap[1].error).toEqual("");
      expect(scope.schedulesBackupMap[1].editFormActive).toEqual('');
      expect(scope.message).toEqual("");
    });
  });
});