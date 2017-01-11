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
  $scope.distributionReview = distributionService.distributionReview;
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

  $scope.childCoverage.childCoverageLineItems = _.sortBy($scope.childCoverage.childCoverageLineItems, 'displayOrder');
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
      vaccinations: ['Polio (Newborn)', 'Polio 1a dose', 'Polio 2a dose', 'Polio 3a dose'],
      rowSpan: 4
    },
    "IPV": {
      products: ['IPV'],
      vaccinations: ['IPV'],
      rowSpan: 1
    },
    "Penta 1a dose": {
      products: ['Penta1', 'Penta10'],
      vaccinations: ['Penta 1a dose', 'Penta 2a dose', 'Penta 3a dose'],
      rowSpan: 3
    },
    "PCV10 1a dose": {
      products: ['PCV'],
      vaccinations: ['PCV10 1a dose', 'PCV10 2a dose', 'PCV10 3a dose'],
      rowSpan: 3
    },
    "RV Rotarix 1a dose": {
      products: ['RV Rotarix'],
      vaccinations: ['RV Rotarix 1a dose', 'RV Rotarix 2a dose'],
      rowSpan: 2
    },
    "Sarampo 1a dose": {
      products: ['Sarampo'],
      vaccinations: ['Sarampo 1a dose'],
      rowSpan: 1
    },
    "Sarampo 2a dose": {
      products: ['MSD'],
      vaccinations: ['Sarampo 2a dose'],
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

  $scope.getFormattedAsPercent = function (number) {
      return utils.getFormattedPercent(number);
  };

  $scope.getHTMLFormattedVaccinationName = function (vaccination) {
      return vaccination.replace('1a', '1<sup>a</sup>').replace('2a', '2<sup>a</sup>').replace('3a', '3<sup>a</sup>');
  };

  $scope.calculateWastageRate = function (productMapEntry) {
    var totalDosesConsumed = 0;
    var totalVaccinations = 0;
    if (!isUndefined(productMapEntry)) {
      $.each(productMapEntry.products, function (index, product) {
        var openedVialLineItem = $scope.openedVialMap[product];

        if (!isUndefined(openedVialLineItem.packSize) && !isUndefined(openedVialLineItem.openedVial) && !isUndefined(openedVialLineItem.openedVial.value)) {
          totalDosesConsumed += openedVialLineItem.packSize * openedVialLineItem.openedVial.value;
        }
      });

      $.each(productMapEntry.vaccinations, function (index, vaccination) {
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
