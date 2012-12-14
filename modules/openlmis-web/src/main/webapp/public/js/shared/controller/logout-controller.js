function LogoutController($scope, $http) {
    $scope.logout = function () {
        sessionStorage.clear();
            $http({
                method:'GET',
                url:'/j_spring_security_logout',
                headers:{'Content-Type':'application/x-www-form-urlencoded'}
            })

        };
}