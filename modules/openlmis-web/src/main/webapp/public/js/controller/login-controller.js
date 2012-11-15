function LoginController($scope, $http) {
    $scope.doLogin = function (targetUrl) {
        var data = $.param({j_username:$scope.username, j_password:$scope.password});
        $http({
            method:'POST',
            url:'/j_spring_security_check',
            data:data,
            headers:{'Content-Type':'application/x-www-form-urlencoded'}
        }).success(function (data) {
                if (data.authenticated == "true") {
                    if (targetUrl)      {
                        window.location = targetUrl;
                    }
                    else {
                        location.reload();
                    }
                } else if(data.error == "true") {
                    $scope.error = "Invalid credentials!";
                }
            }).
            error(function (data) {
                alert("Error");
            });
    }
}