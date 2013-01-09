function SchedulePeriodController($scope, $routeParams, Periods, Schedule, Period, $location) {
  $scope.newPeriod = {};
  $scope.oneDay = 1000 * 60 * 60 * 24;
  $scope.lastPeriodId = "";

  Schedule.get({id:$routeParams.id}, function (data) {
    $scope.error = "";
    $scope.schedule = data.schedule;
  }, function (data) {
    $scope.$parent.errorInValidSchedule = "Error Identifying Schedule";
    $location.path("/list");
  });

  Periods.get({scheduleId:$routeParams.id}, function (data) {
    $scope.periodList = data.periods;
    resetNewPeriod($scope.periodList[0].endDate);
  }, {});

  $scope.calculateDays = function (startTime, endTime) {
    var startDate = new Date(startTime);
    var endDate = new Date(endTime);
    endDate.setHours(0);
    startDate.setHours(0);
    var days = Math.ceil(((endDate.getTime() - startDate.getTime()) / $scope.oneDay));
    if (days > 0)
      return days;
    else return null;
  };

  $scope.calculateMonths = function () {
    if ($scope.calculateDays($scope.newPeriod.startDate, $scope.newPeriod.endDate) != null) {
      $scope.newPeriod.numberOfMonths = Math.round($scope.calculateDays($scope.newPeriod.startDate, $scope.newPeriod.endDate) / 30);
      if ($scope.newPeriod.numberOfMonths == 0) {
        $scope.newPeriod.numberOfMonths += 1;
      }
      return $scope.newPeriod.numberOfMonths;
    }
    else return null;
  };

  $scope.createPeriod = function () {
    function validatePeriod() {
      if ($scope.calculateDays($scope.newPeriod.startDate, $scope.newPeriod.endDate) == null) {
        $scope.error = "End Date must be greater than Start Date";
        $scope.message = "";
        return false;
      }
      return true;
    }

    $scope.showErrorForCreate = true;
    if ($scope.createPeriodForm.$invalid) return;
    if (!validatePeriod()) return;
    $scope.showErrorForCreate = false;

    Periods.save({scheduleId:$routeParams.id}, $scope.newPeriod, function (data) {
      $scope.periodList.unshift($scope.newPeriod);
      $scope.message = data.success;
      $scope.newPeriod.id = data.id;
      setTimeout(function () {
        $scope.$apply(function () {
          $scope.message = "";
        });
      }, 4000);
      $scope.error = "";
      resetNewPeriod(new Date($scope.periodList[0].endDate).getTime());
    }, function (data) {
      $scope.message = "";
      $scope.error = data.data.error;
    });
  };

  var resetNewPeriod = function (endDate) {
    $scope.newPeriod = {};
    $scope.newPeriod.startDate = endDate + $scope.oneDay;
    $scope.refreshEndDateOffset($scope.newPeriod.startDate);
  };

  $scope.refreshEndDateOffset = function (startDateTime) {
    $scope.endDateOffset = Math.ceil((startDateTime + $scope.oneDay - (new Date()).getTime()) / $scope.oneDay);
    $scope.newPeriod.endDate = undefined;
  };

  $scope.blurDateFields = function () {
    angular.element("input[ui-date]").blur();
  };

  $scope.deletePeriod = function (periodId) {
    $($scope.periodList).each(function (index, periodObject) {
      if (periodObject.id == periodId) {
        if (!inValidateStartDate(periodObject)) {
          Period.remove({id:periodId}, function (data) {
            $scope.periodList.splice(index, 1);
            $scope.message = data.success;
            $scope.error = "";
            if($scope.periodList.length > 0 ) {
              resetNewPeriod(new Date($scope.periodList[0].endDate).getTime());
            } else {
              $scope.newPeriod.startDate = undefined;
            }
          }, function (data) {
            $scope.message = "";
            $scope.error = data.data.error;
          });
        } else {
          $scope.message = "";
          $scope.error = "Period's Start Date is smaller than Current Date";
        }
      }
    });
    setTimeout(function () {
      $scope.$apply(function () {
        $scope.message = "";
        $scope.error = "";
      });
    }, 4000);
  }

  var inValidateStartDate = function (periodToDelete) {
    return (periodToDelete.startDate - Date.now()) <= 0;
  }


  $scope.showDeleteButton = function (index) {
    return (index == 0 && !inValidateStartDate($scope.periodList[index]));
  }
}
