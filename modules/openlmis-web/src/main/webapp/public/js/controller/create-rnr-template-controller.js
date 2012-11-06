function CreateRnrTemplateController($scope, Program, RnRMasterColumnList) {
    Program.get({}, function (data) {   //success
        $scope.programs = data.programList;
    }, {});

    RnRMasterColumnList.get({}, function (data) {   //success
        $scope.rnrMasterColumnsList = data.rnrColumnList;
    }, {});
}
