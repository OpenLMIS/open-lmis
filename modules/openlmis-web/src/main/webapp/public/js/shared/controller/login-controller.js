function LoginController($scope, $http, localStorageService) {
  $scope.disableSignInButton = false;

  var validateLoginForm = function () {
    if($scope.username == undefined || $scope.username.trim()==''){
      $scope.loginError = "Please enter your username";
      return false;
    }
    if($scope.password == undefined) {
      $scope.loginError = "Please enter your password";
      return false;
    }
    return true;
  };

  $scope.doLogin = function () {
    if(!validateLoginForm()){
      return;
    }

    $scope.disableSignInButton = true;
    var data = $.param({j_username:$scope.username, j_password:$scope.password});
    $http({
      method:'POST',
      url:'/j_spring_security_check',
      data:data,
      headers:{'Content-Type':'application/x-www-form-urlencoded'}
    }).success(function (data) {
          $scope.disableSignInButton = false;
          if (data.authenticated) {
            localStorageService.add(localStorageKeys.RIGHT, getRights(data.rights));
            if (window.location.href.indexOf("login.html") != -1) {
              window.location = "/";
            } else {
              location.reload();
            }
          } else if (data.error) {
            $scope.loginError = data.error;
          }
        }).
        error(function (data) {
          $scope.disableSignInButton = false;
          $scope.loginError = data.error;
        });
  };

  $scope.goToForgotPassword = function () {
    window.location = "/public/pages/forgot-password.html";
  }

  function getRights(rightList) {
    var rights = [];
    if(!rightList) return rights;
    $.each(rightList, function (index, right) {
      rights.push(right.right);
    });
    return rights;
  }
}