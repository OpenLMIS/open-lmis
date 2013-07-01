/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */


function ProgramProductController($scope, programs, ProgramProducts, ProgramProductsISA) {
  $scope.programs = programs;
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
      if (programProduct.programProductIsa) {
        var programProductIsa = new ProgramProductISA();
        programProductIsa.init(programProduct.programProductIsa);
        programProduct.programProductIsa = programProductIsa;
        programProduct.formula = programProduct.programProductIsa.getIsaFormula();
      }
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
    $scope.isaValue = 0;
    $scope.error = null;
    angular.element(".form-error").hide();
    if (programProduct.programProductIsa == undefined || programProduct.programProductIsa.id == undefined)
      programProduct.programProductIsa = new ProgramProductISA();

    $scope.currentProgramProduct = angular.copy(programProduct);
    $scope.programProductISAModal = true;
  }

  $scope.clearAndCloseProgramProductISAModal = function () {
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
    if (validateForm($scope.currentProgramProduct.programProductIsa)) {
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


  var validateForm = function (programProductIsa) {
    if ($scope.isaForm.$error.required) {
      $scope.inputClass = true;
      $scope.error = "form.error";
      $scope.message = "";
      return false;
    }
    if (programProductIsa.isMaxLessThanMinValue()) {
      $scope.error = "error.minimum.greater.than.maximum";
      $scope.message = "";
      $scope.population = 0;
      $scope.isaValue = 0;
      return false;
    }
    return true;
  };

  $scope.calculateValue = function (programProductIsa) {
    if (!validateForm(programProductIsa))
      return;
    if (programProductIsa.isPresent())
      $scope.isaValue = programProductIsa.calculate($scope.population);
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



