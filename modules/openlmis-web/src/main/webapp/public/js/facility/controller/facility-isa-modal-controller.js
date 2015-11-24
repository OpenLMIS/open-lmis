/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
function FacilityIsaModalController($scope, $rootScope, FacilityProgramProducts, ProgramProducts, $routeParams)
{

  //Set product.programProductIsa
  function calculateAndSetProgramProductIsa(product)
  {
    product.calculatedIsa = getISA(product.programProductIsa);
  }

  //Set product.calculatedFacilityIsa, which is intended to override product.programProductIsa
  function calculateAndSetFacilityIsa(product)
  {
    product.calculatedFacilityIsa = getISA(product.overriddenIsa);
  }

  function setOveriddenIsaFormula(product)
  {
    if(product.overriddenIsa)
    {
      var isa = new ProgramProductISA();
      isa.init(product.overriddenIsa);
      product.overriddenIsaFormula = isa.getIsaFormula();
    }
    else
    {
      product.overriddenIsaFormula = '';
    }
  }

  //Return an ISA value based on the specified isaCoefficients
  function getISA(isaCoefficients)
  {
    var _EMPTY = '--';
    var population = $scope.$parent.facility.catchmentPopulation;
    var isa;

    //Basic validation
    if( isUndefined(population) || isUndefined(isaCoefficients) )
      return  _EMPTY;

    isa = new ProgramProductISA();
    isa.init(isaCoefficients);
    return isa.calculate(population);
  }

  //For each product in productList, set product.calculatedIsa and product.calculatedFacilityIsa
  function calculateIsaValues(products)
  {
    $(products).each(function (index, product)
    {
      calculateAndSetProgramProductIsa(product);
      calculateAndSetFacilityIsa(product);
      setOveriddenIsaFormula(product);
    });
  }

  $scope.$on('showISAEditModal', function()
  {
    if(!$scope.currentProgram)
      return;

    $scope.currentProgramProducts = [];

    function calculateISAAndShowModel()
    {
      calculateIsaValues($scope.$parent.facilityProgramProductsList[$scope.currentProgram.id]);
      $scope.filteredProducts = $scope.currentProgramProducts = angular.copy($scope.$parent.facilityProgramProductsList[$scope.currentProgram.id]);
      $scope.programProductsISAModal = true; //Show the modal
    }

    function successFunc(data)
    {
      $scope.$parent.facilityProgramProductsList[$scope.currentProgram.id] = data.programProductList;
      calculateISAAndShowModel();
    }

    if ($routeParams.facilityId)
      FacilityProgramProducts.get({programId: $scope.currentProgram.id, facilityId: $routeParams.facilityId}, successFunc, function (data) {});
    else
      ProgramProducts.get({programId: $scope.currentProgram.id}, successFunc, function (data) {});

  });

  $rootScope.$on('updateISA', function(event, data)
  {
    if(!$scope.filteredProducts)
      return;

    $scope.filteredProducts[data.index].overriddenIsa = data.isa;
    //calculateAndSetProgramProductIsa( $scope.filteredProducts[data.index] );
    calculateAndSetFacilityIsa( $scope.filteredProducts[data.index] );
    setOveriddenIsaFormula( $scope.filteredProducts[data.index] );
  });

  $scope.closeISAModal = function () {
    $scope.programProductsISAModal = false;
  };


  //If the user specified a search-string, filter $scope.filteredProducts accordingly
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
