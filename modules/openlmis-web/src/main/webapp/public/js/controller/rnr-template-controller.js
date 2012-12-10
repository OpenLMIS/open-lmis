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

function SaveRnrTemplateController($scope, rnrColumns, sources, $http) {
    $scope.rnrColumns = rnrColumns;
    $scope.sources = sources;

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
//TODO: need to find a better way to resolve common dependency
SaveRnrTemplateController.resolve = {
    rnrColumns: function ($q, RnRColumnList, $location, $route, $timeout){
        var deferred = $q.defer();
        var code = $route.current.params.programCode;

        $timeout(function () {
            RnRColumnList.get({programCode: code}, function (data) {   //success
                deferred.resolve(data.rnrTemplateForm.rnrColumns);
                deferred.resolve(data.rnrTemplateForm.rnrColumns);
            }, function () {
                $location.path('select-program');
            });
        }, 100);

        return deferred.promise;
    },
    sources: function ($q, RnRColumnList, $location, $route, $timeout){
        var deferred = $q.defer();
        var code = $route.current.params.programCode;

        $timeout(function () {
            RnRColumnList.get({programCode: code}, function (data) {   //success
                deferred.resolve(data.rnrTemplateForm.sources);
            }, function () {
                $location.path('select-program');
            });
        }, 100);

        return deferred.promise;
    }



}


