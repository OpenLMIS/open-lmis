function HeaderController($scope, UserContext, $rootScope, localStorageService, messageService) {

  UserContext.get({}, function (data) {
    if (!data.authenticated) {
      $rootScope.modalShown = true;
    }
    $scope.user = data.name;

  }, {});

  messageService.populate();

  $rootScope.fixToolBar = function () {

  };

  $scope.logout = function () {
    localStorageService.clearAll();
    window.location = "/j_spring_security_logout";
  };
}