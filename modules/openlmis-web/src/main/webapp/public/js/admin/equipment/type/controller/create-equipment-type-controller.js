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

function CreateEquipmentTypeController($scope, $routeParams, $location, EquipmentType, SaveEquipmentType) {

  $scope.$parent.message = '';

  if (isUndefined($routeParams.id)) {
    $scope.equipment_type = {};
  } else {
    EquipmentType.get({
      id: $routeParams.id
    }, function (data) {
      $scope.equipment_type = data.equipment_type;
    });
  }


  $scope.saveEquipmentType = function () {
    // clear the error message
    $scope.error = undefined;

    var onSuccess = function(){
      $scope.$parent.message = 'Your changes have been saved!';
      $location.path('');
    };

    var onError = function(data){
      $scope.showError = true;
      $scope.error = data.data.error;
    };

    if(!$scope.equipmentTypeForm.$invalid){
      SaveEquipmentType.save( $scope.equipment_type, onSuccess, onError );
    }

  };

  $scope.cancelCreateEquipmentType = function () {
    $location.path('');
  };
}