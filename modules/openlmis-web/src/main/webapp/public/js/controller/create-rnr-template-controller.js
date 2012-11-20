function CreateRnrTemplateController($scope, Program) {
    Program.get({}, function (data) {   //success
        $scope.programs = data.programList;
    }, {});
}

function SaveRnrTemplateController($scope, RnRColumnList, $http, $location) {
    var code = ($scope.program ? $scope.program.code : "");
    RnRColumnList.get({programCode:code}, function (data) {   //success
        $scope.rnrColumnsList = data.rnrColumnList;
    }, function () {
        $location.path('select-program');
    });

    $scope.createProgramRnrTemplate = function () {
        $http.post('/admin/rnr/' + $scope.program.code + '/columns.json', $scope.rnrColumnsList);
        $scope.message = "Template saved successfully.";
    }
}
