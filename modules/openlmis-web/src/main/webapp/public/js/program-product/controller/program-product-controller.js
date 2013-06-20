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
      ProgramProducts.get({programId: $scope.programId}, function (data) {
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
    if (programProduct.programProductIsa == null)
      programProduct.programProductIsa = {"adjustmentValue": 0, "bufferPercentage": 0, "dosesPerYear": 0, "wastageRate": 0, "whoRatio": 0};
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


  $scope.saveProductISA = function () {
    if ($scope.isaForm.$error.required) {
      $scope.inputClass = true;
      $scope.error = "form.error";
      $scope.message = "";
    }
    else if ((($scope.currentProgramProduct.programProductIsa.minimumValue != null) && ($scope.currentProgramProduct.programProductIsa.maximumValue != null))
        && (parseInt($scope.currentProgramProduct.programProductIsa.minimumValue, 10) > parseInt($scope.currentProgramProduct.programProductIsa.maximumValue, 10))) {
      $scope.error = "error.minimum.greater.than.maximum";
      $scope.message = "";
    }
    else {
      $scope.inputClass = false;
      if ($scope.currentProgramProduct.programProductIsa.id)
        ProgramProductsISA.update({programProductId: $scope.currentProgramProduct.id, isaId: $scope.currentProgramProduct.programProductIsa.id},
            $scope.currentProgramProduct.programProductIsa, successCallBack, {});
      else
        ProgramProductsISA.save({programProductId: $scope.currentProgramProduct.id}, $scope.currentProgramProduct.programProductIsa, successCallBack, {});
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
    return present;
  };

  $scope.getFormula = function (programProductIsa) {
    if ($scope.isPresent(programProductIsa)) {
      var adjustmentVal = utils.parseIntWithBaseTen(programProductIsa.adjustmentValue);
      adjustmentVal = adjustmentVal > 0 ? adjustmentVal : "(" + adjustmentVal + ")";
      return "(population) * " +
          (programProductIsa.whoRatio / 100).toFixed(3) +
          " * " + utils.parseIntWithBaseTen(programProductIsa.dosesPerYear) +
          " * " + (1 + programProductIsa.wastageRate / 100).toFixed(3) +
          " / 12 * " + (1 + programProductIsa.bufferPercentage / 100).toFixed(3) +
          " + " + adjustmentVal;
    }
  };

  $scope.calculateValue = function (programProductIsa) {
    if ($scope.population) {
      $scope.isaValue = parseInt($scope.population, 10) *
          (utils.parseIntWithBaseTen(programProductIsa.whoRatio) / 100) *
          (utils.parseIntWithBaseTen(programProductIsa.dosesPerYear)) *
          (1 + utils.parseIntWithBaseTen(programProductIsa.wastageRate) / 100) / 12 *
          (1 + utils.parseIntWithBaseTen(programProductIsa.bufferPercentage) / 100) +
          (utils.parseIntWithBaseTen(programProductIsa.adjustmentValue));
      $scope.isaValue = $scope.isaValue < 0 ? 0 : Math.ceil($scope.isaValue);
    } else {
      $scope.isaValue = 0;
    }
  }
}

ProgramProductController.resolve = {
  programs: function ($q, PushProgram, $location, $route, $timeout) {
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



