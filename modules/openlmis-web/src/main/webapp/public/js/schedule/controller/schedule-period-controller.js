/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function SchedulePeriodController($scope, $routeParams, Periods, Schedule, Period, $location, messageService) {
  $scope.newPeriod = {};
  $scope.oneDay = 1000 * 60 * 60 * 24;
  $scope.lastPeriodId = "";
  var displayTime = 8000;

  Schedule.get({id: $routeParams.id}, function (data) {
    $scope.error = "";
    $scope.schedule = data.schedule;
  }, function () {
    $scope.$parent.errorInValidSchedule = messageService.get("error.identify.schedule");
    $location.path("/list");
  });

  Periods.get({scheduleId: $routeParams.id}, function (data) {
    $scope.periodList = data.periods;
    prepareNewPeriod();
  }, {});

  $scope.updateEndDate = function () {
    $scope.newPeriod.endDate = new Date($scope.newPeriod.endDate.getTime() + $scope.oneDay - 1000);
  }

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
        $scope.error = messageService.get("error.endDate");
        $scope.message = "";
        return false;
      }
      return true;
    }

    $scope.showErrorForCreate = true;
    if ($scope.createPeriodForm.$invalid) return;
    if (!validatePeriod()) return;
    $scope.showErrorForCreate = false;

    Periods.save({scheduleId: $routeParams.id}, $scope.newPeriod, function (data) {
      $scope.periodList.unshift($scope.newPeriod);
      $scope.message = data.success;
      $scope.newPeriod.id = data.id;
      setTimeout(function () {
        $scope.$apply(function () {
          $scope.message = "";
        });
      }, displayTime);
      $scope.error = "";
      if ($scope.periodList.length != 0)
        resetNewPeriod(new Date($scope.periodList[0].endDate).getTime());
      else
        $scope.newPeriod = {};
    }, function (data) {
      $scope.message = "";
      $scope.error = messageService.get(data.data.error);
    });
  };

  var resetNewPeriod = function (endDate) {
    $scope.newPeriod = {};
    $scope.newPeriod.startDate = new Date(endDate + 1000);
    $scope.refreshEndDateOffset($scope.newPeriod.startDate.getTime());
  };

  $scope.refreshEndDateOffset = function (startDateTime) {
    $scope.endDateOffset = Math.ceil((startDateTime + $scope.oneDay - Date.now()) / $scope.oneDay);
    $scope.newPeriod.endDate = undefined;
  };

  $scope.blurDateFields = function () {
    angular.element("input[ui-date]").blur();
  };

  $scope.deletePeriod = function (periodId) {
    $($scope.periodList).each(function (index, periodObject) {
      if (periodObject.id == periodId) {
        if (!inValidateStartDate(periodObject)) {
          Period.remove({id: periodId}, function (data) {
            $scope.periodList.splice(index, 1);
            $scope.message = data.success;
            $scope.error = "";
            if ($scope.periodList.length > 0) {
              resetNewPeriod(new Date($scope.periodList[0].endDate).getTime());
            } else {
              $scope.newPeriod.startDate = undefined;
            }
          }, function (data) {
            $scope.message = "";
            $scope.error = messageService.get(data.data.error);
          });
        } else {
          $scope.message = "";
          $scope.error = messageService.get("error.period.start.date");
        }
      }
    });

    setTimeout(function () {
      $scope.$apply(function () {
        $scope.message = "";
        $scope.error = "";
      });
    }, displayTime);
  };

  var inValidateStartDate = function (periodToDelete) {
    return (periodToDelete.startDate - Date.now()) <= 0;
  };

  var prepareNewPeriod = function () {
    if ($scope.periodList.length != 0)
      resetNewPeriod(new Date($scope.periodList[0].endDate).getTime());
    else
      $scope.newPeriod = {};
  };

  $scope.showDeleteButton = function (index) {
    return (index == 0 && !inValidateStartDate($scope.periodList[index]));
  }
}
