function CreateRnrTemplateController($scope, Program) {
    Program.get({}, function (data) {   //success
        $scope.programs = data.programList;
    }, {});
}

function SaveRnrTemplateController($scope, RnRColumnList, $http) {
    RnRColumnList.get({programId:$scope.program}, function (data) {   //success
        $scope.rnrColumnsList = data.rnrColumnList;
    }, {});

    $scope.createProgramRnrTemplate = function () {
        $http.post('/openlmis/admin/rnr/' + $scope.program + '/columns.json', $scope.rnrColumnsList)
    }
}
