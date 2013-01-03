function SchedulePeriodController($scope, $routeParams, Periods) {

  Periods.get({scheduleId:$routeParams.id}, function (data) {
    $scope.periodList = data.periods;
  }, {});

  $scope.calculateDays = function (startTime, endTime) {
    var startDate = new Date(startTime);
    var endDate = new Date(endTime);
    endDate.setHours(0);
    startDate.setHours(0);

    return Math.ceil(((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)));
  }

}
