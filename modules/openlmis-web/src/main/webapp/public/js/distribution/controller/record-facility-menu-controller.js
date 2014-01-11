/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function RecordFacilityMenuController($scope, $location, $routeParams, distributionService) {
  $scope.distributionData = distributionService.distribution.facilityDistributions[$routeParams.facility];

  $scope.changeRoute = function (routeName) {
    $location.path($location.$$path.replace(getURLName(), routeName));
  };

  function getURLName() {
    var urlParts = $location.$$path.split('/');
    return urlParts[urlParts.length - 1];
  }

  $scope.isSelected = function (route) {
    var routeName = getURLName();
    if (routeName == route) {
      return 'selected';
    }
  };
}
