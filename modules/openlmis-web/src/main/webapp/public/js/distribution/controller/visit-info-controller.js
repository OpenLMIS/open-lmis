/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function VisitInfoController($scope, distributionService, $routeParams) {
  $scope.distribution = distributionService.distribution;
  $scope.distributionReview = distributionService.distributionReview;
  $scope.selectedFacility = $routeParams.facility;

  $scope.convertToDateObject = function (dateText) {
    var dateParts = dateText.split('/');

    return new Date(dateParts[2], dateParts[1] - 1, dateParts[0]);
  };

  $scope.startDate = $scope.convertToDateObject($scope.distribution.period.stringStartDate);

  $scope.reasons = {
    badWeather: "ROAD_IMPASSABLE",
    noTransport: "TRANSPORT_UNAVAILABLE",
    facilityClosed: "HEALTH_CENTER_CLOSED",
    unavailableFuelFunds: "FUEL_FUNDS_UNAVAILABLE",
    unavailablePerDiemFunds: "PERDIEM_FUNDS_UNAVAILABLE",
    refrigeratorsNotWorking: "REFRIGERATORS_NOT_WORKING",
    noRefrigerators: "NO_REFRIGERATORS",
    notPartOfProgram: "HEALTH_CENTER_NOT_IN_DLS",
    other: "OTHER"
  };

  $scope.beforeDatepickerShowDay = function (date) {
    if (!$("#visitDate").val().length && $scope.startDate.getTime() == date.getTime()) {
      return [true, "ui-state-active"];
    }

    return [true, ""];
  };
}
