function ConfigureRnRTemplateController($scope, Program, $location) {
    Program.get({}, function (data) {   //success
        $scope.programs = data.programList;
    }, {});

    $scope.createRnrTemplate = function () {
        if ($scope.$parent.program != undefined) {
            $scope.error = "";
            $location.path('/create-rnr-template/'+$scope.$parent.program.code);
        }
        else {
            $scope.error = "Please select a program";
        }
    };
}

function SaveRnrTemplateController($scope, rnrTemplateForm, $http) {
    $scope.rnrColumns = rnrTemplateForm.rnrColumns;
    $scope.sources = rnrTemplateForm.sources;

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
SaveRnrTemplateController.resolve = {
    rnrTemplateForm: function ($q, RnRColumnList, $location, $route, $timeout){
        var deferred = $q.defer();
        var code = $route.current.params.programCode;

        $timeout(function () {
            RnRColumnList.get({programCode: code}, function (data) {   //success
                deferred.resolve(data.rnrTemplateForm);
            }, function () {
                $location.path('select-program');
            });
        }, 100);

        return deferred.promise;
    }

}


