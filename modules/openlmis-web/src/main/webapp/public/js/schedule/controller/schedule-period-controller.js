function SchedulePeriodController($scope, $routeParams, Periods, Schedule) {
  $scope.newPeriod = {};
  Schedule.get({id:$routeParams.id}, function (data) {
    $scope.schedule = data.schedule;
  }, function(data){
    $scope.error = "Error Identifying Schedule";
  });

  Periods.get({scheduleId:$routeParams.id}, function (data) {
    $scope.periodList = data.periods;
  }, {});

  $scope.calculateDays = function (startTime, endTime) {
    var startDate = new Date(startTime);
    var endDate = new Date(endTime);
    endDate.setHours(0);
    startDate.setHours(0);
    var days = Math.ceil(((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)));
    if(days >= 0)
      return (days+1);
    else return null;
  }

  $scope.createPeriod = function () {
    function validatePeriod(){
      if($scope.calculateDays($scope.newPeriod.startDate, $scope.newPeriod.endDate)<0){
        $scope.error = "End Date must be greater than Start Date";
        $scope.message = "";
        return false;
      }
      return true;
    }

    $scope.showErrorForCreate = true;
    if ($scope.createPeriodForm.$invalid) return;
    if(!validatePeriod()) return;
    $scope.showErrorForCreate = false;

    Periods.save({scheduleId:$routeParams.id}, $scope.newPeriod, function (data) {
      $scope.periodList.unshift(data.period);
      $scope.message = data.success;
      $scope.error = "";
      $scope.newPeriod = {};
    }, function (data) {
      $scope.message = "";
      $scope.error = data.data.error;
    });
  }
}
