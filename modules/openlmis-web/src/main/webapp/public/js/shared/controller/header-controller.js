function HeaderController($scope, User, $rootScope) {
  User.get({}, function (data) {
    if (data.authenticated == 'false') {
      $rootScope.modalShown = true;
    }
//    else if (data.isAdmin == "false" && window.location.href.indexOf("/admin/") != -1) {
//      window.location = "/";
//    }
    $scope.user = data.name;

  }, {});

  $scope.showUserMenu = function(e) {
    $(e.currentTarget).find("ul").show();
  };

  $scope.fixHeight = function() {
    $(".navigation").height(window.innerHeight - 57);
  }();

  $scope.showSubmenu = function() {
    $(".navigation > ul > li").on("click", function() {
      $(this).find("ul").show();
    });
  }();
}