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


