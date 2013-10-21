/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
function IsaModalController($scope, FacilityProgramProducts, ProgramProducts, $routeParams) {

  function calculateIsa(products) {
    $(products).each(function (index, product) {

      var population = $scope.$parent.facility.catchmentPopulation;

      if (isUndefined(population) || isUndefined(product.programProductIsa)) {
        product.calculatedIsa = "--";
        return;
      }
      var programProductIsa = new ProgramProductISA();
      programProductIsa.init(product.programProductIsa);

      product.calculatedIsa = programProductIsa.calculate(population);
    });
  }

  $scope.$on('showISAEditModal', function () {

    if (!$scope.currentProgram) return;

    $scope.currentProgramProducts = [];

    function calculateISAAndShowModel() {
      calculateIsa($scope.$parent.facilityProgramProductsList[$scope.currentProgram.id]);
      $scope.filteredProducts = $scope.currentProgramProducts = angular.copy($scope.$parent.facilityProgramProductsList[$scope.currentProgram.id]);
      $scope.programProductsISAModal = true;
    }

    if ($scope.$parent.facilityProgramProductsList[$scope.currentProgram.id]) {
      calculateISAAndShowModel();
      return;
    }

    var successFunc = function (data) {
      $scope.$parent.facilityProgramProductsList[$scope.currentProgram.id] = data.programProductList;
      calculateISAAndShowModel();
    };

    if ($routeParams.facilityId) {
      FacilityProgramProducts.get({programId: $scope.currentProgram.id, facilityId: $routeParams.facilityId}, successFunc, function (data) {
      });
    } else {
      ProgramProducts.get({programId: $scope.currentProgram.id}, successFunc, function (data) {
      });
    }

  });

  $scope.updateISA = function () {
    $scope.$parent.facilityProgramProductsList[$scope.currentProgram.id] = angular.copy($scope.currentProgramProducts);
    $scope.$parent.programProductsISAModal = false;
  };

  $scope.resetISAModal = function () {
    $scope.$parent.programProductsISAModal = false;
  };

  $scope.resetAllToCalculatedIsa = function () {
    $($scope.currentProgramProducts).each(function (index, product) {
      product.overriddenIsa = null;
    });
  };

  $scope.updateCurrentProgramProducts = function () {
    $scope.filteredProducts = [];
    $scope.query = $scope.query.trim();

    if (!$scope.query.length) {
      $scope.filteredProducts = $scope.currentProgramProducts;
      return;
    }

    $($scope.currentProgramProducts).each(function (index, product) {
      var searchString = $scope.query.toLowerCase();
      if (product.product.primaryName.toLowerCase().indexOf(searchString) >= 0) {
        $scope.filteredProducts.push(product);
      }
    });
  };
}
