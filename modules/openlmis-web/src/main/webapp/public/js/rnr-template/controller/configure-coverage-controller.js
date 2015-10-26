/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
function ConfigureCoverageController($scope, $routeParams, DemographicEstimateCategories, SaveVaccineProductDose, VaccineProductDose) {

  $scope.program = $routeParams.programId;

  DemographicEstimateCategories.get({}, function (data) {
    $scope.demographicCategories = data.estimate_categories;
  });


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
    SaveVaccineProductDose.update($scope.protocol, function(){
      $scope.$parent.message = 'label.vaccine.settings.coverage.configuration.saved';
    });
  };

  $scope.addProduct = function(product, scope){
    scope.showAddNewModal = false;
    var dose = $scope.protocol.possibleDoses[0];
    $scope.protocol.protocols.push({productName: product.primaryName, productId: product.id , doses: [{doseId: dose.id, productId: product.id, programId: $scope.program, displayOrder: 1, displayName: dose.name, trackMale: true, trackFemale: true}]});
  };
}
