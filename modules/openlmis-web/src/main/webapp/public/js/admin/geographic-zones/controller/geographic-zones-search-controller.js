/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

function GeographicZonesSearchController($scope, $location, navigateBackService, GeographicZoneCompleteList) {
    $scope.$on('$viewContentLoaded', function () {
        $scope.$apply($scope.query = navigateBackService.query);
        $scope.showGeographicZonesSearchResults('searchGeographicZone');
    });
    $scope.previousQuery = '';

    $scope.showGeographicZonesSearchResults = function (id) {

        GeographicZoneCompleteList.get(function (data) {
            $scope.filteredGeographicZones = data.geographicZones;
            $scope.geographicZonesList = $scope.filteredGeographicZones;
        });

        var query = document.getElementById(id).value;
        $scope.query = query;

        var len = (query === undefined || query === null) ? 0 : query.length;

        filterGeographicZonesByName(query);
        return true;
    };

    $scope.editGeographicZones = function (id) {
        var data = {query: $scope.query};
        navigateBackService.setData(data);
        $location.path('edit/' + id);
    };


    $scope.clearSearch = function () {
        $scope.query = "";
        $scope.resultCount = 0;
        angular.element("#searchGeographicZone").focus();
    };

    var filterGeographicZonesByName = function (query) {
        query = query || "";

        if (query.length === 0) {
            $scope.filteredGeographicZones = $scope.geographicZonesList;
        }
        else {
            $scope.filteredGeographicZones = [];
            angular.forEach($scope.geographicZonesList, function (geographicZone) {

                if (geographicZone.name.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0) {
                    $scope.filteredGeographicZones.push(geographicZone);
                }
            });
            $scope.resultCount = $scope.filteredGeographicZones.length;
        }
    };

    $scope.filterGeographicZones = function (id) {
        var query = document.getElementById(id).value;
        $scope.query = query;
        filterGeographicZonesByName(query);
    };
}