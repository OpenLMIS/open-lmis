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

function SaveRnrTemplateController($scope, rnrColumns, $http) {
    $scope.rnrColumns = rnrColumns;

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
    rnrColumns: function (RnRColumnList, $location, $route, $q, $timeout) {
        var deferred = $q.defer();
        var code = $route.current.params.programCode;
        //var rnrColumns;

        $timeout(function () {
            RnRColumnList.get({programCode: code}, function (data) {   //success
               // rnrColumns = data.rnrTemplateForm.rnrColumns;
                deferred.resolve(data.rnrTemplateForm.rnrColumns);
            }, function () {
                $location.path('select-program');
            });
        }, 100);

        return deferred.promise;
    }
}

