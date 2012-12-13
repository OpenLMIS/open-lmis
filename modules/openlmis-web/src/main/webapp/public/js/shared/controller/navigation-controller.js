function NavigationController($scope, UserRights) {

  UserRights.get({}, function (data) {
     $scope.rights = data.rightList;
        $(".navigation > ul").show();
  });


  $scope.showSubmenu = function() {
    $(".navigation > ul > li").on("click", function() {
      $(this).find("ul").show();
    });
  }();

  $scope.hasPermission = function(permission) {
   return $scope.rights.indexOf(permission)>-1;
  }

}
