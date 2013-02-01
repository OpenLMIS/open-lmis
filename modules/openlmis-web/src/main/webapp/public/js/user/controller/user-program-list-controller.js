function UserProgramRoleListController($scope) {
  $scope.$parent.gridOptions = { data:'user.roleAssignments',
    headerRowHeight:50,
    rowHeight:50,
    sortable:false,
    showColumnMenu:false,
    displaySelectionCheckbox:false,
    showFilter:false,
    footerVisible:false,
    enableSorting:false,
    canSelectRows:false,
    columnDefs:[
      { field:'supportedPrograms', displayName:'Programs(s)', cellTemplate:program() },
      { field:'roles', displayName:'Role', cellTemplate:selectRole() }
    ]
  };

  $scope.availablePrograms = function () {
    return $scope.$parent.allSupportedPrograms;
  };

  $scope.showRoleAssignmentOptions = function () {
    return $scope.user.facilityId != null;
  };

//  $scope.addRole = function () {
//        var user = $scope.user;
//  };

  $scope.getProgramName = function (programId) {
    if (!$scope.$parent.allSupportedPrograms) return;
    var programName = null;
    $.each($scope.$parent.allSupportedPrograms, function (index, supportedProgram) {
      if (supportedProgram.id == programId) {
        programName = supportedProgram.name;
        return false;
      }
    });
    return programName;
  };


  function program() {
    return '<label ng-bind="getProgramName(user.roleAssignments[$parent.$index].programId)"></label>';
  }

  function selectRole() {
    var select2Div = '<select ui-select2 ng-model="user.roleAssignments[$parent.$index].roleIds" placeholder="+ Add Role"  multiple="multiple"  style="width:400px">' +
        '<option ng-repeat="role in allRoles" value="{{role.id}}">{{role.name}}</option>' +
        '</select>';
    return select2Div;
  }
}