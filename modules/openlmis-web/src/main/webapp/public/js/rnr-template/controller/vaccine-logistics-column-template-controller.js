/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function IVDLogisticsColumnTemplate($scope,$routeParams, VaccineColumnTemplate, VaccineColumnTemplateSave){

  VaccineColumnTemplate.get( {id: $routeParams.programId}, function(data){
    $scope.sortableColumns       = data.columns;
  });

  var updateDisplayOrder = function(){
    angular.forEach($scope.sortableColumns, function(column, index){
      column.displayOrder = index + 1;
    });
  };

  $scope.onSave = function(){
    updateDisplayOrder();
    VaccineColumnTemplateSave.update({columns: $scope.sortableColumns}, function(data){
      $scope.sortableColumns       = data.columns;
      $scope.message = 'Your changes have been saved!';
    });
  };
}