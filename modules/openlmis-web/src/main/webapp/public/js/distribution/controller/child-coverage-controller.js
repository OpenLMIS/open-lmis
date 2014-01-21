/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function ChildCoverageController($scope, $routeParams, distributionService) {

  $scope.distribution = distributionService.distribution;
  $scope.selectedFacilityId = $routeParams.facility;
  $scope.childCoverage = $scope.distribution.facilityDistributions[$scope.selectedFacilityId].childCoverage;

  $scope.columns = {
    vaccination: "label.child.vaccination.doses",
    targetGroup: "label.child.coverage.target.Group",
    childrenAgeGroup0To11: "label.children.age.group.zero.eleven.months",
    childrenAgeGroup12To23: "label.children.age.group.twelve.twenty.three.months",
    categoryOneHealthCenter: "label.child.coverage.health.center.first.category",
    categoryOneMobileBrigade: "label.child.coverage.mobile.brigade.first.category",
    categoryOneTotal: "label.child.coverage.first.total",
    coverageRate: "label.child.coverage.coverage.rate",
    categoryTwoHealthCenter: "label.child.coverage.health.center.second.category",
    categoryTwoMobileBrigade: "label.child.coverage.mobile.brigade.second.category",
    categoryTwoTotal: "label.child.coverage.first.total",
    totalVaccination: "label.child.coverage.total.vaccination",
    openedVials: "label.child.coverage.opened.vials",
    openedVialsWastageRate: "label.child.coverage.opened.vials.wastage.rate"
  };

  $scope.productsMap = {
    "BCG": {
      products: ['BCG'],
      rowSpan: 1
    },
    "Polio (Newborn)": {
      products: ['Polio10', 'Polio20'],
      rowSpan: 4
    },
    "Penta 1st dose": {
      products: ['Penta1', 'Penta10'],
      rowSpan: 3
    },
    "PCV10 1st dose": {
      products: ['PCV'],
      rowSpan: 3
    },
    "Measles": {
      products: ['Measles'],
      rowSpan: 1
    }
  }

  $scope.applyNRAll = function () {
    distributionService.applyNR(function (distribution) {
      distribution.setChildCoverageNotRecorded($routeParams.facility);
    });
  };

  var list = ["BCG", "Polio (Newborn)", "Penta 1st dose", "PCV10 1st dose", "Measles"];

  $scope.hideCell = function (vaccination) {
    if (list.indexOf(vaccination) !== -1)
      return false;
    else
      return true;
  }
}