/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function SchedulePeriodController($scope, $routeParams, Periods, schedule, Period) {

  ONE_DAY = 1000 * 60 * 60 * 24;
  $scope.schedule = schedule;

  function refreshPeriodList() {
    Periods.get({scheduleId: $routeParams.id}, function (data) {
      angular.extend($scope, {periodList: data.periods, nextStartDate: data.nextStartDate});
      prepareNewPeriod();
    }, function () {
    });
  }

  function prepareNewPeriod() {
    if ($scope.periodList.length === 0) {
      $scope.newPeriod = {};
      return;
    }
    $scope.newPeriod = {startDate: $scope.nextStartDate};
    $scope.refreshEndDateOffset($scope.newPeriod.startDate);
  }

  $scope.refreshEndDateOffset = function (startDate) {
    $scope.endDateOffset = Math.ceil((new Date(startDate.split('-')).getTime() + ONE_DAY - Date.now()) / ONE_DAY);
  };

  refreshPeriodList();

  function correctDateFormat(stringDate) {
    if (stringDate && angular.isString(stringDate) && stringDate.indexOf('/') !== -1) {
      stringDate = stringDate.split("/").reverse();
    }
    return stringDate;
  }

  $scope.calculateDays = function (startDate, endDate) {
    startDate = correctDateFormat(startDate);
    endDate = correctDateFormat(endDate);
    return Math.ceil((new Date(endDate).getTime() - new Date(startDate).getTime()) / ONE_DAY) + 1;
  };

  $scope.calculateMonths = function () {
    if (!($scope.newPeriod && $scope.newPeriod.startDate && $scope.newPeriod.endDate))
      return undefined;

    $scope.newPeriod.numberOfMonths = Math.round($scope.calculateDays($scope.newPeriod.startDate, $scope.newPeriod.endDate) / 30);
    $scope.newPeriod.numberOfMonths = Math.max($scope.newPeriod.numberOfMonths, 1);
    return $scope.newPeriod.numberOfMonths;
  };

  function errorCallBack(data) {
    $scope.message = '';
    $scope.error = data.data.error;
  }

  $scope.createPeriod = function () {
    $scope.showErrorForCreate = true;
    if ($scope.createPeriodForm.$invalid) {
      return;
    }
    Periods.save({scheduleId: $routeParams.id}, $scope.newPeriod, function (data) {
      $scope.showErrorForCreate = false;
      $scope.message = data.success;
      $scope.error = '';
      refreshPeriodList();
    }, errorCallBack);
  };

  $scope.blurDateFields = function () {
    setTimeout(function () {
      angular.element("input[ui-date]").blur();
    });
  };

  $scope.deletePeriod = function (periodId) {
    Period.remove({id: periodId}, function (data) {
      $scope.message = data.success;
      $scope.error = '';
      refreshPeriodList();
    }, errorCallBack);
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
  }
};
