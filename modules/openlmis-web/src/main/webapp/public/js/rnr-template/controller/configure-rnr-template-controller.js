function ConfigureRnRTemplateController($scope, programs, $location) {

  $scope.programs = programs;

  $scope.createRnrTemplate = function () {
    if ($scope.$parent.program != undefined) {
      $scope.error = "";
      $location.path('/create-rnr-template/' + $scope.$parent.program.id);
    } else {
      $scope.error = "Please select a program";
    }
  };
}

ConfigureRnRTemplateController.resolve = {
  programs:function ($q, Program, $location, $route, $timeout) {
    var deferred = $q.defer();

    $timeout(function () {
      Program.get({}, function (data) { //success
        deferred.resolve(data.programList);
      }, function () {
        location.path('/select-program');
      });
    }, 100);

    return deferred.promise;
  }
};



