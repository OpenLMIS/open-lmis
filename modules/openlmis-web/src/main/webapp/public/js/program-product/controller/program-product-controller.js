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
        $scope.programProducts = data.programProductList;
        $scope.filteredProducts = data.programProductList;
        $scope.assignFormula($scope.filteredProducts);
      }, {});
    }
  };

  $scope.assignFormula = function (list) {
    $.each(list, function (index, programProduct) {
      programProduct.formula = $scope.getFormula(programProduct.programProductIsa);
    });
  }

  $scope.filterProducts = function () {
    $scope.filteredProducts = [];
    var query = $scope.query || "";

    $scope.filteredProducts = $.grep($scope.programProducts, function (programProduct) {
      return programProduct.product.primaryName.toLowerCase().indexOf(query.toLowerCase()) != -1;
    });
  }

  $scope.showProductISA = function (programProduct) {
    $scope.inputClass = false;
    $scope.population = 0;
    $scope.error = null;
    programProduct.previousFormula = programProduct.formula;
    $scope.currentProgramProduct = angular.copy(programProduct);
    $scope.programProductISAModal = true;
  }

  $scope.clearAndCloseProgramProductISAModal = function () {
    if ($scope.currentProgramProduct && $scope.currentProgramProduct.formula == null) {
      $scope.currentProgramProduct.formula = $scope.currentProgramProduct.previousFormula;
    }
    $scope.population = 0;
    $scope.inputClass = false;
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
    if ($scope.currentProgramProduct) {
      $scope.calculateValue($scope.currentProgramProduct.programProductIsa);
    }
  }, true);

  $scope.saveProductISA = function () {
    if ($scope.isaForm.$error.required) {
      $scope.inputClass = true;
      $scope.error = "form.error";
      $scope.message = "";
    } else {
      $scope.inputClass = false;
      if ($scope.currentProgramProduct.programProductIsa.id)
        ProgramProductsISA.update({programProductId:$scope.currentProgramProduct.id, isaId:$scope.currentProgramProduct.programProductIsa.id},
          $scope.currentProgramProduct.programProductIsa, successCallBack, {});
      else
        ProgramProductsISA.save({programProductId:$scope.currentProgramProduct.id}, $scope.currentProgramProduct.programProductIsa, successCallBack, {});
    }
  };

  var successCallBack = function () {
    $scope.message = "message.isa.save.success";
    setTimeout(function () {
      $scope.$apply(function () {
        angular.element("#saveSuccessMsgDiv").fadeOut('slow', function () {
          $scope.message = '';
        });
      });
    }, 3000);
    $scope.error = "";
    $scope.programProductISAModal = false;
    $scope.loadProgramProducts();
  };

  function isDefined(value) {
    return !(value == null || value == undefined || value == "-");
  }


  $scope.isPresent = function (programProductIsa) {
    var present = programProductIsa && isDefined(programProductIsa.whoRatio) && isDefined(programProductIsa.dosesPerYear) &&
      isDefined(programProductIsa.wastageRate) && isDefined(programProductIsa.bufferPercentage) &&
      isDefined(programProductIsa.adjustmentValue);
    if (present) $scope.error = null;
    return present;
  };

  $scope.getFormula = function (programProductIsa) {
    if ($scope.isPresent(programProductIsa)) {
      var adjustmentVal = parseInt(programProductIsa.adjustmentValue, 10) > 0 ? programProductIsa.adjustmentValue : "(" + programProductIsa.adjustmentValue + ")";
      return "(population) * " + programProductIsa.whoRatio + " * " + programProductIsa.dosesPerYear + " * "
        + programProductIsa.wastageRate + " / 12 * " + programProductIsa.bufferPercentage + " + " + adjustmentVal;
    }
  };

  $scope.calculateValue = function (programProductIsa) {
    if ($scope.isPresent(programProductIsa) && $scope.population) {
      $scope.isaValue = parseInt($scope.population, 10) * parseInt(programProductIsa.whoRatio, 10) * parseInt(programProductIsa.dosesPerYear, 10) *
        parseInt(programProductIsa.wastageRate, 10) / 12 * parseInt(programProductIsa.bufferPercentage, 10) + parseInt(programProductIsa.adjustmentValue, 10);
      $scope.isaValue = Math.ceil($scope.isaValue);
    } else {
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



