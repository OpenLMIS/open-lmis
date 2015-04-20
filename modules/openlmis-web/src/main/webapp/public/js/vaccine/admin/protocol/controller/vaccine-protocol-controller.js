/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function VaccineProtocolController($scope, programs, protocol, SaveVaccineProductDose, VaccineProductDose) {

  $scope.programs = programs;
  $scope.protocol = protocol;

  $scope.onProgramChanged = function(){
    VaccineProductDose.get({programId: $scope.program}, function (data) {
      $scope.protocol = data.protocol;
    });
  };


  $scope.save = function(){
    SaveVaccineProductDose.update($scope.protocol, function(data){
      $scope.message = 'Protocol Saved Successfully';
    });
  };

  $scope.addProduct = function(product, scope){
    scope.showAddNewModal = false;
    var dose = $scope.protocol.possibleDoses[0]
    $scope.protocol.protocols.push({productName: product.primaryName, doses: [{doseId: dose.id, productId: product.id, programId: $scope.program, displayName: dose.displayName, trackMale: true, trackFemale: true}]});
  };
}

VaccineProtocolController.resolve = {
  protocol : function($q, $timeout, $route, VaccineProductDose){
    var deferred = $q.defer();

    if($route.current.params.program)
    {
      $timeout(function() {
        VaccineProductDose.get({programId: $route.current.params.program}, function (data) {
          deferred.resolve(data.protocol);
        });
      }, 100);
    }else{
      return {};
    }
    return deferred.promise;
  },
  programs: function($q, $timeout, Programs){
    var deferred = $q.defer();

    $timeout(function(){
      Programs.get({type: 'push'}, function(data){
        deferred.resolve(data.programs);
      });
    },100);

    return deferred.promise;
  }
};