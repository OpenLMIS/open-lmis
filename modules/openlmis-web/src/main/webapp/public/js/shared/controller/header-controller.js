function HeaderController($scope, User, $rootScope, localStorageService) {
  User.get({}, function (data) {
    if (!data.authenticated) {
      $rootScope.modalShown = true;
    }
    $scope.user = data.name;

  }, {});

  $rootScope.fixToolBar = function () {
    var toolbarWidth = window.innerWidth - 26;
    angular.element("#action_buttons").css("width", toolbarWidth + "px");
  };

  $scope.logout = function () {
    localStorageService.clearAll();
    window.location = "/j_spring_security_logout";
  };
}