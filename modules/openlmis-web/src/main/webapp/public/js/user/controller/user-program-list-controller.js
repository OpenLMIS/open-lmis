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
      { field:'roles', displayName:'Role', cellTemplate:selectRole() },
      { field:'', displayName:'', cellTemplate:deleteRow()}
    ]
  };

  function deleteRow() {
    return '<div><a value="delete" ng-click="deleteCurrentRow($parent.$index)">delete</a></div>';
  }

  function program() {
      return '<label ng-bind="getProgramName(user.roleAssignments[$parent.$index].programId)"></label>';
    }

  function selectRole() {
    var select2Div = '<select ui-select2 ng-model="user.roleAssignments[$parent.$index].roleIds" placeholder="+ Add Role"  multiple="multiple"  style="width:400px">' +
      '<option ng-repeat="role in allRoles" value="{{role.id}}">{{role.name}}</option>' +
      '</select>';
    return select2Div;
  }

  $scope.deleteCurrentRow = function (rowNum) {
    $scope.user.roleAssignments[rowNum] = [];
    $scope.user.roleAssignments.length = $scope.user.roleAssignments.length - 1;
  };

  $scope.availablePrograms = function () {
    return $scope.$parent.allSupportedPrograms;
  };

  $scope.showRoleAssignmentOptions = function () {
    return ($scope.user != null && $scope.user.facilityId != null)
  };

  $scope.addRole = function () {
    if ($scope.programSelected && $scope.selectedRoleIds) {
      setRoleAssignments();
      clearCurrentSelection();
    }
  };

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

  function setRoleAssignments() {
    $scope.user.roleAssignments = $scope.user.roleAssignments.concat({programId:"", roleIds:[]});
    $scope.user.roleAssignments[$scope.user.roleAssignments.length - 1].programId = $scope.programSelected;
    $scope.user.roleAssignments[$scope.user.roleAssignments.length - 1].roleIds = $scope.selectedRoleIds;
  }

  function clearCurrentSelection() {
    $scope.programSelected = null;
    $scope.selectedRoleIds = null;
  }
}