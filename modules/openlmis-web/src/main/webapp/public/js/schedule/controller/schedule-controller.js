function ScheduleController($scope, Schedules) {
  Schedules.get({}, function (data) {
    $scope.initialSchedules = angular.copy(data.schedules, $scope.initialSchedules);
    $scope.schedules = data.schedules;
    $scope.newSchedule = {};
  }, function (data) {
    $location.path($scope.$parent.sourceUrl);
  });

  $scope.createSchedule = function () {

    Schedules.save({}, $scope.newSchedule, function (data) {
      $scope.schedules.push(data.schedule);
      $scope.message = "Schedule Saved Successfully";
      $scope.error = "";
      $scope.newSchedule = {};
    }, function (data) {
      $scope.message = "";
      $scope.error = "Save failed";
    });
  }

  $scope.scheduleLoaded = function () {
    return !($scope.schedules == undefined || $scope.schedules == null);
  }
}