function UserProgramRoleListController($scope) {
  $scope.$parent.gridOptions = { data:'programAndRoleList',
    headerRowHeight:50,
    rowHeight:100,
    sortable:false,
    showColumnMenu:false,
    displaySelectionCheckbox:false,
    showFilter:false,
    footerVisible:false,
    enableSorting:false,
    canSelectRows:false,
    columnDefs:[
      { field:'supportedPrograms', displayName:'Programs(s)', cellTemplate:dropDownProgramCellTemplate() },
      { field:'roles', displayName:'Role', cellTemplate:multipleSelectCellTemplate() }
    ]
  };

  function dropDownProgramCellTemplate() {
    var divElement = '<select id="programList" ng-model="user.roleAssignments[$parent.$index].programId"' +
        'ng-options="program.id as program.name for program in programAndRoleList[$parent.$index].supportedPrograms">' +
        ' <option value="">--Select Program-</option>' +
        '</select>';
    return divElement;
  }

  function multipleSelectCellTemplate() {
    var divElement = '<select id="roleList" ng-model="user.roleAssignments[$parent.$index].roleIds"' +
        'ng-options="role.id as role.name for role in programAndRoleList[$parent.$index].roles" multiple="multiple"> ' +
        '<option value="">--Select Role--</option>' +
        '</select>';
    return divElement;
  }
}