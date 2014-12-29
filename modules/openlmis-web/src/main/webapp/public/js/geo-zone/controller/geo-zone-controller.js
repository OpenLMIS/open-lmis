/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function GeoZoneController($scope, geoLevels, geoZone, GeographicZonesAboveLevel, $location, GeographicZones) {
  $scope.levels = geoLevels;
  $scope.geoZone = geoZone;
  $scope.$parent.message = "";

  $scope.loadParents = function (levelCode) {
    GeographicZonesAboveLevel.get({geoLevelCode: levelCode}, function (data) {
      $scope.parentGeoZones = data.geographicZoneList;
      $scope.parentLevels = _.uniq(_.pluck(_.pluck($scope.parentGeoZones, 'level'), 'name'));
    }, {});
  };

  if ($scope.geoZone) {
    $scope.loadParents($scope.geoZone.level.code);
    $scope.editMode = true;
  }

  $scope.cancel = function () {
    $scope.$parent.geoZoneId = undefined;
    $scope.$parent.message = "";
    $location.path('#/search');
  };

  var success = function (data) {
    $scope.error = "";
    $scope.$parent.message = data.success;
    $scope.$parent.geoZoneId = data.geoZone.id;
    $scope.showError = false;
    $location.path('');
  };

  var error = function (data) {
    $scope.$parent.message = "";
    $scope.error = data.data.error;
    $scope.showError = true;
  };

  $scope.save = function () {
    if ($scope.geoZoneForm.$error.pattern || $scope.geoZoneForm.$error.required || !$scope.geoZone.level.code) {
      $scope.showError = true;
      $scope.error = 'form.error';
      $scope.message = "";
      return;
    }

    if (!$scope.parentLevels || $scope.parentLevels.length === 0) {
      $scope.geoZone.parent = undefined;
    }
    if ($scope.geoZone.id) {
      GeographicZones.update({id: $scope.geoZone.id}, $scope.geoZone, success, error);
    }
    else {
      GeographicZones.save({}, $scope.geoZone, success, error);
    }
  };
}

GeoZoneController.resolve = {
  geoLevels: function ($q, $timeout, GeoLevels) {
    var deferred = $q.defer();
    $timeout(function () {
      GeoLevels.get({}, function (data) {
        deferred.resolve(data.geographicLevelList);
      }, {});
    }, 100);
    return deferred.promise;
  },

  geoZone: function ($q, $route, $timeout, GeographicZones) {
    if ($route.current.params.id === undefined) return undefined;

    var deferred = $q.defer();
    var geoZoneId = $route.current.params.id;

    $timeout(function () {
      GeographicZones.get({id: geoZoneId}, function (data) {
        deferred.resolve(data.geoZone);
      }, {});
    }, 100);
    return deferred.promise;
  }
};
