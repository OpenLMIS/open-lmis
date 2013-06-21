function ConfigureRegimenTemplateController($scope, programs, $location) {
  $scope.programs = programs;

  $scope.configure = function(programId) {
    $location.path('/create-regimen-template/' + programId);
  };

}

ConfigureRegimenTemplateController.resolve = {
  programs:function ($q, Programs, $location, $route, $timeout) {
    var deferred = $q.defer();

    $timeout(function () {
      Programs.get({}, function (data) { //success
        deferred.resolve(data.programs);
      }, function () {
        location.path('/select-program');
      });
    }, 100);

    return deferred.promise;
  }
};