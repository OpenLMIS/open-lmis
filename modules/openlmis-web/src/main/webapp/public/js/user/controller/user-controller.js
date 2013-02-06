function UserController($scope, $routeParams, Users, User, AllFacilities, Facility, Roles) {
  $scope.userNameInvalid = false;
  $scope.user = {};

  if ($routeParams.userId) {
    User.get({id:$routeParams.userId}, function (data) {
      $scope.user = data.user;
      loadRoleAssignments();
    }, {});
  }

  $scope.saveUser = function () {
    var successHandler = function (response) {
      $scope.user = response.user;
      $scope.showError = false;
      $scope.error = "";
      $scope.message = response.success;
    };

    var errorHandler = function (response) {
      $scope.showError = true;
      $scope.message = "";
      $scope.error = response.data.error;
    };

    var invalidRoleAssignment = function (user) {
      if (!user.roleAssignments) {
        return false;
      }

      var valid = true;
      $.each(user.roleAssignments, function (index, roleAssignment) {
        if (!roleAssignment.programId || !roleAssignment.roleIds || roleAssignment.roleIds.length == 0) {
          valid = false;
          return false;
        }
      });
      return !valid;
    };

    var requiredFieldsPresent = function (user) {
      if ($scope.userForm.$error.required || invalidRoleAssignment(user)) {
        $scope.error = "Please correct errors before saving.";
        $scope.message = "";
        $scope.showError = true;
        return false;
      } else {
        return true;
      }
    };

    if (!requiredFieldsPresent($scope.user))  return false;

    if ($scope.user.id) {
      User.update({id:$scope.user.id}, $scope.user, successHandler, errorHandler);
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
        AllFacilities.get({searchParam:query}, function (data) {
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
    loadRoleAssignments();
    $scope.query = null;
  };

  $scope.clearSelectedFacility = function () {
    $scope.facilitySelected = null;
    $scope.allSupportedPrograms = null;
    $scope.user.roleAssignments = null;
    $scope.user.facilityId = null;

    setTimeout(function () {
      angular.element("#searchFacility").focus();
    });
  };


  var loadRoleAssignments = function () {
    if (!isNullOrUndefined($scope.user.facilityId)) {
      if (isNullOrUndefined($scope.allSupportedPrograms)) {
        Facility.get({id:$scope.user.facilityId}, function (data) {
          $scope.allSupportedPrograms = data.facility.supportedPrograms;
          $scope.facilitySelected = data.facility;
        }, {});
      }

      if (isNullOrUndefined($scope.allRoles)) {
        Roles.get({}, function (data) {
          $scope.allRoles = data.roles;
        });
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

  var isNullOrUndefined = function (obj) {
    return obj == undefined || obj == null;
  }
}

