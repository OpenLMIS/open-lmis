function NavigationController($scope, User, localStorageService, $rootScope) {


    $scope.loadRights = function () {
        var rights = localStorageService.get(localStorageKeys.RIGHT);

        if(rights == undefined){
            User.get({}, function (data) {
              if(data.authenticated) {
                  rights = getRights(data.rights);
                  localStorageService.add(localStorageKeys.RIGHT, rights);
              }
            }, {});

        }

        $scope.rights = rights;

        $(".navigation > ul").show();
    }();

    $scope.showSubmenu = function () {
        $(".navigation li:not(.navgroup)").on("click", function () {
            $(this).children("ul").show();
        });
    }();

    $rootScope.hasPermission = function (permission) {
        return (($scope.rights != undefined) &&  ($scope.rights.indexOf(permission) > -1));
    };

  function getRights(rightList) {
    var rights = [];
    $.each(rightList, function (index, right) {
      rights.push(right.right);
    });
    return rights;
  }

}
