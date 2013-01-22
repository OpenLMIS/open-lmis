function UserController($scope, $routeParams, Users, UserById) {

  if ($routeParams.userId) {
    var id = $routeParams.userId;
    UserById.get({id:id}, function (data) {
      $scope.user = data.user;
    });
  } else {
    $scope.user = {};
  }

  $scope.saveUser = function () {

    Users.save({}, $scope.user, function (data) {
      $scope.user = data.user;
      $scope.showError = false;
      $scope.error = "";
      $scope.message = data.success;
    }, function (data) {
      $scope.showError = true;
      $scope.message = "";
      $scope.error = data.data.error;
    });
  };

  $scope.validateUserName = function() {
    if($scope.user.userName!=null && $scope.user.userName.trim().indexOf(' ')>=0){
      return true;
    }
    return false;
  }

};