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

  $scope.clearMotorbikeProblems = function () {
    var visit = $scope.distribution.facilityDistributions[$scope.selectedFacility].facilityVisit;

    if (!visit.motorbikeProblems) {
      visit.motorbikeProblems = { notRecorded: false };
    }

    visit.motorbikeProblems.notRecorded = false;
    $.each(['lackOfFundingForFuel','repairsSchedulingProblem','lackOfFundingForRepairs','missingParts','other'], function (i, elem) {
      visit.motorbikeProblems[elem] = setApplicableField(visit.motorbikeProblems[elem]);
    });
  };

  $scope.setApplicableVisitInfo = function () {
    var visit = $scope.distribution.facilityDistributions[$scope.selectedFacility].facilityVisit;

    if (isUndefined(visit.visited)) {
        return;
    }

    $scope.clearMotorbikeProblems();
    if (visit.visited.value) {
      visit.reasonForNotVisiting = setApplicableField(visit.reasonForNotVisiting);
      visit.otherReasonDescription = setApplicableField(visit.otherReasonDescription);
      return;
    }

    visit.observations = setApplicableField(visit.observations);
    if (!visit.confirmedBy) {
      visit.confirmedBy = {};
    }
    visit.confirmedBy.name = setApplicableField(visit.confirmedBy.name);
    visit.confirmedBy.title = setApplicableField(visit.confirmedBy.title);
    if (!visit.verifiedBy) {
      visit.confirmedBy = {};
    }
    visit.verifiedBy.name = setApplicableField(visit.verifiedBy.name);
    visit.verifiedBy.title = setApplicableField(visit.verifiedBy.title);
    visit.vehicleId = setApplicableField(visit.vehicleId);
    visit.visitDate = setApplicableField(visit.visitDate);
    visit.numberOfOutreachVisitsPlanned = setApplicableField(visit.numberOfOutreachVisitsPlanned);
    visit.numberOfOutreachVisitsCompleted = setApplicableField(visit.numberOfOutreachVisitsCompleted);
    visit.numberOfMotorbikesAtHU = setApplicableField(visit.numberOfMotorbikesAtHU);
    visit.numberOfFunctioningMotorbikes = setApplicableField(visit.numberOfFunctioningMotorbikes);
    visit.numberOfMotorizedVehiclesWithProblems = setApplicableField(visit.numberOfMotorizedVehiclesWithProblems);
    visit.numberOfDaysWithLimitedTransport = setApplicableField(visit.numberOfDaysWithLimitedTransport);
  };

  function setApplicableField(field) {
    if (isUndefined(field)) {
        return {type: 'reading'};
    }

    if (isUndefined(field.original)) {
        return {type: 'reading'};
    }

    return { original: field.original, type: 'reading' };
  }
}
