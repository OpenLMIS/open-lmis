/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function FacilitySubFiltersController($scope, GeographicZoneSearch, FacilityTypes, messageService) {

  $scope.searchGeoZone = function () {
    if (!$scope.geoZoneSearchParam) return;
    $scope.geoZoneQuery = $scope.geoZoneSearchParam.trim();

    GeographicZoneSearch.get({"searchParam": $scope.geoZoneQuery}, function (data) {
      $scope.geoZoneList = data.geoZones;
      $scope.geoZonesResultCount = isUndefined($scope.geoZoneList) ? 0 : $scope.geoZoneList.length;
      $scope.manyGeoZoneMessage = data.message;
      $scope.levels = _.uniq(_.pluck(_.pluck($scope.geoZoneList, 'level'), 'name'));
      $scope.showResults = true;
    });
  };

  $scope.triggerSearch = function (event) {
    if (event.keyCode === 13) {
      $scope.searchGeoZone();
    }
  };

  $scope.label = !$scope.facilityType ? messageService.get("create.facility.select.facilityType") :
    messageService.get("label.change.facility.type");

  $scope.showFilterModal = function () {
    $scope.geoZoneSearchParam = undefined;
    $scope.filterModal = true;
    FacilityTypes.get({}, function (data) {
      $scope.facilityTypes = data.facilityTypeList;
    }, {});
  };

  $scope.setGeoZone = function (geoZone) {
    $scope.selectedGeoZone = geoZone;
    $scope.clearGeoZoneSearch();
  };

  $scope.setFacilityType = function () {
    $scope.selectedFacilityType = $scope.facilityType;
    $scope.label = $scope.facilityType ? messageService.get("label.change.facility.type") :
      messageService.get("create.facility.select.facilityType");
    $scope.facilityType = undefined;
  };

  $scope.setFilters = function () {
    $scope.$parent.$parent.zone = $scope.selectedGeoZone || {};
    $scope.$parent.$parent.type = $scope.selectedFacilityType || {};
    $scope.filterModal = false;
    $scope.$parent.$parent.showFacilitySearchResults();
  };

  $scope.clearGeoZoneSearch = function () {
    $scope.showResults = false;
    $scope.geoZoneList = [];
  $scope.geoZoneSearchParam = undefined;
    angular.element('#geoZoneSearchList').focus();
  };

  $scope.cancelFilters = function () {
    $scope.selectedFacilityType = $scope.$parent.$parent.type;
    $scope.selectedGeoZone = $scope.$parent.$parent.zone;
    $scope.filterModal = false;
  };

  $scope.showLevel = function (index) {
    return !((index > 0 ) && ($scope.geoZoneList[index].level.name === $scope.geoZoneList[index - 1].level.name));
  };

  function clearFilters() {
    $scope.selectedFacilityType = undefined;
    $scope.selectedGeoZone = undefined;
  }

  $scope.$on('singleSelectSearchCleared', clearFilters);
  $scope.$on('multiSelectSearchCleared', clearFilters);
}
