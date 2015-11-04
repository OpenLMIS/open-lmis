/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function ProgramProductController($scope, programs, ProgramProducts, ProgramProductsISA)
{
  $scope.programs = programs;
  $scope.isaService = ProgramProductsISA; //isaService is used by ISACoefficientsModalController, which is intended to be used as a descendant controller of this one.

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

}

ProgramProductController.resolve = {
  programs: function ($q, Programs, $location, $route, $timeout) {
    var deferred = $q.defer();

    $timeout
    (
              function()
              {
                Programs.get({type: ''}, function(data)
                                         {
                                            deferred.resolve(data.programs);
                                         },

                                         function()
                                         {
                                            location.path('/');
                                         }
                            );
              },
              100
    );

    return deferred.promise;
  }
};



