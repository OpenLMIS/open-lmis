function LoginController($scope, $http,localStorageService) {
    $scope.doLogin = function () {
        var data = $.param({j_username:$scope.username, j_password:$scope.password});
        $http({
            method:'POST',
            url:'/j_spring_security_check',
            data:data,
            headers:{'Content-Type':'application/x-www-form-urlencoded'}
        }).success(function (data) {
            if (data.authenticated == "true") {
              localStorageService.add(localStorageKeys.RIGHT,getRights(data.rights));
          if (window.location.href.indexOf("login.html") != -1) {
            window.location = "/";
          } else {
            location.reload();
          }
        } else if (data.error == "true") {
          $scope.error = "The username or password you entered is incorrect. Please try again.";
        }
      }).
      error(function (data) {
        $scope.error = "Server Error!!";
      });
  };

  function getRights(rightList) {
    var rights = [];
    $.each(rightList, function (index, right) {
      rights.push(right.right);
    });
    return rights;
  }
}