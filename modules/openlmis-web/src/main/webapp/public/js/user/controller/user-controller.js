function UserController($scope, $routeParams, Users, UserById, SearchFacilitiesByCodeOrName, Facility, Roles) {

  if ($routeParams.userId) {
    var id = $routeParams.userId;
    UserById.get({id:id}, function (data) {
      $scope.user = data.user;
    });
  } else {
    $scope.user = {};
    $scope.programAndRoleList = [];
    $scope.assignedProgramRolesMapped=[{assignedProgram:"",rolesMapped:[]}];
  }

  $scope.saveUser = function () {

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
    $scope.facilitySelected = facility;
    $scope.query = null;
  };

  $scope.clearSelectedFacility = function () {
  };

  $scope.displayProgramRoleMapping = function () {

    if ($scope.facilitySelected != null) {
      var programRoleGridRow = { supportedPrograms:[],
        roles:[]};

      Facility.get({id:$scope.facilitySelected.id}, function (data) {
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
      if (facility.code.indexOf($scope.query) >= 0 || facility.name.indexOf($scope.query) >= 0) {
        $scope.filteredFacilities.push(facility);
      }
      $scope.resultCount = $scope.filteredFacilities.length;
    })
  };
}