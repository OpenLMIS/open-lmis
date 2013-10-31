/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
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

