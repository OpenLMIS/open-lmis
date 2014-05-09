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
    $scope.search();
  });

  $scope.searchOptions = [
    {value: "name", name: "option.value.geo.zone"},
    {value: "parentName", name: "option.value.geo.zone.parent"}
  ];

  $scope.selectedSearchOption = navigateBackService.selectedSearchOption || $scope.searchOptions[0];

  $scope.selectSearchType = function (searchOption) {
    $scope.selectedSearchOption = searchOption;
  };

  $scope.editGeoZone = function (id) {
    var data = {query: $scope.query, selectedSearchOption: $scope.selectedSearchOption};
    navigateBackService.setData(data);
    $location.path('edit/' + id);
  };

  $scope.clearSearch = function () {
    $scope.query = "";
    $scope.resultCount = 0;
    angular.element("#searchGeoZone").focus();
  };

  $scope.search = function () {
    if (!$scope.query) return;

    $scope.query = $scope.query.trim();
    GeographicZones.get({"searchParam": $scope.query, "columnName": $scope.selectedSearchOption.value}, function (data) {
      $scope.geoZoneList = data.geographicZoneList;
      $scope.resultCount = $scope.geoZoneList.length;
    }, {});
  }
}
