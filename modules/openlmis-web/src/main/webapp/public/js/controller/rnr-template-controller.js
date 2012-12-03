function CreateRnrTemplateController($scope, Program) {
    Program.get({}, function (data) {   //success
        $scope.programs = data.programList;
    }, {});
}

function SaveRnrTemplateController($scope, RnRColumnList, $http, $location) {

    var code = ($scope.program ? $scope.program.code : "");
    RnRColumnList.get({programCode:code}, function (data) {   //success
        $scope.rnrColumnList = data.rnrColumnList;
    }, function () {
        $location.path('select-program');
    });

    $scope.createProgramRnrTemplate = function () {
        $http.post('/admin/rnr/' + $scope.program.code + '/columns.json', $scope.rnrColumnList).success(function () {
            $scope.message = "Template saved successfully!";
            $scope.error = "";
            $scope.errorMap = undefined;
        }).error(function (data) {
                if (data.errorMap != null) {
                    $scope.errorMap = data.errorMap;
                }
                updateErrorMessage("Save Failed!");
            });
    };

    $scope.update = function () {
        $scope.rnrColumnList.forEach(function(rnrColumn, index) {
            rnrColumn.position = index + 1;
        });
    };

    function updateErrorMessage(message) {
        $scope.error = message;
        $scope.message = "";
    }

}
