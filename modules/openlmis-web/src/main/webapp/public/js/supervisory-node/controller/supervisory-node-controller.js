/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function SupervisoryNodeController($scope, supervisoryNode, SupervisoryNodes) {
  $scope.supervisoryNode = supervisoryNode;
  if ($scope.query.length < 3) return;
  $scope.showParentNodeSearchResults = function () {
    SupervisoryNodes.get({"searchParam": $scope.query}, function (data) {
      $scope.filteredNodes = _.reject(data.supervisoryNodes, function (node) {
        return node.code == $scope.supervisoryNode.code;
      });
    });
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