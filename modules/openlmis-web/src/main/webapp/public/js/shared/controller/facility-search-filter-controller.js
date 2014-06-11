/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function FacilitySearchFilterController($scope, Facilities) {

  $scope.showResults = false;
  $scope.type = {};
  $scope.zone = {};

  $scope.showFacilitySearchResults = function () {
    if (!$scope.facilitySearchParam) return;

    $scope.facilityQuery = $scope.facilitySearchParam.trim();
    Facilities.get({"searchParam": $scope.facilityQuery, "facilityTypeId": $scope.type.id, "geoZoneId": $scope.zone.id}, function (data) {
      $scope.facilityList = data.facilityList;
      $scope.facilityResultCount = isUndefined($scope.facilityList) ? 0 : $scope.facilityList.length;
      $scope.message = data.message;
    });
  };

  $scope.clearFacilitySearch = function () {
    angular.element("#search .search-list").slideUp("slow", function () {
      $scope.facilitySearchParam = undefined;
      $scope.facilityList = undefined;
      $scope.facilityResultCount = undefined;
      $scope.$apply();
      angular.element('#searchFacility').focus();
    });
  };

  $scope.associate = function (facility) {
    $scope.$parent.$parent.associate(facility);
  };
}
