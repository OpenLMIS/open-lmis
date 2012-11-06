function CreateRnrTemplateController($scope, Program, RnRMasterColumnList, $http) {
    Program.get({}, function (data) {   //success
        $scope.programs = data.programList;
    }, {});

    RnRMasterColumnList.get({}, function (data) {   //success
        $scope.rnrMasterColumnsList = data.rnrColumnList;
    }, {});

    $scope.createProgramRnrTemplate = function () {
        $http.post('/openlmis/admin/rnr/' + $scope.program + '/columns.json', $scope.rnrMasterColumnsList)
    }
}
