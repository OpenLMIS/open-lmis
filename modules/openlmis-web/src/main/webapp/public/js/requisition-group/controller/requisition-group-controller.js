/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function RequisitionGroupController($scope, requisitionGroupData, $location, RequisitionGroups, SupervisoryNodesSearch, programs, schedules) {

  var currentProgramSchedule, programScheduleUnderEdit;
  if (requisitionGroupData) {
    $scope.requisitionGroup = requisitionGroupData.requisitionGroup;
    $scope.requisitionGroupMembers = requisitionGroupData.requisitionGroupMembers;
    $scope.requisitionGroupProgramSchedules = requisitionGroupData.requisitionGroupProgramSchedules;
    _.each($scope.requisitionGroupProgramSchedules, function (requisitionGroupProgramSchedule) {
      requisitionGroupProgramSchedule.underEdit = false;
    });
  }
  else {
    $scope.requisitionGroup = {};
    $scope.requisitionGroupMembers = [];
    $scope.requisitionGroupProgramSchedules = [];
  }

  refreshAndSortPrograms();
  angular.element('.facility-add-success').fadeOut("fast");

  function refreshAndSortPrograms() {
    var selectedProgramIds = _.pluck(_.pluck($scope.requisitionGroupProgramSchedules, 'program'), 'id');
    $scope.programs = _.reject(programs, function (program) {
      return _.contains(selectedProgramIds, program.id);
    });

    $scope.programs = _.sortBy($scope.programs, function (program) {
      return program.name.toLowerCase();
    });
  }

  $scope.addNewRow = function () {
    $scope.addNew = true;
  };

  $scope.updateSchedule = function (index) {
    $scope.requisitionGroupProgramSchedules[index].processingSchedule = _.find($scope.schedules, function (schedule) {
      return $scope.requisitionGroupProgramSchedules[index].processingSchedule.id == schedule.id;
    });
  };

  $scope.edit = function (index) {
    currentProgramSchedule = angular.copy($scope.requisitionGroupProgramSchedules[index]);
    $scope.requisitionGroupProgramSchedules[index].underEdit = true;
  };

  $scope.clearDropOffFacility = function (index) {
    $scope.requisitionGroupProgramSchedules[index].dropOffFacility = undefined;
  };

  $scope.cancelEdit = function (index) {
    $scope.requisitionGroupProgramSchedules[index] = currentProgramSchedule;
    $scope.requisitionGroupProgramSchedules[index].underEdit = false;
    currentProgramSchedule = undefined;
  };

  $scope.cancelAdd = function () {
    $scope.newProgramSchedule = undefined;
    $scope.addNew = false;
  };

  $scope.findProgramScheduleUnderEdit = function () {
    return _.find($scope.requisitionGroupProgramSchedules, function (programSchedule) {
      return programSchedule.underEdit === true;
    });
  };

  $scope.saveEditableRow = function (index) {
    $scope.requisitionGroupProgramSchedules[index].underEdit = false;
    $scope.requisitionGroupProgramSchedules[index].dropOffFacility = programScheduleUnderEdit.dropOffFacility || $scope.requisitionGroupProgramSchedules[index].dropOffFacility;
  };

  $scope.programMessage = $scope.programs.length ? "label.select.program" : "label.noProgramLeft";
  $scope.schedules = schedules;
  $scope.newProgramSchedule = {};

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
      RequisitionGroups.update({id: $scope.requisitionGroup.id},
        {"requisitionGroup": $scope.requisitionGroup, "requisitionGroupMembers": $scope.requisitionGroupMembers, "requisitionGroupProgramSchedules": $scope.requisitionGroupProgramSchedules},
        success, error);
    }
    else {
      RequisitionGroups.save({},
        {"requisitionGroup": $scope.requisitionGroup, "requisitionGroupMembers": $scope.requisitionGroupMembers, "requisitionGroupProgramSchedules": $scope.requisitionGroupProgramSchedules},
        success, error);
    }
  };

  $scope.toggleSlider = function () {
    $scope.showSlider = !$scope.showSlider;
  };

  $scope.associate = function (facility) {
    programScheduleUnderEdit = $scope.findProgramScheduleUnderEdit();
    if (programScheduleUnderEdit) {
      programScheduleUnderEdit.dropOffFacility = facility;
    }
    else {
      $scope.newProgramSchedule.dropOffFacility = facility;
    }
    $scope.toggleSlider();
  };

  $scope.addMembers = function (tempFacilities) {
    var duplicateMembers = _.intersection(_.pluck(_.pluck($scope.requisitionGroupMembers, 'facility'), 'id'),
      _.pluck(tempFacilities, "id"));
    if (duplicateMembers.length > 0) {
      $scope.duplicateFacilityName = _.find($scope.requisitionGroupMembers, function (member) {
        return member.facility.id == duplicateMembers[0];
      }).facility.name;
      $scope.facilitiesAddedSuccesfully = false;
      return false;
    }

    $.each(tempFacilities, function (index, tempFacility) {
      var newMember = {"facility": tempFacility, "requisitionGroup": $scope.requisitionGroup};
      $scope.requisitionGroupMembers.push(newMember);
    });

    $scope.requisitionGroupMembers = _.sortBy($scope.requisitionGroupMembers, function (member) {
      return member.facility.code.toLowerCase();
    });

    $scope.showMultipleFacilitiesSlider = !$scope.showMultipleFacilitiesSlider;
    angular.element('.facility-add-success').fadeIn("slow");
    $scope.duplicateFacilityName = undefined;
    angular.element('.facility-add-success').delay(3000).fadeOut("slow");
    return true;
  };

  $scope.addProgramSchedules = function () {
    $scope.requisitionGroupProgramSchedules.push($scope.newProgramSchedule);
    refreshAndSortPrograms();
    $scope.newProgramSchedule = undefined;
    $scope.addNew = false;
  };

  $scope.$watch('showMultipleFacilitiesSlider', function () {
    $scope.duplicateFacilityName = undefined;
  });

  $scope.removeMember = function (memberFacilityId) {
    $scope.requisitionGroupMembers = _.filter($scope.requisitionGroupMembers, function (member) {
      return member.facility.id != memberFacilityId;
    });
  };

  $scope.remove = function (programId) {
    $scope.requisitionGroupProgramSchedules = _.reject($scope.requisitionGroupProgramSchedules, function (programSchedule) {
      return programSchedule.program.id === programId;
    });
    refreshAndSortPrograms();
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
        $scope.nodeResultCount = $scope.filteredNodeList.length;
      }, {});
    }
    else {
      $scope.filteredNodeList = _.filter($scope.supervisoryNodes, function (node) {
        return node.name.toLowerCase().indexOf($scope.query.toLowerCase()) !== -1;
      });
      $scope.nodeResultCount = $scope.filteredNodeList.length;
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
    if ($scope.requisitionGroup) {
      $scope.nodeSelected = $scope.requisitionGroup.supervisoryNode;
    }
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
  },

  programs: function ($q, $timeout, Programs) {
    var deferred = $q.defer();

    $timeout(function () {
      Programs.get({type: "pull"}, function (data) {
        deferred.resolve(data.programs);
      }, {});
    }, 100);
    return deferred.promise;
  },

  schedules: function ($q, $timeout, Schedule) {
    var deferred = $q.defer();

    $timeout(function () {
      Schedule.get({}, function (data) {
        deferred.resolve(data.schedules);
      }, {});
    }, 100);
    return deferred.promise;
  }
};