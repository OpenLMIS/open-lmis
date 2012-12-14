function HeaderController($scope, User, $rootScope, $http) {
    User.get({}, function (data) {
        if (data.authenticated == 'false') {
            $rootScope.modalShown = true;
        }
        $scope.user = data.name;

    }, {});

    $scope.showUserMenu = function (e) {
        $(e.currentTarget).find("ul").show();
    };

    $scope.fixHeight = function () {
        $(".navigation").height(window.innerHeight - 57);
    }();

    $scope.showSubmenu = function () {
        $(".navigation > ul > li").on("click", function () {
            $(this).find("ul").show();
        });
    }();

    $scope.logout = function () {
            sessionStorage.clear();
                $http({
                    method:'GET',
                    url:'/j_spring_security_logout',
                    headers:{'Content-Type':'application/x-www-form-urlencoded'}
                })

    };
}