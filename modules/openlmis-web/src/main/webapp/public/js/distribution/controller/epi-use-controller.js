/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function EPIUseController($scope, $routeParams, distributionService) {


  $scope.selectedFacilityId = $routeParams.facility;

  $scope.$on('distributionReceived', function () {
    $scope.distribution = distributionService.distribution;
    $scope.productGroupReadings = $scope.distribution.facilityDistributionData[$scope.selectedFacilityId].epiUse.productGroups;
    $($scope.productGroupReadings).each(function (index, groupReading) {
      groupReading.reading = {stockAtFirstOfMonth: "", received: "", total: "", distributed: "", loss: "", stockAtEndOfMonth: "", expirationDate: ""}
    });
  })

  if ($scope.distribution == undefined && distributionService.distribution != undefined) {
    $scope.distribution = distributionService.distribution;
    $scope.productGroupReadings = $scope.distribution.facilityDistributionData[$scope.selectedFacilityId].epiUse.productGroups;
    $($scope.productGroupReadings).each(function (index, groupReading) {
      groupReading.reading = {stockAtFirstOfMonth: "", received: "", total: "", distributed: "", loss: "", stockAtEndOfMonth: "", expirationDate: ""}
    });
  }

  $scope.headerColumns = ["EPI Stock(doses)", "Stock at first of month", "Received", "Total", "Distributed", "Loss", "Stock at end of month", "Expiration Date(MM/YYYY)"];

};
