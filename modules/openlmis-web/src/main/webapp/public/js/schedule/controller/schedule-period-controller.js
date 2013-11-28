/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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
  };

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
    if ($scope.calculateDays($scope.newPeriod.startDate, $scope.newPeriod.endDate) !== null) {
      $scope.newPeriod.numberOfMonths = Math.round($scope.calculateDays($scope.newPeriod.startDate, $scope.newPeriod.endDate) / 30);
      if ($scope.newPeriod.numberOfMonths === 0) {
        $scope.newPeriod.numberOfMonths += 1;
      }
      return $scope.newPeriod.numberOfMonths;
    }
    else return null;
  };

  $scope.createPeriod = function () {
    function validatePeriod() {
      if ($scope.calculateDays($scope.newPeriod.startDate, $scope.newPeriod.endDate) === null) {
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
      if ($scope.periodList.length !== 0)
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
    setTimeout(function() {
      angular.element("input[ui-date]").blur();
    });
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
    if ($scope.periodList.length !== 0)
      resetNewPeriod(new Date($scope.periodList[0].endDate).getTime());
    else
      $scope.newPeriod = {};
  };

  $scope.showDeleteButton = function (index) {
    return (index === 0 && !inValidateStartDate($scope.periodList[index]));
  };
}
