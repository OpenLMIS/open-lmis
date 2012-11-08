function CreateRnrTemplateController($scope, Program) {
    Program.get({}, function (data) {   //success
        $scope.programs = data.programList;
    }, {});
}

function SaveRnrTemplateController($scope, RnRColumnList, $http) {
    RnRColumnList.get({programId:$scope.program.id}, function (data) {   //success
        $scope.rnrColumnsList = data.rnrColumnList;
    }, {});

    $scope.createProgramRnrTemplate = function () {
        $http.post('/admin/rnr/' + $scope.program.id + '/columns.json', $scope.rnrColumnsList)
    }
}
