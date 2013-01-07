function HeaderController($scope, User, $rootScope, $http, localStorageService) {
  User.get({}, function (data) {
    if (!data.authenticated) {
      $rootScope.modalShown = true;
    }
    $scope.user = data.name;

  }, {});

  $scope.showUserMenu = function (e) {
    $(e.currentTarget).find("ul").show();
  };

  $rootScope.fixToolBar = function () {
    var toolbarWidth = window.innerWidth - 279;
    angular.element("#action_buttons").css("width", toolbarWidth + "px");
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
    localStorageService.clearAll();
    window.location = "/j_spring_security_logout";
  };
}