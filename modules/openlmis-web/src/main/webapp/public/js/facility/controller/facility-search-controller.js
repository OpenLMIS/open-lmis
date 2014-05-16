/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function FacilitySearchController($scope, Facility, $location, navigateBackService) {

  $scope.$on('$viewContentLoaded', function () {
    $scope.query = navigateBackService.query;
    $scope.updateFilteredQueryList();
  });
  $scope.previousQuery = '';

  $scope.editFacility = function (id) {
    var data = {query: $scope.query};
    navigateBackService.setData(data);
    $location.path('edit/' + id);
  };

  $scope.clearSearch = function () {
    $scope.query = "";
    $scope.resultCount = 0;
    angular.element("#searchFacility").focus();
  };

  $scope.updateFilteredQueryList = function () {

    if (!$scope.query) return;

    $scope.query = $scope.query.trim();
    var queryLength = $scope.query.length;
    if (queryLength >= 3) {
      if (compareQuery()) {
        Facility.get({"searchParam": $scope.query.substring(0, 3)}, function (data) {
          $scope.filteredFacilities = data.facilityList;
          $scope.facilityList = $scope.filteredFacilities;
          $scope.previousQuery = $scope.query;
          if (queryLength > 3) {
            filterFacilities();
          }
          $scope.resultCount = $scope.facilityList.length;
        }, {});
      }
      else {
        filterFacilities();
        $scope.resultCount = $scope.facilityList.length;
      }
    }
  };

  var compareQuery = function () {
    if ($scope.previousQuery.substring(0, 3) != $scope.query.substring(0, 3)) {
      return true;
    }
  };

  var filterFacilities = function () {
    $scope.facilityList = [];
    angular.forEach($scope.filteredFacilities, function (facility) {
      var searchString = $scope.query.toLowerCase();
      if (facility.code.toLowerCase().indexOf(searchString) >= 0 || facility.name.toLowerCase().indexOf(searchString) >= 0) {
        $scope.facilityList.push(facility);
      }
    });
  };
}
