function SchedulePeriodController($scope, $routeParams, Periods) {

  Periods.get({scheduleId:$routeParams.id}, function (data) {
    $scope.periodList = data.periods;
  },{});

}
