/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function EPIUseController($scope, $routeParams, distributionService) {

  $scope.epiLineItems = [];
  $scope.selectedFacilityId = $routeParams.facility;

  $scope.$on('distributionReceived', function () {
    $scope.distribution = distributionService.distribution;
    $($scope.distribution.facilityDistributionData[$scope.selectedFacilityId].epiUse.productGroups).each(function (index, group) {
      $scope.epiLineItems[index] = {productGroup: group};
    });
  })

  if ($scope.distribution == undefined) {
    $scope.distribution = distributionService.distribution;
    $($scope.distribution.facilityDistributionData[$scope.selectedFacilityId].epiUse.productGroups).each(function (index, group) {
      $scope.epiLineItems[index] = {productGroup: group};
    });
  }

  $scope.headerColumns = ["EPI Stock(doses)", "Stock at first of month", "Received", "Total", "Distributed", "Loss", "Stock at end of month", "Expiration Date(MM/YYYY)"];

};
