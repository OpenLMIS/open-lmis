/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
function IsaModalController($scope, FacilityProgramProducts, $routeParams) {

  $scope.$watch('$parent.programProductsISAModal', function () {
    if (!$scope.$parent.programProductsISAModal) return;

    if (!$scope.currentProgram) return;

    $scope.currentProgramProducts = [];

    if ($scope.$parent.allocationProgramProductsList[$scope.currentProgram.id]) {
      $scope.currentProgramProducts = angular.copy($scope.$parent.allocationProgramProductsList[$scope.currentProgram.id]);
      return;
    }

    FacilityProgramProducts.get({programId: $scope.currentProgram.id, facilityId: $routeParams.facilityId}, function (data) {

      $scope.$parent.allocationProgramProductsList[$scope.currentProgram.id] = angular.copy(data.programProductList);

      $($scope.$parent.allocationProgramProductsList[$scope.currentProgram.id]).each(function (index, product) {

        var population = $scope.$parent.facility.catchmentPopulation;

        if (isUndefined(population) || isUndefined(product.programProductIsa)) return;

        product.calculatedIsa = Math.ceil(utils.parseIntWithBaseTen(population) * utils.parseIntWithBaseTen(product.programProductIsa.whoRatio) *
          utils.parseIntWithBaseTen(product.programProductIsa.dosesPerYear) * utils.parseIntWithBaseTen(product.programProductIsa.wastageRate) / 12 *
          utils.parseIntWithBaseTen(product.programProductIsa.bufferPercentage) + utils.parseIntWithBaseTen(product.programProductIsa.adjustmentValue));

      });

      $scope.currentProgramProducts = angular.copy($scope.$parent.allocationProgramProductsList[$scope.currentProgram.id]);

    }, function (data) {
    });
  });

  $scope.updateISA = function () {
    $scope.$parent.allocationProgramProductsList[$scope.currentProgram.id] = angular.copy($scope.currentProgramProducts);
    $scope.$parent.programProductsISAModal = false;
  }

  $scope.resetISAModal = function () {
    $scope.$parent.programProductsISAModal = false;
  }

}
