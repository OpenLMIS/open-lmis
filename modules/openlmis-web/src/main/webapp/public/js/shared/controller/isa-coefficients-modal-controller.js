/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function ISACoefficientsModalController($scope, $rootScope, $timeout)
{
  var successCallBack = function () {
    $scope.message = "message.isa.save.success";

    $timeout
    (
        function()
        {
          $scope.message = '';
        },
        2000
    );

    $scope.error = "";
    $scope.programProductISAModal = false;

    if(facilityId())
      $rootScope.$broadcast('updateISA', {index: $scope.currentIndex, isa: $scope.isaToEdit});
    else
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


  $scope.isaValue = 0;

  $scope.showProductISA = function(programProduct, index)
  {
    $scope.inputClass = false;
    $scope.isaValue = 0;
    $scope.error = null;

    //Used to answer the question "what would my ISA be for a given population," this value is arbitrary. It's both seen and easy changed by the user.
    $scope.population = 100;

    angular.element(".form-error").hide();
    if (programProduct.programProductIsa === undefined || programProduct.programProductIsa.id === undefined)
      programProduct.programProductIsa = new ProgramProductISA();

    if(facilityId())
    {
      if (programProduct.overriddenIsa === undefined /*|| programProduct.overriddenIsa.id === undefined*/)
        programProduct.overriddenIsa = new ProgramProductISA();
      else
        programProduct.overriddenIsa = new ProgramProductISA(programProduct.overriddenIsa);
    }

    $scope.currentIndex = index;
    $scope.currentProgramProduct = angular.copy(programProduct);

    if(facilityId())
      $scope.isaToEdit = $scope.currentProgramProduct.overriddenIsa;
    else
      $scope.isaToEdit = $scope.currentProgramProduct.programProductIsa;

    $scope.origPopulationSource = $scope.isaToEdit.populationSource;
    if( ! $scope.origPopulationSource) {
      $scope.origPopulationSource  = 0;
    }
    var origIndex = _.findIndex($scope.demographicCategories, function(value){return value.id==$scope.origPopulationSource;});
    $scope.currentPopulationSource = $scope.demographicCategories[origIndex];

    $scope.programProductISAModal = true;
  };

  $scope.populationSourceChanged = function()
  {
    console.log($scope.origPopulationSource);
    console.log($scope.currentPopulationSource);
    $scope.isaToEdit.populationSource = $scope.currentPopulationSource.id;
    console.log($scope.isaToEdit.populationSource );
  };

  $scope.clearAndCloseProgramProductISAModal = function()
  {
    $scope.population = 0;
    $scope.inputClass = false;
    $scope.currentProgramProduct = null;
    $scope.programProductISAModal = false;
  };

  $scope.highlightRequired = function (value) {
    if ($scope.inputClass && (isUndefined(value))) {
      return "required-error";
    }
    return null;
  };


  $scope.deleteProductISA = function(programProduct, facility, index)
  {
    //For now, only allow overridden ISA values to be deleted
    if(!facilityId())
      return;

    //Other data validation
    if(!programProduct || !facility)
      return;

    $scope.currentIndex = index;
    $scope.isaToEdit = null;

    var params = {};
    params.programProductId = programProduct.id;
    params.facilityId = facility.id;

    $scope.isaService.delete(params, undefined, successCallBack, {});
  };

  $scope.saveProductISA = function()
  {
    if(!validateForm($scope.isaToEdit))
      return;

    $scope.inputClass = false;

    var params = {};
    params.programProductId = $scope.currentProgramProduct.id;

    var saveFunction = $scope.isaService.save;
    if ($scope.isaToEdit.id && !facilityId())
    {
      params.isaId = $scope.isaToEdit.id;
      saveFunction = $scope.isaService.update;
    }

    if(facilityId())
      params.facilityId = facilityId();

    if($scope.isaToEdit.populationSource === 0)
      $scope.isaToEdit.populationSource = undefined;

    saveFunction(params, $scope.isaToEdit, successCallBack, {});
  };

  function facilityId()
  {
    if($scope.facility && $scope.facility.id)
      return $scope.facility.id;
    else
      return undefined;
  }


  $scope.calculateValue = function (programProductIsa) {
    if (!validateForm(programProductIsa))
      return;
    if (programProductIsa.isPresent())
      $scope.isaValue = programProductIsa.calculate($scope.population);
  };


}
