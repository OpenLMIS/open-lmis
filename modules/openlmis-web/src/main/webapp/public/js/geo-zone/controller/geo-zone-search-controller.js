/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function GeoZoneSearchController($scope, GeographicZones, $location, navigateBackService) {

  $scope.$on('$viewContentLoaded', function () {
    $scope.query = navigateBackService.query;
    $scope.updateFilteredQueryList();
  });

  $scope.searchOptions = [
    {value: "name", name: "option.value.geo.zone"},
    {value: "parentName", name: "option.value.geo.zone.parent"}
  ];

  $scope.previousQuery = '';
  $scope.selectedSearchOption = navigateBackService.selectedSearchOption || $scope.searchOptions[0];

  $scope.selectSearchType = function (searchOption) {
    $scope.selectedSearchOption = searchOption;
    $scope.updateFilteredQueryList();
  };

  $scope.editGeoZone = function (id) {
    var data = {query: $scope.query, selectedSearchOption: $scope.selectedSearchOption};
    navigateBackService.setData(data);
    $location.path('edit/' + id);
  };

  $scope.filterGeoZonesByNameOrCode = function (query) {
    var filteredGeoZones = [];
    query = query || "";

    angular.forEach($scope.geoZoneList, function (geoZone) {
      if (geoZone.name.toLowerCase().indexOf(query.toLowerCase()) >= 0 || geoZone.code.toLowerCase().indexOf(query.toLowerCase()) >= 0) {
        filteredGeoZones.push(geoZone);
      }
    });
    $scope.resultCount = filteredGeoZones.length;
    return filteredGeoZones;
  };

  $scope.clearSearch = function () {
    $scope.query = "";
    $scope.resultCount = 0;
    angular.element("#searchGeoZone").focus();
  };


  $scope.updateFilteredQueryList = function () {

    if (!$scope.query) return;

    $scope.query = $scope.query.trim();
    var queryLength = $scope.query.length;
    if (queryLength >= 3) {
      GeographicZones.get({"searchParam": $scope.query.substring(0, 3), "columnName": $scope.selectedSearchOption.value}, function (data) {
        $scope.filteredGeoZones = data.geographicZoneList;
        $scope.geoZoneList = $scope.filteredGeoZones;
        $scope.previousQuery = $scope.query;
        if (queryLength > 3) {
          filterGeoZones();
        }
        $scope.resultCount = $scope.geoZoneList.length;
      }, {});
    }
  };

  var filterGeoZones = function () {
    $scope.geoZoneList = [];
    angular.forEach($scope.filteredGeoZones, function (geoZone) {
      var searchString = $scope.query.toLowerCase();
      if ($scope.selectedSearchOption === $scope.searchOptions[0] && geoZone.name.toLowerCase().indexOf(searchString) >= 0) {
        $scope.geoZoneList.push(geoZone);
      }
      if ($scope.selectedSearchOption === $scope.searchOptions[1] && geoZone.parent.name.toLowerCase().indexOf(searchString) >= 0) {
        $scope.geoZoneList.push(geoZone);
      }
    });
  };
}
