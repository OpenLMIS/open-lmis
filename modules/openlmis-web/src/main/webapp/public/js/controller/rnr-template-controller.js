function ConfigureRnRTemplateController($scope, Program, $location) {
    Program.get({}, function (data) {   //success
        $scope.programs = data.programList;
    }, {});

    $scope.createRnrTemplate = function () {
        if ($scope.$parent.program != undefined) {
            $scope.error = "";
            $location.path('create-rnr-template');
        }
        else {
            $scope.error = "Please select a program";
        }
    };
}

function SaveRnrTemplateController($scope, RnRColumnList, $http, $location) {
    var code = ($scope.program ? $scope.program.code : "");
    RnRColumnList.get({programCode:code}, function (data) {   //success
        $scope.rnrColumns = data.rnrTemplateForm.rnrColumns;
        $scope.sources = data.rnrTemplateForm.sources;
    }, function () {
        $location.path('select-program');
    });

    $scope.createProgramRnrTemplate = function () {

        $http.post('/admin/rnr/' + $scope.program.code + '/columns.json', $scope.rnrColumns).success(function () {
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
        $scope.rnrColumns.forEach(function (rnrColumn, index) {
            rnrColumn.position = index + 1;
        });
    };

    function updateErrorMessage(message) {
        $scope.error = message;
        $scope.message = "";
    }

}
