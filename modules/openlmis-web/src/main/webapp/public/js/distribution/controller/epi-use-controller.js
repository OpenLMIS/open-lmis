/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function EPIUseController($scope, $routeParams, distributionService) {
  $scope.distribution = distributionService.distribution;
  $scope.selectedFacilityId = $routeParams.facility;

  $scope.applyNRAll = function () {
    distributionService.applyNR(function () {
      $scope.distribution.facilityDistributions[$routeParams.facility].epiUse.setNotRecorded();
    });
  };
}

function EpiUseRowController($scope) {
  $scope.getTotal = function () {
    if (!$scope.lineItem) {
      return 0;
    }
    return getValue($scope.lineItem.stockAtFirstOfMonth) + getValue($scope.lineItem.received);
  };

  var getValue = function (object) {
    return (!isUndefined(object) && !isUndefined(object.value)) ? parseInt(object.value, 10) : 0;
  };
}
