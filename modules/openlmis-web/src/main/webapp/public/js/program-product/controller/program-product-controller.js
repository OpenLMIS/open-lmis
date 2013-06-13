/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */


function ProgramProductController($scope, programs, ProgramProducts, ProgramProductsISA) {

  $scope.programs = programs;
  $scope.population = 0;
  $scope.isaValue = 0;

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
  }

  $scope.showProductISA = function (programProduct) {
    $scope.currentProgramProduct = programProduct;
    $scope.programProductISAModal = true;
  }

  $scope.clearAndCloseProgramProductISAModal = function () {
    $scope.currentProgramProduct = null;
    $scope.programProductISAModal = false;
  }

  $scope.highlightRequired = function (value) {
    if ($scope.inputClass && (isUndefined(value))) {
      return "required-error";
    }
    return null;
  };

  $scope.$watch('currentProgramProduct', function () {
    if ($scope.currentProgramProduct)
      $scope.calculateValue($scope.currentProgramProduct.programProductISA);
  }, true);

  $scope.saveProductISA = function () {
    if ($scope.isaForm.$error.required) {
      $scope.inputClass = true;
      $scope.error = "Please fill required values"
      $scope.message = "";
    } else {
      $scope.inputClass = false;
      ProgramProductsISA.save({programProductId:$scope.currentProgramProduct.id}, $scope.currentProgramProduct.programProductISA, function () {
        $scope.message = "ISA saved successfully";
        setTimeout(function () {
          $scope.$apply(function () {
            angular.element("#saveSuccessMsgDiv").fadeOut('slow', function () {
              $rootScope.message = '';
            });
          });
        }, 3000);
        $scope.error = "";
        $scope.programProductISAModal = false;
      }, {});
    }
  }

  $scope.isPresent = function (programProductISA) {
    return programProductISA && programProductISA.whoRatio && programProductISA.dosesPerYear && programProductISA.wastageRate
      && programProductISA.bufferPercentage && programProductISA.adjustmentValue;
  }

  $scope.getFormula = function (programProductISA) {
    if ($scope.isPresent(programProductISA))
      return "(population) * " + programProductISA.whoRatio + " * " + programProductISA.dosesPerYear + " * "
        + programProductISA.wastageRate + " / 12 * " + programProductISA.bufferPercentage + " + " + programProductISA.adjustmentValue;
    else "";
  }

  $scope.calculateValue = function (programProductISA) {
    if ($scope.isPresent(programProductISA) && $scope.population) {
      $scope.isaValue = $scope.population * programProductISA.whoRatio * programProductISA.dosesPerYear *
        programProductISA.wastageRate / 12 * programProductISA.bufferPercentage + programProductISA.adjustmentValue;
      $scope.isaValue = Math.ceil($scope.isaValue);
    }else{
      $scope.isaValue = 0;
    }

  }
}

ProgramProductController.resolve = {
  programs:function ($q, PushProgram, $location, $route, $timeout) {
    var deferred = $q.defer();

    $timeout(function () {
      PushProgram.get({}, function (data) {
        deferred.resolve(data.programs);
      }, function () {
        location.path('/');
      });
    }, 100);

    return deferred.promise;
  }
};



