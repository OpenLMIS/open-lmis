/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function SupplyLineController($scope, $location, supplyLine, TopLevelSupervisoryNodes, SupplyLines, programs) {
  $scope.supplyLine = supplyLine || {};
  $scope.programs = programs;
  $scope.$parent.message = "";

  var reset = function () {
    $scope.query = undefined;
    $scope.nodeResultCount = undefined;
    $scope.filteredFacilities = undefined;
    $scope.previousQuery = undefined;
  };

  var compareQuery = function () {
    if (!isUndefined($scope.previousQuery)) {
      return $scope.query.substr(0, 3) !== $scope.previousQuery.substr(0, 3);
    }
    return true;
  };

  $scope.toggleSlider = function () {
    $scope.showSlider = !$scope.showSlider;
    $scope.extraParams = {"virtualFacility": null, "enabled": true};
  };

  $scope.associate = function (facility) {
    $scope.supplyLine.supplyingFacility = facility;
    $scope.showSlider = !$scope.showSlider;
    reset();
  };

  $scope.showTopLevelNodeResults = function () {
    if ($scope.query === undefined || $scope.query.length < 3) return;

    if (compareQuery()) {
      TopLevelSupervisoryNodes.get({"searchParam": $scope.query}, function (data) {
        $scope.topLevelNodes = data.supervisoryNodeList;
        $scope.filteredNodes = $scope.topLevelNodes;
        $scope.previousQuery = $scope.query;
        $scope.nodeResultCount = $scope.filteredNodes.length;
      });
    }
    else {
      $scope.filteredNodes = _.filter($scope.topLevelNodes, function (node) {
        return node.name.toLowerCase().indexOf($scope.query.toLowerCase()) !== -1;
      });
      $scope.nodeResultCount = $scope.filteredNodes.length;
    }
  };

  $scope.setSupervisoryNode = function (node) {
    $scope.supplyLine.supervisoryNode = node;
    reset();
  };

  $scope.deleteSupervisoryNode = function () {
    $scope.supplyLine.supervisoryNode = undefined;
    angular.element("#searchParentNode").focus();
  };

  $scope.cancel = function () {
    $scope.$parent.message = "";
    $scope.$parent.supplyLineId = undefined;
    $location.path('#/search');
  };

  var success = function (data) {
    $scope.error = "";
    $scope.$parent.message = data.success;
    $scope.$parent.supplyLineId = data.supplyLineId;
    $scope.showError = false;
    $location.path('');
  };

  var error = function (data) {
    $scope.$parent.message = "";
    $scope.error = data.data.error;
    $scope.showError = true;
  };

  $scope.save = function () {
    if ($scope.supplyLineForm.$error.required || !$scope.supplyLine.supplyingFacility || !$scope.supplyLine.supervisoryNode) {
      $scope.showError = true;
      $scope.error = "form.error";
      return;
    }
    if ($scope.supplyLine.id) {
      SupplyLines.update({id: $scope.supplyLine.id}, $scope.supplyLine, success, error);
    }
    else {
      SupplyLines.save({}, $scope.supplyLine, success, error);
    }
  };
}

SupplyLineController.resolve = {
  programs: function ($q, $timeout, Programs) {
    var deferred = $q.defer();

    $timeout(function () {
      Programs.get({type: "pull"}, function (data) {
        deferred.resolve(data.programs);
      }, {});
    }, 100);
    return deferred.promise;
  },

  supplyLine: function ($q, $route, $timeout, SupplyLines) {
    if ($route.current.params.id === undefined) return undefined;

    var deferred = $q.defer();
    var supplyLineId = $route.current.params.id;

    $timeout(function () {
      SupplyLines.get({id: supplyLineId}, function (data) {
        deferred.resolve(data.supplyLine);
      }, {});
    }, 100);
    return deferred.promise;
  }
};