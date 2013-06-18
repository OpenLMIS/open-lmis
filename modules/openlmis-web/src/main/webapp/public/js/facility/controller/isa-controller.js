/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
function IsaModalController($scope, ProgramProducts) {

  $scope.$watch('currentProgram', function () {
    if (!$scope.currentProgram) return;

    if ($scope.allocationProgramProducts[$scope.currentProgram.id]) return;

    ProgramProducts.get({programId: $scope.currentProgram.id}, function (data) {
      $scope.allocationProgramProducts[$scope.currentProgram.id] = data.programProductList;

      var population = $scope.$parent.facility.catchmentPopulation;

      if (isUndefined(population)) return;

      $($scope.allocationProgramProducts[$scope.currentProgram.id]).each(function (index, product) {

        if (isUndefined(product.programProductIsa))

          product.calculatedIsa = Math.ceil(parseInt(population) * parseInt(product.programProductIsa.whoRatio) * parseInt(product.programProductIsa.dosesPerYear) *
            parseInt(product.programProductIsa.wastageRate) / 12 * parseInt(product.programProductIsa.bufferPercentage) + parseInt(product.programProductIsa.adjustmentValue));

      });

    }, function (data) {
    });
  });

  $scope.saveISA = function () {

  }

}
