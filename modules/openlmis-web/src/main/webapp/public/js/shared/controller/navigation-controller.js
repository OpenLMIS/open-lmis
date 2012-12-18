function NavigationController($scope, User, localStorageService) {


    $scope.loadRights = function () {

        var rights = localStorageService.get(localStorageKeys.RIGHT);

        if(rights ==undefined){
            User.get({}, function (data) {
                rights=data.rights;
                localStorageService.add(localStorageKeys.RIGHT,rights);

            }, {});

        }

        $scope.rights = rights;

        $(".navigation > ul").show();
    }();

    $scope.showSubmenu = function () {
        $(".navigation > ul > li").on("click", function () {
            $(this).find("ul").show();
        });
    }();

    $scope.hasPermission = function (permission) {
        if ($scope.rights == undefined || $scope.rights.indexOf(permission) == undefined)   return false;

        return $scope.rights.indexOf(permission) > -1;
    }

}
