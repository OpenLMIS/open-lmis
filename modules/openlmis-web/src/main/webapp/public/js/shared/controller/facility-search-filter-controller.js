/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function FacilitySearchFilterController($scope, Facility){

  $scope.facilitySearchResults = function(){
    if (!$scope.query) return;
    $scope.query = $scope.query.trim();
    Facility.get({"searchParam": $scope.query}, function (data) {
      $scope.facilityList = data.facilityList;
      $scope.resultCount = $scope.facilityList.length;
    }, {});
  };

  $scope.clearSearch = function(){
    $scope.query = undefined;
    $scope.facilityList = undefined;
    $scope.resultCount = undefined;
    angular.element('#searchFacility').focus();
  };

  $scope.associateFacility = function(facility) {
    $scope.$parent.$parent.supervisoryNode.facility = facility;
    $scope.$parent.$parent.toggleSlider();
  };
}

