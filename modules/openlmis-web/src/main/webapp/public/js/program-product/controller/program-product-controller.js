/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ProgramProductListController($scope, programs, ProgramProducts, ProgramProductsISA) {

  $scope.programs = programs;


  $scope.loadProgramProducts = function () {
    if ($scope.programId) {
      ProgramProducts.get({programId:$scope.programId}, function (data) {
        $scope.programProducts = data.PROGRAM_PRODUCT_LIST;
        $scope.filteredProducts = data.PROGRAM_PRODUCT_LIST;
      }, {});
    }
  };


  $scope.filterProducts = function () {
    $scope.filteredProducts = [];
    var query = $scope.query || "";

    $scope.filteredProducts = $.grep($scope.programProducts, function (programProduct) {
      return programProduct.product.primaryName.toLowerCase().indexOf(query.toLowerCase()) != -1;
      ;
    });

    $scope.resultCount = $scope.filteredProducts.length;
  }

  $scope.showProductISA = function (programProduct) {
    $scope.currentProgramProduct = programProduct;
    $scope.programProductISAModal = true;
  }

  $scope.clearAndCloseProgramProductISAModal = function () {
    $scope.currentProgramProduct = null;
    $scope.programProductISAModal = false;
  }


  $scope.saveProductISA = function () {
    if ($scope.isaForm.$error.required) {

    } else {
      ProgramProductsISA.save({programProductId:$scope.currentProgramProduct.id}, $scope.currentProgramProduct.programProductISA, function () {
        $scope.message = "ISA saved successfully";
        $scope.programProductISAModal = false;
      }, {});
    }
  }

  $scope.getFormula = function (programProductISA) {
    if (programProductISA && programProductISA.whoRatio && programProductISA.dosesPerYear && programProductISA.wastageRate && programProductISA.bufferPercentage)
      return "(population) * " + programProductISA.whoRatio + " * " + programProductISA.dosesPerYear + " * "
        + programProductISA.wastageRate + " * / 12 * " + programProductISA.bufferPercentage;
    else "";
  }
}

ProgramProductListController.resolve = {
  programs:function ($q, PushProgram, $location, $route, $timeout) {
    var deferred = $q.defer();

    $timeout(function () {
      PushProgram.get({}, function (data) { //success
        deferred.resolve(data.programs);
      }, function () {
        location.path('/');
      });
    }, 100);

    return deferred.promise;
  }
};



