/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function SchedulePeriodController($scope, $routeParams, Periods, schedule, periods, Period) {

  $scope.newPeriod = {};
  $scope.oneDay = 1000 * 60 * 60 * 24;
  $scope.lastPeriodId = "";
  $scope.schedule = schedule;

  $scope.periodList = periods;

  $scope.refreshEndDateOffset = function (startDateTime) {
    $scope.endDateOffset = Math.ceil((startDateTime + $scope.oneDay - Date.now()) / $scope.oneDay);
    $scope.newPeriod.endDate = undefined;
  };

  function resetNewPeriod(endDate) {
    $scope.newPeriod = {};
    $scope.newPeriod.startDate = new Date(endDate + 1000);
    $scope.refreshEndDateOffset($scope.newPeriod.startDate.getTime());
  }

  prepareNewPeriod();

  $scope.updateEndDate = function () {
    $scope.newPeriod.endDate = new Date($scope.newPeriod.endDate.getTime() + $scope.oneDay - 1000);
  };

  $scope.calculateDays = function (startTime, endTime) {
    var startDate = new Date(startTime);
    var endDate = new Date(endTime);
    var days = Math.ceil(((endDate.getTime() - startDate.getTime()) / $scope.oneDay));
    return days;
  };

  $scope.calculateMonths = function () {
    $scope.newPeriod.numberOfMonths = Math.round($scope.calculateDays($scope.newPeriod.startDate, $scope.newPeriod.endDate) / 30);
    $scope.newPeriod.numberOfMonths = Math.max($scope.newPeriod.numberOfMonths, 1);
    return $scope.newPeriod.numberOfMonths;
  };

  function prepareNewPeriod() {
    if ($scope.periodList.length !== 0)
      resetNewPeriod(new Date($scope.periodList[0].endDate).getTime());
    else
      $scope.newPeriod = {};
  }

  function errorCallBack(data) {
    $scope.message = '';
    $scope.error = data.data.error;
  }

  $scope.createPeriod = function () {
    $scope.showErrorForCreate = true;
    if ($scope.createPeriodForm.$invalid) {
      return;
    }

    $scope.showErrorForCreate = false;

    Periods.save({scheduleId: $routeParams.id}, $scope.newPeriod, function (data) {
      $scope.newPeriod.stringStartDate = $scope.newPeriod.startDate;
      $scope.newPeriod.stringEndDate = $scope.newPeriod.endDate;

      $scope.periodList.unshift($scope.newPeriod);
      $scope.message = data.success;
      $scope.newPeriod.id = data.id;
      $scope.error = '';
      prepareNewPeriod();
    }, errorCallBack);
  };

  $scope.blurDateFields = function () {
    setTimeout(function () {
      angular.element("input[ui-date]").blur();
    });
  };

  $scope.deletePeriod = function (periodId) {
    $($scope.periodList).each(function (index, periodObject) {
      if (periodObject.id == periodId) {
        if (isStartDateValid(periodObject)) {
          Period.remove({id: periodId}, function (data) {
            $scope.periodList.splice(index, 1);
            $scope.message = data.success;
            $scope.error = '';

            prepareNewPeriod();
          }, errorCallBack);
        } else {
          $scope.message = '';
          $scope.error = 'error.period.start.date';
        }
      }
    });
  };

  var isStartDateValid = function (periodToDelete) {
    return (periodToDelete.startDate - Date.now()) > 0;
  };

  $scope.showDeleteButton = function (index) {
    return (index === 0 && isStartDateValid($scope.periodList[index]));
  };
}

SchedulePeriodController.resolve = {
  schedule: function ($q, $timeout, $route, $location, Schedule) {
    var deferred = $q.defer();
    $timeout(function () {
      Schedule.get({id: $route.current.params.id}, function (data) {
        deferred.resolve(data.schedule);
      }, function () {
        $location.path("/list");
      });
    });

    return deferred.promise;
  },

  periods: function ($q, $timeout, $route, $location, Periods) {
    var deferred = $q.defer();
    $timeout(function () {
      Periods.get({scheduleId: $route.current.params.id}, function (data) {
        deferred.resolve(data.periods);
      }, function () {
        $location.path("/list");
      });
    });

    return deferred.promise;
  }
};
