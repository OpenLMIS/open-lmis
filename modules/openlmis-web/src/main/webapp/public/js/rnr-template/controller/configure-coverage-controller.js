/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function ConfigureCoverageController($scope, $routeParams, SaveVaccineProductDose, VaccineProductDose) {

  $scope.program = $routeParams.programId;

  VaccineProductDose.get({programId: $scope.program}, function (data) {
    $scope.protocol = data.protocol;
    $scope.$parent.protocol = $scope.protocol;
  });

  $scope.addDosageForProduct = function(product){
    var dose = $scope.protocol.possibleDoses[product.doses.length];
    if(dose !== undefined){
      var newEntry = {doseId: dose.id, productId: product.productId, programId: $scope.program, displayName: dose.name, trackMale: true, displayOrder: product.doses.length + 1 , trackFemale: true};
      product.doses.push(newEntry);
    }
  };

  $scope.removeDosageFromProduct = function(product){
      product.doses.pop();
  };

  $scope.$parent.saveProtocols = function(){
    SaveVaccineProductDose.update($scope.protocol, function(data){
      $scope.$parent.message = 'label.vaccine.settings.coverage.configuration.saved';
    });
  };

  $scope.addProduct = function(product, scope){
    scope.showAddNewModal = false;
    var dose = $scope.protocol.possibleDoses[0];
    $scope.protocol.protocols.push({productName: product.primaryName, productId: product.id , doses: [{doseId: dose.id, productId: product.id, programId: $scope.program, displayOrder: 1, displayName: dose.name, trackMale: true, trackFemale: true}]});
  };
}