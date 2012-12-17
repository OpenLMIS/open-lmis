function NavigationController($scope, User, localStorageService) {


    $scope.loadRights = function () {

        var sessionStorageRights = localStorageService.get(localStorageKeys.RIGHT);

        if(sessionStorageRights ==undefined){
            User.get({}, function (data) {
                sessionStorageRights=data.rights;

                location.reload();
            }, {});
        }

        $scope.rights = sessionStorageRights;

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
