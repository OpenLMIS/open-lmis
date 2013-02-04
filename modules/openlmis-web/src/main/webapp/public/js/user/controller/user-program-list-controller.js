function UserProgramRoleListController($scope) {
  $scope.$parent.gridOptions = { data:'user.roleAssignments',
    headerRowHeight:30,
    rowHeight:50,
    sortable:false,
    showColumnMenu:false,
    displaySelectionCheckbox:false,
    showFilter:false,
    footerVisible:false,
    enableSorting:false,
    canSelectRows:false,
    columnDefs:[
      { field:'supportedPrograms', displayName:'Programs(s)', cellTemplate:program(), width:'30%' },
      { field:'roles', displayName:'Role', cellTemplate:selectRole(), width:'60%' },
      { field:'', displayName:'', cellTemplate:deleteRow(), width:'10%'}
    ]
  };

  function deleteRow() {
    return '<div><a value="delete" ng-click="deleteCurrentRow($parent.$index)">delete</a></div>';
  }

  function program() {
    return '<label ng-bind="getProgramName(user.roleAssignments[$parent.$index].programId)"></label>';
  }

  function selectRole() {
    return '<select ui-select2 ng-model="user.roleAssignments[$parent.$index].roleIds" placeholder="+ Add Role"' +
      ' multiple="multiple" style="width:400px" name="roles" id="roles"> ' +
      ' <option ng-repeat="role in allRoles" value="{{role.id}}">{{role.name}}</option>' +
      '</select>' +
      '<span ng-show="user.roleAssignments[$parent.$index].roleIds.length == 0" class="field-error">' +
      ' Please Fill In this value' +
      '</span>';
  }

  $scope.deleteCurrentRow = function (rowNum) {
    $scope.user.roleAssignments.splice(rowNum, 1);
  };

  $scope.availablePrograms = function () {
    return $scope.$parent.allSupportedPrograms;
  };

  $scope.showRoleAssignmentOptions = function () {
    return ($scope.user != null && $scope.user.facilityId != null)
  };

  $scope.addRole = function () {
    if (isPresent($scope.programSelected) && isPresent($scope.selectedRoleIds)) {
      var newRoleAssignment = {programId:$scope.programSelected, roleIds:$scope.selectedRoleIds};
      addRoleAssignment(newRoleAssignment);
      clearCurrentSelection();
    }

    function clearCurrentSelection() {
      $scope.programSelected = null;
      $scope.selectedRoleIds = null;
    }

    function addRoleAssignment(newRoleAssignment) {
      if (!$scope.user.roleAssignments) {
        $scope.user.roleAssignments = [];
      }
      $scope.user.roleAssignments.push(newRoleAssignment);
    }

    function isPresent(obj) {
      return obj != undefined && obj != null;
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
}