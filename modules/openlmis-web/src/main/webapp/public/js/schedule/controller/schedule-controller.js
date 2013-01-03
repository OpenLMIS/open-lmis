function ScheduleController($scope, Schedules, Schedule) {
  $scope.newSchedule = {};
  $scope.schedules = {};
  $scope.editSchedule = {};
  Schedules.get({}, function (data) {
    $scope.initialSchedules = angular.copy(data.schedules, $scope.initialSchedules);
    $scope.schedules = data.schedules;

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
    Schedules.save({}, $scope.newSchedule, function (data) {
      $scope.schedules.unshift(data.schedule);
      $scope.message = "Schedule Saved Successfully";
      $scope.newSchedule = {};
    }, function (data) {
      $scope.message = "";
      $scope.error = data.data.error;
    });
  };

  $scope.startAddNewSchedule = function() {
    $scope.$parent.newScheduleMode = true;
    angular.element("#createScheduleForm").find("#code").focus();
    console.log("start")
  };

  $scope.completeAddNewSchedule = function() {
    $scope.$parent.newScheduleMode = false;
    $scope.showErrorForCreate = false;
    console.log("end")
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
    $scope.showErrorForEdit = true;
    Schedule.update({id:schedule.id}, schedule, function (data) {
      updateUiData(data.schedule);
      $scope.completeAddNewSchedule();
      $scope.message = "Schedule Saved Successfully";
      $scope.error = "";
      $scope.newSchedule = {};
      $scope.editSchedule = {};
    }, function (data) {
      $scope.message = "";
      $scope.error = data.data.error;
    });
  }

  $scope.scheduleLoaded = function () {
    return !($scope.schedules == undefined || $scope.schedules == null);
  }

  $scope.startScheduleEdit = function (scheduleUnderEdit) {
    $scope.editSchedule.code = scheduleUnderEdit.code;
    $scope.editSchedule.name = scheduleUnderEdit.code;
    $scope.editSchedule.description = scheduleUnderEdit.code;
  }

  $scope.endScheduleEdit = function (scheduleUnderEdit) {
    scheduleUnderEdit.code = $scope.editSchedule.code;
    scheduleUnderEdit.name = $scope.editSchedule.code;
    scheduleUnderEdit.description = $scope.editSchedule.code;
  }
}