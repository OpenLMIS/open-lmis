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

  $scope.type = {};
  $scope.zone = {};
  var defaultSearchParam='%';
  $scope.showCloseButton = false;

  $scope.showFacilitySearchResults = function () {
    if (!($scope.facilitySearchParam || defaultSearchParam)) return;

    $scope.facilityQuery = $scope.facilitySearchParam ? $scope.facilitySearchParam.trim() : defaultSearchParam;
    Facilities.get({
        "searchParam": $scope.facilityQuery,
        "facilityTypeId": $scope.type.id,
        "geoZoneId": $scope.zone.id,
        "virtualFacility": $scope.extraParams.virtualFacility,
        "enabled": $scope.extraParams.enabled},
      function (data) {
        $scope.facilityList = data.facilityList;
        $scope.facilityResultCount = isUndefined($scope.facilityList) ? 0 : $scope.facilityList.length;
        $scope.resultCount = $scope.facilityResultCount;
        $scope.message = data.message;
        if($scope.facilityQuery === '%'){
            $scope.showCloseButton = false;
        }else {
            $scope.showCloseButton = true;
        }
      });
  };

  $scope.triggerSearch = function (event) {
    if (event.keyCode === 13) {
      $scope.showFacilitySearchResults();
    }
  };

  $scope.clearFacilitySearch = function () {
    $scope.facilitySearchParam = undefined;
    $scope.facilityList = undefined;
    angular.element('#searchFacility').focus();
    $scope.showFacilitySearchResults();
  };

  $scope.clearVisibleFilters = function () {
    $scope.type = {};
    $scope.zone = {};
  };

  $scope.associate = function (facility) {
    $scope.$parent.associate(facility);
    $scope.clearFacilitySearch();
    $scope.clearVisibleFilters();
    $scope.$broadcast('singleSelectSearchCleared');
  };

  $scope.$watch('showSlider', function() {
      if($scope.showSlider){
        $scope.showFacilitySearchResults();
      }
  });
}
