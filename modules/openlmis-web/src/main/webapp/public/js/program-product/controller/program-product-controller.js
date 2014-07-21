/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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
  };

  $scope.filterProducts = function () {
    $scope.filteredProducts = [];
    var query = $scope.query || "";

    $scope.filteredProducts = $.grep($scope.programProducts, function (programProduct) {
      return programProduct.product.primaryName.toLowerCase().indexOf(query.toLowerCase()) != -1;
    });
  };

  $scope.showProductISA = function (programProduct) {
    $scope.inputClass = false;
    $scope.population = 0;
    $scope.isaValue = 0;
    $scope.error = null;
    angular.element(".form-error").hide();
    if (programProduct.programProductIsa === undefined || programProduct.programProductIsa.id === undefined)
      programProduct.programProductIsa = new ProgramProductISA();

    $scope.previousProgramProduct = angular.copy(programProduct);
    $scope.programProductISAModal = true;
  };

  $scope.clearAndCloseProgramProductISAModal = function () {
    $scope.population = 0;
    $scope.inputClass = false;
    $scope.previousProgramProduct = null;
    $scope.programProductISAModal = false;
  };

  $scope.highlightRequired = function (value) {
    if ($scope.inputClass && (isUndefined(value))) {
      return "required-error";
    }
    return null;
  };


  $scope.saveProductISA = function () {
    if (validateForm($scope.previousProgramProduct.programProductIsa)) {
      $scope.inputClass = false;
      if ($scope.previousProgramProduct.programProductIsa.id)
        ProgramProductsISA.update({programProductId: $scope.previousProgramProduct.id, isaId: $scope.previousProgramProduct.programProductIsa.id},
            $scope.previousProgramProduct.programProductIsa, successCallBack, {});
      else
        ProgramProductsISA.save({programProductId: $scope.previousProgramProduct.id}, $scope.previousProgramProduct.programProductIsa, successCallBack, {});
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
  };
}

ProgramProductController.resolve = {
  programs: function ($q, Programs, $location, $route, $timeout) {
    var deferred = $q.defer();

    $timeout(function () {
      Programs.get({type: 'push'}, function (data) {
        deferred.resolve(data.programs);
      }, function () {
        location.path('/');
      });
    }, 100);

    return deferred.promise;
  }
};



