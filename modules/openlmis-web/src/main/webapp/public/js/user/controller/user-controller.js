/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function UserController($scope, $routeParams, $location, $dialog, Users, User, AllFacilities, Roles, Facility, Programs, SupervisoryNodes, messageService) {
  $scope.userNameInvalid = false;
  $scope.showHomeFacilityRoleMappingError = false;
  $scope.showSupervisorRoleMappingError = false;
  $scope.user = {};

  if ($routeParams.userId) {
    User.get({id: $routeParams.userId}, function (data) {
      $scope.user = data.user;
      loadUserFacility();
    }, {});
  }

  if (utils.isNullOrUndefined($scope.allRoles)) {
    Roles.get({}, function (data) {
      $scope.allRoles = data.roles;
      filterRoles();
    });
  }

  Programs.get({}, function (data) {
    $.each(data.programs, function (index, program) {
      program.status = program.active ? messageService.get("label.active") : messageService.get('label.inactive');
    });
    $scope.programs = data.programs;
  });

  SupervisoryNodes.get({}, function (data) {
    $scope.supervisoryNodes = data.supervisoryNodes;
  });

  function filterRoles() {
    $scope.adminRoles = _.filter($scope.allRoles, function (role) {
      return role.adminRole;
    });
    $scope.nonAdminRoles = _.filter($scope.allRoles, function (role) {
      return !role.adminRole;
    });
  }

  function validateHomeFacilityRoles(user) {
    if (!user.homeFacilityRoles) {
      return true;
    }
    var valid = true;
    $.each(user.homeFacilityRoles, function (index, roleAssignment) {
      if (!roleAssignment.programId || !roleAssignment.roleIds || roleAssignment.roleIds.length == 0) {
        valid = false;
        return false;
      }
    });
    return valid;
  }

  function validateSupervisorRoles(user) {
    if (!user.supervisorRoles) {
      return true;
    }

    var valid = true;
    $.each(user.supervisorRoles, function (index, roleAssignment) {
      if (!roleAssignment.programId || !roleAssignment.supervisoryNode || !roleAssignment.supervisoryNode.id || !roleAssignment.roleIds || roleAssignment.roleIds.length == 0) {
        valid = false;
        return false;
      }
    });
    return valid;
  }

  var validateRoleAssignment = function (user) {
    return validateHomeFacilityRoles(user) && validateSupervisorRoles(user);
  };

  $scope.saveUser = function () {
    var successHandler = function (response) {
      $scope.user = response.user;
      $scope.showError = false;
      $scope.error = "";
      $scope.$parent.message = response.success;
      $scope.$parent.userId = $scope.user.id;
      $location.path('');
    };

    var errorHandler = function (response) {
      $scope.showError = true;
      $scope.error = response.data.error;
    };

    var requiredFieldsPresent = function (user) {
      if ($scope.userForm.$error.required || !validateRoleAssignment(user)) {
        $scope.error = messageService.get("form.error");
        $scope.showError = true;
        return false;
      } else {
        return true;
      }
    };

    if (!requiredFieldsPresent($scope.user))  return false;

    if ($scope.user.id) {
      User.update({id: $scope.user.id}, $scope.user, successHandler, errorHandler);
    } else {
      Users.save({}, $scope.user, successHandler, errorHandler);
    }
    return true;
  };

  $scope.validateUserName = function () {
    $scope.userNameInvalid = $scope.user.userName != null && $scope.user.userName.trim().indexOf(' ') >= 0;
  };

  $scope.showFacilitySearchResults = function () {
    var query = $scope.query;
    var len = (query == undefined) ? 0 : query.length;

    if (len >= 3) {
      if (len == 3) {
        AllFacilities.get({searchParam: query}, function (data) {
          $scope.facilityList = data.facilityList;
          $scope.filteredFacilities = $scope.facilityList;
          $scope.resultCount = $scope.filteredFacilities.length;
        }, {});
      }
      else {
        filterFacilitiesByCodeOrName();
      }
    }
  };

  $scope.setSelectedFacility = function (facility) {
    $scope.user.facilityId = facility.id;
    $scope.facilitySelected = facility;
    loadUserFacility();
    $scope.query = null;
  };

  $scope.clearSelectedFacility = function (result) {
    if (!result) return;

    $scope.facilitySelected = null;
    $scope.allSupportedPrograms = null;
    $scope.user.homeFacilityRoles = null;
    $scope.user.facilityId = null;

    setTimeout(function () {
      angular.element("#searchFacility").focus();
    });
  };

  $scope.confirmFacilityDelete = function () {
    var dialogOpts = {
      id: "deleteFacilityModal",
      header: messageService.get('delete.facility.header'),
      body: messageService.get('confirm.programRole.deletion')
    };
    OpenLmisDialog.newDialog(dialogOpts, $scope.clearSelectedFacility, $dialog);
  };

  var loadUserFacility = function () {
    if (!utils.isNullOrUndefined($scope.user.facilityId)) {
      if (utils.isNullOrUndefined($scope.allSupportedPrograms)) {
        Facility.get({id: $scope.user.facilityId}, function (data) {
          $scope.allSupportedPrograms = data.facility.supportedPrograms;
          $scope.facilitySelected = data.facility;
        }, {});
      }
    }
  };

  var filterFacilitiesByCodeOrName = function () {
    $scope.filteredFacilities = [];

    angular.forEach($scope.facilityList, function (facility) {
      if (facility.code.toLowerCase().indexOf($scope.query.toLowerCase()) >= 0 || facility.name.toLowerCase().indexOf($scope.query.toLowerCase()) >= 0) {
        $scope.filteredFacilities.push(facility);
      }
      $scope.resultCount = $scope.filteredFacilities.length;
    })
  };


}

