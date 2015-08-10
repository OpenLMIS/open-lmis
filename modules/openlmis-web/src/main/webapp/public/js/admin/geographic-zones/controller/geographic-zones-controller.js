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

function GeographicZonesController($scope, $routeParams, $location, GeographicZoneCompleteList, GeographicLevels, GetGeographicZone, SetGeographicZone) {
    $scope.geographicZoneNameInvalid = false;
    $scope.geographicZone = {};
    $scope.geographicZoneLevelInvalid = false;
    $scope.selectedParent = {};


    if ($routeParams.geographicZoneId) {
        GetGeographicZone.get({id: $routeParams.geographicZoneId}, function (data) {
            $scope.geographicZone = data.geographicZone;

            GetGeographicZone.get({id:$scope.geographicZone.parent.id},function(data){
                $scope.selectedParent = data.geographicZone;
            },{});

        }, {});
    }

    GeographicLevels.get(function (data) {
        $scope.geographicLevels = data.geographicLevels;
    });

    GeographicZoneCompleteList.get(function (data) {
        $scope.geographicZones = data.geographicZones;
        $scope.geographicZones.push("");
    });

    $scope.saveGeographicZone = function () {

        if($scope.geographicZoneLevelInvalid){
            $scope.showError = true;
            return false;
        }

        var successHandler = function (response) {
            $scope.geographicZone = response.geographicZone;
            $scope.showError = false;
            $scope.error = "";
            $scope.$parent.message = response.success;
            $scope.$parent.geographicZoneId = $scope.geographicZone.id;
            $location.path('');
        };

        var errorHandler = function (response) {
            $scope.showError = true;
            $scope.error = response.data.error;
        };

        if(!$scope.geographicZone.parent.id){
            $scope.geographicZone.parent = null;
        }

        SetGeographicZone.save($scope.geographicZone,successHandler,errorHandler);

        return true;
    };

    $scope.validateGeographicZoneName = function () {
        $scope.geographicZoneNameInvalid = $scope.geographicZone.name === null || typeof $scope.geographicZone.name === "undefined";
    };

    $scope.validateGeographicZoneLevelParent = function(){
        angular.forEach($scope.geographicZones, function(geoZoneParent) {
            if(geoZoneParent.id == $scope.geographicZone.parent.id){
                $scope.selectedParent = geoZoneParent;
            }
        });
        $scope.geographicZoneLevelInvalid = $scope.geographicZone.level.id == $scope.selectedParent.level.id;
    };

    $scope.validateGeographicZoneLevel = function(){
        $scope.geographicZoneLevelInvalid = $scope.geographicZone.level.id == $scope.selectedParent.level.id;
    };


}

