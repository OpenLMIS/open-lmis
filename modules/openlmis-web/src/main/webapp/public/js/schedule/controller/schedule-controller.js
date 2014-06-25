/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function ScheduleController($scope, Schedule, $location, messageService) {
  $scope.schedulesBackupMap = [];
  $scope.newSchedule = {};
  $scope.schedules = {};
  $scope.editSchedule = {};
  $scope.creationError = '';

  $scope.getBackupSchedule = function (schedule) {
    return {
      code: schedule.code,
      name: schedule.name,
      description: schedule.description
    };
  };

  Schedule.get({}, function (data) {
    $scope.initialSchedules = angular.copy(data.schedules, $scope.initialSchedules);
    $scope.schedules = data.schedules;
    for (var scheduleIndex in data.schedules) {
      var schedule = data.schedules[scheduleIndex];
      $scope.schedulesBackupMap[schedule.id] = $scope.getBackupSchedule(schedule);
    }
  }, function (data) {
    $location.path($scope.$parent.sourceUrl);
  });

  $scope.createSchedule = function () {
    $scope.error = "";
    if ($scope.createScheduleForm.$invalid) {
      $scope.showErrorForCreate = true;
      return;
    }
    $scope.showErrorForCreate = false;
    Schedule.save({}, $scope.newSchedule, function (data) {
      $scope.schedules.unshift(data.schedule);
      $scope.completeAddNewSchedule(data.schedule);
      $scope.message = data.success;
      setTimeout(function () {
        $scope.$apply(function () {
          $scope.message = "";
        });
      }, 4000);
      $scope.newSchedule = {};
    }, function (data) {
      $scope.message = "";
      $scope.creationError = messageService.get(data.data.error);
    });
  };

  $scope.startAddNewSchedule = function () {
    $scope.$parent.newScheduleMode = true;
    $scope.$parent.formActive = "save-row-active";
  };

  $scope.completeAddNewSchedule = function (schedule) {
    $scope.schedulesBackupMap[schedule.id] = $scope.getBackupSchedule(schedule);
    $scope.$parent.newScheduleMode = false;
    $scope.showErrorForCreate = false;
  };

  $scope.cancelAddNewSchedule = function (schedule) {
    $scope.$parent.newScheduleMode = false;
    $scope.showErrorForCreate = false;
  };

  $scope.updateSchedule = function (schedule, form) {
    function updateUiData(sourceSchedule) {
      var schedulesLength = $scope.schedules.length;
      for (var i = 0; i < schedulesLength; i++) {
        if ($scope.schedules[i].id == sourceSchedule.id) {
          $scope.schedules[i].code = sourceSchedule.code;
          $scope.schedules[i].name = sourceSchedule.name;
          $scope.schedules[i].description = sourceSchedule.description;
          $scope.schedules[i].modifiedBy = sourceSchedule.modifiedBy;
          $scope.schedules[i].modifiedDate = sourceSchedule.modifiedDate;
        }
      }
    }

    $scope.error = "";
    if (form.$invalid) {
      $scope.showErrorForEdit = true;
      return;
    }

    $scope.schedulesBackupMap[schedule.id].error = '';
    $scope.showErrorForEdit = true;

    Schedule.update({id: schedule.id}, schedule, function (data) {
      var returnedSchedule = data.schedule;
      $scope.schedulesBackupMap[returnedSchedule.id] = $scope.getBackupSchedule(returnedSchedule);

      updateUiData(returnedSchedule);
      $scope.message = data.success;
      setTimeout(function () {
        $scope.$apply(function () {
          $scope.message = "";
        });
      }, 4000);
      $scope.error = "";
      $scope.newSchedule = {};
      $scope.editSchedule = {};

      $scope.schedulesBackupMap[returnedSchedule.id].editFormActive = 'updated-item';
      $scope.schedulesBackupMap[returnedSchedule.id].edit = false;
    }, function (data) {
      $scope.message = "";
      $scope.startScheduleEdit(schedule);
      $scope.schedulesBackupMap[schedule.id].error = messageService.get(data.data.error);
    });
  };

  $scope.scheduleLoaded = function () {
    return !($scope.schedules === undefined || $scope.schedules === null);
  };

  $scope.startScheduleEdit = function (scheduleUnderEdit) {
    $scope.schedulesBackupMap[scheduleUnderEdit.id].editFormActive = "save-row-active";
  };

  $scope.cancelScheduleEdit = function (scheduleUnderEdit) {
    var backupScheduleRow = $scope.schedulesBackupMap[scheduleUnderEdit.id];
    scheduleUnderEdit.code = backupScheduleRow.code;
    scheduleUnderEdit.name = backupScheduleRow.name;
    scheduleUnderEdit.description = backupScheduleRow.description;
    $scope.schedulesBackupMap[scheduleUnderEdit.id].error = '';
    $scope.schedulesBackupMap[scheduleUnderEdit.id].editFormActive = '';
    $scope.message = "";
  };

  $scope.navigateToPeriodFor = function (scheduleForPeriod) {
    $location.path('/manage-period/' + scheduleForPeriod.id);
  };
}