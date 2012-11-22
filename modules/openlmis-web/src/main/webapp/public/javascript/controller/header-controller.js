function HeaderController($scope, User, $rootScope) {
  User.get({}, function (data) {
    if (data.authenticated == 'false') {
      $rootScope.modalShown = true;
    } else if (data.isAdmin == "false" && window.location.href.indexOf("/admin/") != -1) {
      window.location = "/";
    }
    $scope.user = data.name;

  }, {});
}