/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
    for(var scheduleIndex in data.schedules){
      var schedule = data.schedules[scheduleIndex];
      $scope.schedulesBackupMap[schedule.id] =  $scope.getBackupSchedule(schedule);
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
      setTimeout(function() {
        $scope.$apply(function() {
          $scope.message = "";
        });
      }, 4000);
      $scope.newSchedule = {};
    }, function (data) {
      $scope.message = "";
      $scope.creationError = messageService.get(data.data.error);
    });
  };

  $scope.startAddNewSchedule = function() {
    $scope.$parent.newScheduleMode = true;
    $scope.$parent.formActive = "schedule-form-active";
  };

  $scope.completeAddNewSchedule = function(schedule) {
    $scope.schedulesBackupMap[schedule.id] = $scope.getBackupSchedule(schedule);
    $scope.$parent.newScheduleMode = false;
    $scope.showErrorForCreate = false;
  };

  $scope.cancelAddNewSchedule = function(schedule) {
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

    Schedule.update({id:schedule.id}, schedule, function (data) {
      var returnedSchedule = data.schedule;
      $scope.schedulesBackupMap[returnedSchedule.id] = $scope.getBackupSchedule(returnedSchedule);

      updateUiData(returnedSchedule);
      $scope.message = data.success;
      setTimeout(function() {
        $scope.$apply(function() {
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
    return !($scope.schedules == undefined || $scope.schedules == null);
  };

  $scope.startScheduleEdit = function (scheduleUnderEdit) {
    $scope.schedulesBackupMap[scheduleUnderEdit.id].editFormActive = "schedule-form-active";
  };

  $scope.cancelScheduleEdit = function (scheduleUnderEdit) {
    var backupScheduleRow = $scope.schedulesBackupMap[scheduleUnderEdit.id];
    scheduleUnderEdit.code = backupScheduleRow.code;
    scheduleUnderEdit.name = backupScheduleRow.name;
    scheduleUnderEdit.description = backupScheduleRow.description;
    $scope.schedulesBackupMap[scheduleUnderEdit.id].error = '';
    $scope.schedulesBackupMap[scheduleUnderEdit.id].editFormActive = '';
  };

  $scope.navigateToPeriodFor = function (scheduleForPeriod) {
    $location.path('/manage-period/' + scheduleForPeriod.id);
  };
}