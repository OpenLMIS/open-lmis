/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ProgramProductListController($scope, programs, ProgramProducts) {

  $scope.programs = programs;
  $scope.programProductISAModal = false;

  $scope.loadProgramProducts = function () {
    if ($scope.programId) {
      ProgramProducts.get({programId:$scope.programId}, function (data) {
        $scope.programProducts = data.PROGRAM_PRODUCT_LIST;
        $scope.filteredProducts =  data.PROGRAM_PRODUCT_LIST;
      }, {});
    }
  };


  $scope.filterProducts = function() {
    $scope.filteredProducts = [];
    var query = $scope.query || "";

    $scope.filteredProducts = $.grep($scope.programProducts, function (programProduct) {
      return programProduct.product.primaryName.toLowerCase().indexOf(query.toLowerCase()) != -1;;
    });

    $scope.resultCount = $scope.filteredProducts.length;
  }

  $scope.showProductISA = function(programProduct) {
    $scope.currentProgramProduct = programProduct;
    $scope.programProductISAModal = true;

  }
  $scope.getFormula = function (programProductISA) {
    if(programProductISA)
    return "(population) * " + programProductISA.whoRatio + " * " + programProductISA.dosesPerMonth + " * "
      + programProductISA.wastageRate + " * / 12 * " + programProductISA.bufferPercentage ;
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



