function CreateRnrTemplateController($scope, $http, $location) {
    $http.get('../../../../json/programs.json').success(function (data) {
        $scope.programs = data.programList;
    });
}
