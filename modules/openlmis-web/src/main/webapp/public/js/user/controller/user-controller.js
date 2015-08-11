       /*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function UserController($scope, $location, $dialog, Users, Facility, messageService, user, roles_map, programs, supervisoryNodes, deliveryZones, enabledWarehouses, $timeout, UpdatePassword) {
  $scope.userNameInvalid = false;
  $scope.showHomeFacilityRoleMappingError = false;
  $scope.showSupervisorRoleMappingError = false;
  $scope.user = user || {homeFacilityRoles: [], restrictLogin: false};
  $scope.supervisoryNodes = supervisoryNodes;
  $scope.warehouses = enabledWarehouses;
  $scope.deliveryZones = deliveryZones;
  $scope.$parent.userId = null;
  $scope.message = "";
  $scope.$parent.message = "";

  var originalUser = $.extend(true, {}, user);

  loadUserFacility();
  preparePrograms(programs);

  $scope.rolesMap = roles_map;

  function preparePrograms(programs) {
    if (programs) {
      $.each(programs, function (index, program) {
        program.status = program.active ? messageService.get("label.active") : messageService.get('label.inactive');
      });
    }
    $scope.programsMap = _.groupBy(programs, function (program) {
      return program.push ? 'push' : 'pull';
    });
    $scope.programsMap.all = programs;
  }

  function validateHomeFacilityRoles(user) {
    if (!user.homeFacilityRoles) {
      return true;
    }
    var valid = true;
    $.each(user.homeFacilityRoles, function (index, roleAssignment) {
      if (!roleAssignment.programId || !roleAssignment.roleIds || roleAssignment.roleIds.length === 0) {
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
      if (!roleAssignment.programId || !roleAssignment.supervisoryNode || !roleAssignment.supervisoryNode.id || !roleAssignment.roleIds || roleAssignment.roleIds.length === 0) {
        valid = false;
        return false;
      }
    });
    return valid;
  }

  function validateShipmentRoles(user) {
    if (!user.fulfillmentRoles) {
      return true;
    }

    var valid = true;
    $.each(user.fulfillmentRoles, function (index, roleAssignment) {
      if (!roleAssignment.facilityId || !roleAssignment.roleIds || roleAssignment.roleIds.length === 0) {
        valid = false;
        return false;
      }
    });
    return valid;
  }

  var validateRoleAssignment = function (user) {
    return validateHomeFacilityRoles(user) && validateSupervisorRoles(user) && validateShipmentRoles(user);
  };

  $scope.saveUser = function () {
    var successHandler = function (msgKey) {
      $scope.showError = false;
      $scope.error = "";
      $scope.$parent.message = messageService.get(msgKey, $scope.user.firstName, $scope.user.lastName);
      $scope.$parent.userId = $scope.user.id;
      $location.path('');
    };

    var saveSuccessHandler = function (response) {
      $scope.user = response.user;
      successHandler(response.success);
    };

    var updateSuccessHandler = function () {
      successHandler("message.user.updated.success");
    };

    var errorHandler = function (response) {
      $scope.showError = true;
      $scope.message = "";
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
    if ($scope.user.email === "") {
      $scope.user.email = null;
    }
    if ($scope.user.id) {
      Users.update({id: $scope.user.id}, $scope.user, updateSuccessHandler, errorHandler);
    } else {
      Users.save({}, $scope.user, saveSuccessHandler, errorHandler);
    }
    return true;
  };

  $scope.validateUserName = function () {
    $scope.userNameInvalid = $scope.user.userName !== null && $scope.user.userName.trim().indexOf(' ') >= 0;
  };

  $scope.toggleSlider = function () {
    if (!$scope.facilitySelected) {
      $scope.showSlider = !$scope.showSlider;
      $scope.extraParams = {"virtualFacility": false, "enabled": null };
    }
  };

  $scope.associate = function (facility) {
    $scope.user.facilityId = facility.id;
    $scope.facilitySelected = facility;
    loadUserFacility();
    $scope.query = null;
    $scope.showSlider = !$scope.showSlider;
  };

  $scope.clearSelectedFacility = function (result) {
    if (!result) return;

    $scope.facilitySelected = null;
    $scope.allSupportedPrograms = null;
    $scope.user.homeFacilityRoles = null;
    $scope.user.facilityId = null;

    $timeout(function () {
      angular.element("#searchFacility").focus();
    });
  };

  $scope.confirmFacilityDelete = function () {
    var dialogOpts = {
      id: "deleteFacilityModal",
      header: 'delete.facility.header',
      body: 'confirm.programRole.deletion'
    };
    OpenLmisDialog.newDialog(dialogOpts, $scope.clearSelectedFacility, $dialog);
  };

  function loadUserFacility() {
    if (!$scope.user) return;

    if (!utils.isNullOrUndefined($scope.user.facilityId)) {
      if (utils.isNullOrUndefined($scope.allSupportedPrograms)) {
        Facility.get({id: $scope.user.facilityId}, function (data) {
          $scope.allSupportedPrograms = _.filter(data.facility.supportedPrograms, function (supportedProgram) {
            return !supportedProgram.program.push;
          });

          $scope.facilitySelected = data.facility;
        }, {});
      }
    }
  }

  $scope.cancel = function () {
    $scope.$parent.message = "";
    $scope.$parent.userId = undefined;
    $location.path('#/search');
  };

  var clearErrorAndSetMessage = function (msgKey) {
    $scope.showError = "false";
    $scope.error = "";
    $scope.message = messageService.get(msgKey, $scope.user.firstName, $scope.user.lastName);
  };

  var errorFunc = function (data) {
    $scope.showError = "true";
    $scope.message = "";
    $scope.error = data.error;
  };

  $scope.showConfirmUserDisableModal = function () {
    var dialogOpts = {
      id: "disableUserDialog",
      header: 'disable.user.header',
      body: messageService.get('disable.user.confirm', $scope.user.firstName, $scope.user.lastName)
    };
    OpenLmisDialog.newDialog(dialogOpts, $scope.disableUserCallback, $dialog);
  };

  $scope.disableUserCallback = function (result) {
    if (!result) return;
    Users.disable({id: $scope.user.id}, disableSuccessFunc, errorFunc);
  };

  var disableSuccessFunc = function (data) {
    clearErrorAndSetMessage(data.success);
    $scope.user = originalUser;
    $scope.user.active = false;
  };

  $scope.showConfirmUserRestoreModal = function () {
    var dialogOpts = {
      id: "restoreUserDialog",
      header: 'enable.user.header',
      body: messageService.get('enable.user.confirm', $scope.user.firstName, $scope.user.lastName)
    };
    OpenLmisDialog.newDialog(dialogOpts, $scope.restoreUserCallback, $dialog);
  };

  $scope.restoreUserCallback = function (result) {
    if (!result) return;
    $scope.user.active = true;
    Users.update({id: $scope.user.id}, $scope.user, restoreSuccessFunc, errorFunc);
  };

  $scope.getMessage = function (key) {
    return messageService.get(key);
  };

  $scope.changePassword = function (user) {
    if (user.active) {
      $scope.user.password1 = $scope.user.password2 = $scope.message = $scope.passwordError = "";
      $scope.changePasswordModal = true;
    }
    else {
      $scope.message = "";
      $scope.error = messageService.get("user.is.disabled");
    }
  };

  $scope.updatePassword = function () {
    var reWhiteSpace = new RegExp("\\s");
    var digits = new RegExp("\\d");
    if ($scope.user.password1.length < 8 || $scope.user.password1.length > 16 || !digits.test($scope.user.password1) ||
      reWhiteSpace.test($scope.user.password1)) {
      $scope.passwordError = messageService.get("error.password.invalid");
      return;
    }

    if ($scope.user.password1 != $scope.user.password2) {
      $scope.passwordError = messageService.get('error.password.mismatch');
      return;
    }

    UpdatePassword.update({userId: $scope.user.id}, $scope.user.password1, function (data) {
      $scope.message = data.success;
      $scope.error = "";
      $scope.resetPasswordModal();
    }, {});
  };

  $scope.resetPasswordModal = function () {
    $scope.changePasswordModal = false;
  };

  var restoreSuccessFunc = function (data) {
    clearErrorAndSetMessage("msg.user.restore.success");
    $('.form-group').find(':input').removeAttr('disabled');
  };

  $timeout(function () {
    accordion.expandCollapseToggle($('.accordion-section:first .heading'));
  });
}

UserController.resolve = {

  user: function ($q, Users, $route, $timeout) {
    var userId = $route.current.params.userId;
    if (!userId) return undefined;
    var deferred = $q.defer();
    $timeout(function () {
      Users.get({id: userId}, function (data) {
        deferred.resolve(data.user);
      }, function () {
      });
    }, 100);
    return deferred.promise;
  },

  roles_map: function ($q, Roles, $timeout) {
    var deferred = $q.defer();

    $timeout(function () {
      Roles.get({}, function (data) {
        deferred.resolve(data.roles_map);
      }, function () {
      });
    }, 100);

    return deferred.promise;
  },

  programs: function ($q, Program, $timeout) {
    var deferred = $q.defer();

    $timeout(function () {
      Program.get({}, function (data) {
        deferred.resolve(data.programs);
      }, function () {
      });
    }, 100);

    return deferred.promise;
  },

  supervisoryNodes: function ($q, SupervisoryNodes, $timeout) {
    var deferred = $q.defer();

    $timeout(function () {
      SupervisoryNodes.get({}, function (data) {
        deferred.resolve(data.supervisoryNodes);
      }, function () {
      });
    }, 100);

    return deferred.promise;
  },

  deliveryZones: function ($q, DeliveryZone, $timeout) {
    var deferred = $q.defer();

    $timeout(function () {
      DeliveryZone.get({}, function (data) {
        deferred.resolve(data.deliveryZones);
      }, function () {
      });
    }, 100);

    return deferred.promise;
  },

  enabledWarehouses: function ($q, EnabledWarehouse, $timeout) {
    var deferred = $q.defer();

    $timeout(function () {
      EnabledWarehouse.get({}, function (data) {
        deferred.resolve(data.enabledWarehouses);
      }, function () {
      });
    }, 100);

    return deferred.promise;
  }
};
