function UserProgramRoleListController($scope) {
  $scope.$parent.gridOptions = { data:'programAndRoleList',
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

  function program() {
    var divElement = '<select id="programList" ng-model="user.roleAssignments[$parent.$index].programId"' +
        'ng-options="program.id as program.name for program in programAndRoleList[$parent.$index].supportedPrograms">' +
        ' <option value="">--Select Program-</option>' +
        '</select>';
    return divElement;
  }

  function selectRole() {
    var select2Div =
      '<select ui-select2 ng-model="user.roleAssignments[$parent.$index].roleIds" placeholder="+ Add Role"  multiple="multiple"  style="width:400px">'+
        '<option ng-repeat="role in programAndRoleList[$parent.$index].roles" value="{{role.id}}">{{role.name}}</option>'+
      '</select>';
    return select2Div;
  }
}