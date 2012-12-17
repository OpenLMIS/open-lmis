function NavigationController($scope, User) {


    $scope.loadRights = function () {

        if(sessionStorage['rights'] ==undefined){
            User.get({}, function (data) {
                sessionStorage['rights'] = data.rights;
                location.reload();
            }, {});
        }

        $scope.rights = sessionStorage['rights'];

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
