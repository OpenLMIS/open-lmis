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

  var getValue = function (obj) {
    return (!isUndefined(obj)) ? ((!isUndefined(obj.value)) ? parseInt(obj.value, 10) : 0) : 0;
  };

  var syncNR = function (product1, product2) {
    if (!isUndefined($scope.openedVialMap[product2].openedVial) && !isUndefined($scope.openedVialMap[product1].openedVial))
      $scope.openedVialMap[product1].openedVial.notRecorded = $scope.openedVialMap[product2].openedVial.notRecorded;
  };

  var convertListToMap = function (list, key) {
    var map = {};
    list.forEach(function (lineItem) {
      map[lineItem[key]] = lineItem;
    });
    return map;
  };

  $scope.childCoverageMap = convertListToMap($scope.childCoverage.childCoverageLineItems, 'vaccination');
  $scope.openedVialMap = convertListToMap($scope.childCoverage.openedVialLineItems, 'productVialName');

  $scope.columns = {
    vaccination: "label.child.vaccination.doses",
    targetGroup: "label.coverage.target.Group",
    childrenAgeGroup0To11: "label.children.age.group.zero.eleven.months",
    childrenAgeGroup12To23: "label.children.age.group.twelve.twenty.three.months",
    categoryOneHealthCenter: "label.coverage.health.center",
    categoryOneMobileBrigade: "label.coverage.outreach",
    categoryOneTotal: "label.child.coverage.first.total",
    coverageRate: "label.coverage.rate",
    categoryTwoHealthCenter: "label.coverage.health.center",
    categoryTwoMobileBrigade: "label.coverage.outreach",
    categoryTwoTotal: "label.child.coverage.first.total",
    totalVaccination: "label.child.coverage.total.vaccination",
    openedVials: "label.coverage.opened.vials",
    openedVialsWastageRate: "label.coverage.opened.vials.wastage.rate"
  };

  $scope.productsMap = {
    "BCG": {
      products: ['BCG'],
      vaccinations: ['BCG'],
      rowSpan: 1
    },
    "Polio (Newborn)": {
      products: ['Polio10', 'Polio20'],
      vaccinations: ['Polio (Newborn)', 'Polio 1st dose', 'Polio 2nd dose', 'Polio 3rd dose'],
      rowSpan: 4
    },
    "Penta 1st dose": {
      products: ['Penta1', 'Penta10'],
      vaccinations: ['Penta 1st dose', 'Penta 2nd dose', 'Penta 3rd dose'],
      rowSpan: 3
    },
    "PCV10 1st dose": {
      products: ['PCV'],
      vaccinations: ['PCV10 1st dose', 'PCV10 2nd dose', 'PCV10 3rd dose'],
      rowSpan: 3
    },
    "Measles": {
      products: ['Measles'],
      vaccinations: ['Measles'],
      rowSpan: 1
    }
  };

  $scope.hideCell = function (vaccination) {
    return Object.keys($scope.productsMap).indexOf(vaccination) === -1;
  };

  $scope.$watch("openedVialMap['Polio20'].openedVial.notRecorded", function () {
    syncNR('Polio10', 'Polio20');
  });

  $scope.$watch("openedVialMap['Penta10'].openedVial.notRecorded", function () {
    syncNR('Penta1', 'Penta10');
  });

  $scope.getTotal = function (obj1, obj2) {
    return getValue(obj1) + getValue(obj2);
  };

  $scope.getTotalVaccinations = function (childCoverageLineItem) {
    return $scope.getTotal(childCoverageLineItem.healthCenter11Months, childCoverageLineItem.outreach11Months) +
      $scope.getTotal(childCoverageLineItem.healthCenter23Months, childCoverageLineItem.outreach23Months);
  };

  $scope.calculateCoverageRate = function (total, targetGroup) {
    return (isUndefined(targetGroup) ? null : (targetGroup === 0 ? null : Math.round((total / targetGroup) * 100)));
  };

  $scope.calculateWastageRate = function (productsForVaccination) {
    var totalDosesConsumed = 0;
    var totalVaccinations = 0;
    if (!isUndefined(productsForVaccination)) {
      $.each(productsForVaccination.products, function (index, product) {
        var openedVialLineItem = $scope.openedVialMap[product];
        if (!isUndefined(openedVialLineItem.packSize) && !isUndefined(openedVialLineItem.openedVial) && !isUndefined(openedVialLineItem.openedVial.value)) {
          totalDosesConsumed += openedVialLineItem.packSize * openedVialLineItem.openedVial.value;
        }
      });

      $.each(productsForVaccination.vaccinations, function (index, vaccination) {
        var childCoverageLineItem = $scope.childCoverageMap[vaccination];
        totalVaccinations += $scope.getTotalVaccinations(childCoverageLineItem);
      });
    }
    return totalDosesConsumed === 0 ? null : Math.round(((totalDosesConsumed - totalVaccinations) / totalDosesConsumed) * 100);
  };

  $scope.applyNRAll = function () {
    distributionService.applyNR(function () {
      $scope.childCoverage.setNotRecorded();
    });
  };
}