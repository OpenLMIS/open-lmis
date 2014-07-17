/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function MultipleFacilitySearchFilterController($scope, Facilities) {

  $scope.type = {};
  $scope.zone = {};
  $scope.disableAddFacility = true;
  $scope.tempFacilities = [];

  $scope.showFacilitySearchResults = function () {
    if (!$scope.multipleFacilitiesSearchParam) return;
    $scope.tempFacilities = [];
    $scope.$parent.$parent.duplicateFacilityName = undefined;
    $scope.disableAddFacility = true;
    $scope.multipleFacilitiesQuery = $scope.multipleFacilitiesSearchParam.trim();
    Facilities.get({
        "searchParam": $scope.multipleFacilitiesQuery,
        "facilityTypeId": $scope.type.id,
        "geoZoneId": $scope.zone.id,
        "virtualFacility": $scope.extraMultipleParams.virtualFacility,
        "enabled": $scope.extraMultipleParams.enabled},
      function (data) {
        $scope.multipleFacilities = data.facilityList;
        $scope.multipleFacilitiesResultCount = isUndefined($scope.multipleFacilities) ? 0 : $scope.multipleFacilities.length;
        $scope.multipleFacilitiesMessage = data.message;
        $scope.resultCount = $scope.multipleFacilitiesResultCount;
      });
  };

  $scope.triggerSearch = function (event) {
    if (event.keyCode === 13) {
      $scope.showFacilitySearchResults();
    }
  };

  $scope.clearMultiSelectFacilitySearch = function () {
    $scope.multipleFacilitiesSearchParam = undefined;
    $scope.multipleFacilities = undefined;
    $scope.multipleFacilitiesResultCount = undefined;
    $scope.disableAddFacility = true;
    $scope.tempFacilities = [];
    angular.element('#searchFacility').focus();
  };

  $scope.addToFacilityList = function (facility) {
    facility.selected = !facility.selected;
    if (facility.selected) {
      $scope.tempFacilities.push(facility);
    }
    else {
      $scope.tempFacilities = _.filter($scope.tempFacilities, function (tempFacility) {
        return tempFacility.id != facility.id;
      });
    }
    $scope.disableAddFacility = $scope.tempFacilities.length <= 0;
  };

  $scope.clearVisibleFilters = function () {
    $scope.type = {};
    $scope.zone = {};
  };

  $scope.addMembers = function () {
    if ($scope.$parent.$parent.addMembers($scope.tempFacilities)) {
      $scope.clearMultiSelectFacilitySearch();
      $scope.clearVisibleFilters();
      $scope.$broadcast('multiSelectSearchCleared');
    }
  };
}
