function UserController($scope, $routeParams, Users, UserById, SearchFacilitiesByCodeOrName, Facility, Roles) {

  if ($routeParams.userId) {
    var id = $routeParams.userId;
    UserById.get({id:id}, function (data) {
      $scope.user = data.user;
      //$scope.facilitySelected = {name:"Village-Reach"};
      //$scope.programAndRoleList = {supportedPrograms:programs, roles:roles }
     // $scope.programToRoleMappingList = getProgramToRoleMappingList($scope.user.programToRoleMappingList);
    });
  } else {
    $scope.user = {};
    $scope.programAndRoleList = [];
    $scope.programToRoleMappingList = [
      {program:{}, roles:[]}
    ];
  }

  $scope.saveUser = function () {

    $scope.user.programToRoleMappingList = $scope.programToRoleMappingList;

    Users.save({}, $scope.user, function (data) {
      $scope.user = data.user;
      $scope.showError = false;
      $scope.error = "";
      $scope.message = data.success;
    }, function (data) {
      $scope.showError = true;
      $scope.message = "";
      $scope.error = data.data.error;
    });
  };

  $scope.validateUserName = function () {
    if ($scope.user.userName != null && $scope.user.userName.trim().indexOf(' ') >= 0) {
      return true;
    }
    return false;
  };

  $scope.showFacilitySearchResults = function () {
    var query = $scope.query;
    var len = (query == undefined) ? 0 : query.length;

    if (len >= 3) {
      if (len == 3) {
        SearchFacilitiesByCodeOrName.get({searchParam:query}, function (data) {
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
    $scope.query = null;
  };

  $scope.clearSelectedFacility = function () {
    $scope.facilitySelected = null;
    $scope.programAndRoleList = [];

    setTimeout(function() {
      angular.element("#searchFacility").focus();
    });
    
  };

  $scope.displayProgramRoleMapping = function () {

    if ($scope.facilitySelected != null) {
      var programRoleGridRow = { supportedPrograms:[],
        roles:[]};

      $scope.programToRoleMappingList = $scope.programToRoleMappingList.concat({program:{}, roles:[]});

      Facility.get({id:$scope.user.facilityId}, function (data) {
        programRoleGridRow.supportedPrograms = data.facility.supportedPrograms;
      });

      Roles.get({}, function (data) {
        programRoleGridRow.roles = data.roles;
      });

      $scope.programAndRoleList = $scope.programAndRoleList.concat(programRoleGridRow);
    }
  }

  var filterFacilitiesByCodeOrName = function () {
    $scope.filteredFacilities = [];

    angular.forEach($scope.facilityList, function (facility) {
      if (facility.code.toLowerCase().indexOf($scope.query.toLowerCase()) >= 0 || facility.name.toLowerCase().indexOf($scope.query.toLowerCase()) >= 0) {
        $scope.filteredFacilities.push(facility);
      }
      $scope.resultCount = $scope.filteredFacilities.length;
    })
  };

  var getProgramToRoleMappingList = function (userProgramToRoleMappingList) {
    var programToRoleMappingList = [];

    $.each(userProgramToRoleMappingList, function (index, programToRoleMapping) {
      $.each($scope.programAndRoleList, function (index, programToRole) {
        $.each(programToRole.supportedPrograms, function (index, program) {
          if (programToRoleMapping.program.id == program.id) {
            programToRoleMappingList.push(programToRole);
          }
        });
      });
    });

    return programToRoleMappingList;
  };
}


