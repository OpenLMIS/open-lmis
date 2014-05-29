/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function facilitySearchFilterController($scope, FacilityTypes, GeographicZoneSearch) {

  $scope.showResults = false;
  $scope.selectedFacilityType = {};
  $scope.selectedGeoZone = {};

  FacilityTypes.get({}, function (data) {
    $scope.facilityTypes = data.facilityTypeList;
  }, {});

  $scope.showFacilitySearchResults = function () {
    if (!$scope.facilitySearchParam) return;

    $scope.facilityQuery = $scope.facilitySearchParam.trim();
    $scope.$parent.$parent.getSearchResults($scope.facilityQuery, $scope.selectedFacilityType.id, $scope.selectedGeoZone.id, function (data) {
      $scope.facilityList = data.facilityList;
      $scope.resultCount = isUndefined($scope.facilityList) ? 0 : $scope.facilityList.length;
      $scope.message = data.message;
    });
  };

  $scope.clearFacilitySearch = function () {
    angular.element("#search .search-list").slideUp("slow", function () {
      $scope.facilitySearchParam = undefined;
      $scope.facilityList = undefined;
      $scope.resultCount = undefined;
      $scope.$apply();
      angular.element('#searchFacility').focus();
    });
  };

  $scope.searchGeoZone = function () {
    if (!$scope.geoZoneSearchParam) return;
    $scope.geoZoneQuery = $scope.geoZoneSearchParam.trim();

    GeographicZoneSearch.get({"searchParam": $scope.geoZoneQuery}, function (data) {
      $scope.geoZoneList = data.geoZones;
      $scope.geoZonesResultCount = isUndefined($scope.geoZoneList) ? 0 : $scope.geoZoneList.length;
      $scope.manyGeoZoneMessage = data.message;
      $scope.showResults = true;
      angular.element("#filter .search-list").show();
    });
  };

  $scope.triggerSearch = function (event) {
    if (event.keyCode === 13) {
      $scope.searchGeoZone();
    }
  };

  $scope.setGeoZone = function (geoZone) {
    $scope.selectedGeoZone = geoZone;
    $scope.clearGeoZoneSearch();
  };

  $scope.setFacilityType = function () {
    $scope.selectedFacilityType = $scope.facilityType;
    $scope.facilityType = undefined;
  };

  $scope.setFilters = function () {
    $scope.zone = $scope.selectedGeoZone;
    $scope.type = $scope.selectedFacilityType;
    $scope.setFiltersModal = false;
    $scope.showFacilitySearchResults();
  };

  $scope.clearGeoZoneSearch = function () {
    $scope.showResults = false;
    $scope.geoZoneList = [];
    $scope.geoZoneQuery = undefined;
    $scope.geoZoneSearchParam = undefined;
  };

  $scope.associate = function (facility) {
    $scope.$parent.$parent.associate(facility);
    $scope.$parent.$parent.showSlider = !$scope.$parent.$parent.showSlider;
  };

  $scope.cancelFilters = function () {
    $scope.selectedFacilityType = undefined;
    $scope.selectedGeoZone = undefined;
    $scope.setFiltersModal = false;
  }
}
