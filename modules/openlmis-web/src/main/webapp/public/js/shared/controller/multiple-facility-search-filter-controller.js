/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function MultipleFacilitySearchFilterController($scope, FacilityTypes, GeographicZoneSearch, Facilities, messageService) {

  $scope.showResults = false;
  $scope.type = {};
  $scope.zone = {};
  $scope.disableAddFacility = true;
  $scope.tempFacilities = [];

  $scope.label = !$scope.facilityType ? messageService.get("create.facility.select.facilityType") :
    messageService.get("label.change.facility.type");

  $scope.showFilterModal = function () {
    $scope.filterModal = true;
    FacilityTypes.get({}, function (data) {
      $scope.facilityTypes = data.facilityTypeList;
    }, {});
  };

  $scope.showFacilitySearchResults = function () {
    if (!$scope.facilitySearchParam) return;
    $scope.tempFacilities = [];
    $scope.$parent.$parent.duplicateFacilityName = undefined;
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
    $scope.disableAddFacility = true;
    $scope.tempFacilities = [];
  };

  $scope.searchGeoZone = function () {
    if (!$scope.geoZoneSearchParam) return;
    $scope.geoZoneQuery = $scope.geoZoneSearchParam.trim();

    GeographicZoneSearch.get({"searchParam": $scope.geoZoneQuery}, function (data) {
      $scope.geoZoneList = data.geoZones;
      $scope.geoZonesResultCount = isUndefined($scope.geoZoneList) ? 0 : $scope.geoZoneList.length;
      $scope.manyGeoZoneMessage = data.message;
      $scope.levels = _.uniq(_.pluck(_.pluck($scope.geoZoneList, 'level'), 'name'));
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
    $scope.label = $scope.facilityType ? messageService.get("label.change.facility.type") :
      messageService.get("create.facility.select.facilityType");
    $scope.facilityType = undefined;
  };

  $scope.setFilters = function () {
    $scope.zone = $scope.selectedGeoZone || {};
    $scope.type = $scope.selectedFacilityType || {};
    $scope.filterModal = false;
    $scope.showFacilitySearchResults();
  };

  $scope.clearGeoZoneSearch = function () {
    $scope.showResults = false;
    $scope.geoZoneList = [];
    $scope.geoZoneQuery = undefined;
    $scope.geoZoneSearchParam = undefined;
  };

  $scope.associate = function (facility) {
    facility.selected = !facility.selected;
    if (facility.selected) {
      $scope.tempFacilities.push(facility);
    }
    else {
      $scope.tempFacilities = _.filter($scope.tempFacilities, function (tempFacility) {
        return tempFacility.id != facility.id;
      });
    }
    if($scope.tempFacilities.length > 0){
      $scope.disableAddFacility = false;
    }
  };

  $scope.addMembers = function(){
    if($scope.$parent.$parent.addMembers($scope.tempFacilities)){
      $scope.clearFacilitySearch();
    }
  };

  $scope.cancelFilters = function () {
    $scope.selectedFacilityType = $scope.type;
    $scope.selectedGeoZone = $scope.zone;
    $scope.filterModal = false;
  };

  $scope.showLevel = function (index) {
    return !((index > 0 ) && ($scope.geoZoneList[index].level.name === $scope.geoZoneList[index - 1].level.name));
  };
}
