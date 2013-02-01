function ResetPasswordController($scope, $location, userId) {
  $scope.resetPassword = function () {

    var reWhiteSpace = new RegExp("/^\s+$/");
    var digits = new RegExp("/\d/");

    if($scope.password1.length < 8 ||$scope.password1.length > 16 || !digits.test($scope.password1) || reWhiteSpace.test($scope.password1)){
      $scope.error = "Password is invalid. Password must be between 8 to 16 characters, should not contain spaces and contain at least 1 number.";
      return;
    }

    if ($scope.password1 != $scope.password2) {
      $scope.error = "Passwords do not match";
      return;
    }

    $scope.user.password = $scope.password1;
    /*UpdateUserPassword.update({}, $scope.user, function(){
      $location();
    },{});*/
  }
}


ResetPasswordController.resolve = {

  userId:function ($q, $timeout, ValidatePasswordToken) {
    var deferred = $q.defer();
    $timeout(function () {
      ValidatePasswordToken.get({token :  $location.search('token') }, function(data){
       deferred.resolve(data.userId);
        //return true;
      }, function() {
        console.log("");
      });
    }, 100);
    return deferred.promise;
  }

};