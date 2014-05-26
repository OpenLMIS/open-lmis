/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function RequisitionGroupController($scope, requisitionGroupData, $location, RequisitionGroups, SupervisoryNodesSearch, Facilities) {

  if (requisitionGroupData) {
    $scope.requisitionGroup = requisitionGroupData.requisitionGroup;
    $scope.requisitionGroupMembers = requisitionGroupData.requisitionGroupMembers;
  }
  else {
    $scope.requisitionGroup = {};
    $scope.requisitionGroupMembers = [];
  }

  $scope.cancel = function () {
    $scope.$parent.message = "";
    $scope.$parent.requisitionGroupId = undefined;
    $location.path('#/search');
  };

  loadSupervisoryNode();

  $scope.save = function () {
    if ($scope.requisitionGroupForm.$error.required || !$scope.requisitionGroup.supervisoryNode) {
      $scope.showError = "true";
      $scope.error = 'form.error';
      $scope.message = "";
      return;
    }
    if ($scope.requisitionGroup.id) {
      RequisitionGroups.update({id: $scope.requisitionGroup.id}, {"requisitionGroup": $scope.requisitionGroup, "requisitionGroupMembers": $scope.requisitionGroupMembers},
          success, error);
    }
    else {
      RequisitionGroups.save({}, {"requisitionGroup": $scope.requisitionGroup, "requisitionGroupMembers": $scope.requisitionGroupMembers},
          success, error);
    }
  };

  $scope.getSearchResults = function (query, successCallBack) {
    Facilities.get({"searchParam": query}, successCallBack, {});
  };

  $scope.associate = function (facility) {
    var isDuplicate = _.find($scope.requisitionGroupMembers, function (member) {
      return member.facility.id == facility.id;
    });
    if (isDuplicate) {
      $scope.duplicateFacilityName = facility.name;
      return;
    }
    $scope.duplicateFacilityName = undefined;

    var newMember = {"facility": facility, "requisitionGroup": $scope.requisitionGroup};
    $scope.requisitionGroupMembers.push(newMember);

    $scope.requisitionGroupMembers.sort(function (member1, member2) {
      if (member1.facility.code.toLowerCase() > member2.facility.code.toLowerCase())
        return 1;
      if (member1.facility.code.toLowerCase() < member2.facility.code.toLowerCase())
        return -1;
      return 0;
    });
  };

  $scope.removeMember = function (memberFacilityId) {
    $scope.requisitionGroupMembers = _.filter($scope.requisitionGroupMembers, function (member) {
      return member.facility.id != memberFacilityId;
    });
  };

  var success = function (data) {
    $scope.error = "";
    $scope.$parent.message = data.success;
    $scope.$parent.requisitionGroupId = data.requisitionGroupId;
    $scope.showError = false;
    $location.path('#/search');
  };

  var error = function (data) {
    $scope.$parent.message = "";
    $scope.error = data.data.error;
    $scope.showError = true;
  };

  var compareQuery = function () {
    if (!isUndefined($scope.previousQuery)) {
      return $scope.query.substr(0, 3) !== $scope.previousQuery.substr(0, 3);
    }
    return true;
  };

  $scope.showSupervisoryNodeSearchResults = function () {
    if ($scope.query === undefined || $scope.query.length < 3) return;

    if (compareQuery()) {
      SupervisoryNodesSearch.get({searchParam: $scope.query}, function (data) {
        $scope.supervisoryNodes = data.supervisoryNodeList;
        $scope.filteredNodeList = $scope.supervisoryNodes;
        $scope.previousQuery = $scope.query;
        $scope.resultCount = $scope.filteredNodeList.length;
      }, {});
    }
    else {
      $scope.filteredNodeList = _.filter($scope.supervisoryNodes, function (node) {
        return node.name.toLowerCase().indexOf($scope.query.toLowerCase()) !== -1;
      });
      $scope.resultCount = $scope.filteredNodeList.length;
    }
  };

  $scope.setSelectedSupervisoryNode = function (node) {
    $scope.requisitionGroup.supervisoryNode = node;
    $scope.nodeSelected = node;
    loadSupervisoryNode();
    $scope.query = undefined;
  };

  $scope.clearSelectedNode = function () {
    $scope.nodeSelected = undefined;
    $scope.requisitionGroup.supervisoryNode = undefined;
  };

  function loadSupervisoryNode() {
    if (!$scope.requisitionGroup) return;

    $scope.nodeSelected = $scope.requisitionGroup.supervisoryNode;
  }
}

RequisitionGroupController.resolve = {
  requisitionGroupData: function ($q, $route, $timeout, RequisitionGroups) {
    if ($route.current.params.id === undefined) return undefined;

    var deferred = $q.defer();
    var requisitionGroupId = $route.current.params.id;

    $timeout(function () {
      RequisitionGroups.get({id: requisitionGroupId}, function (data) {
        deferred.resolve(data.requisitionGroupData);
      }, {});
    }, 100);
    return deferred.promise;
  }
};
