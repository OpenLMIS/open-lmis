/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function SupervisoryNodeController($scope, supervisoryNode, ParentSupervisoryNodes) {
  $scope.supervisoryNode = supervisoryNode;

  var reset = function () {
    $scope.query = undefined;
    $scope.resultCount = undefined;
    $scope.filteredFacilities = undefined;
    $scope.previousQuery = undefined;
  };

  var compareQuery = function () {
    if (!isUndefined($scope.previousQuery)) {
      return $scope.query.substr(0, 3) !== $scope.previousQuery.substr(0, 3);
    }
    return true;
  };

  $scope.showParentNodeSearchResults = function () {
    if ($scope.query === undefined || $scope.query.length < 3) return;

    $scope.query = $scope.query.trim();
    if (compareQuery()) {
      ParentSupervisoryNodes.get({"searchParam": $scope.query}, function (data) {
        $scope.parentNodes = _.reject(data.supervisoryNodeList, function (node) {
          return $scope.supervisoryNode === undefined ? false : node.code == $scope.supervisoryNode.code;
        });
        $scope.filteredNodes = $scope.parentNodes;
        $scope.previousQuery = $scope.query;
        $scope.resultCount = $scope.filteredNodes.length;
      });
    }
    else {
      $scope.filteredNodes = _.filter($scope.parentNodes, function (node) {
        return node.name.toLowerCase().indexOf($scope.query.toLowerCase()) !== -1;
      });
      $scope.resultCount = $scope.filteredNodes.length;
    }
  };

  $scope.setSelectedParentNode = function (node) {
    $scope.parentNodeSelected = node;
    reset();
  };

  $scope.deleteParentNode = function () {
    $scope.parentNodeSelected = undefined;
    angular.element("#searchParentNode").focus();
  };
}

SupervisoryNodeController.resolve = {
  supervisoryNode: function ($q, $route, $timeout, SupervisoryNodes) {
    if ($route.current.params.id === undefined) return undefined;

    var deferred = $q.defer();
    var supervisoryNodeId = $route.current.params.id;

    $timeout(function () {
      SupervisoryNodes.get({id: supervisoryNodeId}, function (data) {
        deferred.resolve(data.supervisoryNode);
      }, {});
    }, 100);
    return deferred.promise;
  }
};