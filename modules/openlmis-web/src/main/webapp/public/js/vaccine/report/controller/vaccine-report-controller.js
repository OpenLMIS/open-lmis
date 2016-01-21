/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
function VaccineReportController($scope, programs, VaccineReportFacilities, VaccineReportPeriods, VaccineReportInitiate, messageService, $location) {

  $scope.programs = programs;

  $scope.onProgramChanged = function () {
    VaccineReportFacilities.get({programId: $scope.filter.program}, function (data) {
      $scope.facilities = data.facilities;
    });
  };

  $scope.onFacilityChanged = function () {
    if (isUndefined($scope.filter.facility)) {
      return;
    }
    VaccineReportPeriods.get({facilityId: $scope.filter.facility, programId: $scope.filter.program}, function (data) {
      $scope.periodGridData = data.periods;
      if ($scope.periodGridData.length > 0) {
        $scope.periodGridData[0].showButton = true;
      }
    });
  };

  $scope.initiate = function (period) {
    if (!angular.isUndefined(period.id) && (period.id !== null)) {
      // redirect already
      $location.path('/create/' + period.id);
    } else {
      // initiate
      VaccineReportInitiate.get({
        periodId: period.periodId,
        facilityId: period.facilityId,
        programId: period.programId
      }, function (data) {
        $location.path('/create/' + data.report.id);
      });
    }
  };

  function getActionButton(showButton) {
    return '<input type="button" ng-click="initiate(row.entity)" openlmis-message="button.proceed" class="btn btn-primary btn-small grid-btn" ng-show="' + showButton + '"/>';
  }

  $scope.periodGridOptions = {
    data: 'periodGridData',
    canSelectRows: false,
    displayFooter: false,
    displaySelectionCheckbox: false,
    enableColumnResize: true,
    enableColumnReordering: true,
    enableSorting: false,
    showColumnMenu: false,
    showFilter: false,
    columnDefs: [
      {field: 'periodName', displayName: messageService.get("label.periods")},
      {field: 'status', displayName: messageService.get("label.ivd.status")},
      {field: '', displayName: '', cellTemplate: getActionButton('row.entity.showButton')}
    ]
  };

  if (programs.length === 1) {
    $scope.filter = {program: programs[0].id};
    $scope.onProgramChanged();
  }
}

VaccineReportController.resolve = {
  programs: function ($q, $timeout, VaccineSupervisedIvdPrograms) {
    var deferred = $q.defer();

    $timeout(function () {
      VaccineSupervisedIvdPrograms.get({}, function (data) {
        deferred.resolve(data.programs);
      });
    }, 100);

    return deferred.promise;
  }
};
