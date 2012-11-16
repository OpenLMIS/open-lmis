function HeaderController($scope, User) {
    User.get({}, function (data) {
        $scope.user = data.user;
    }, {});
}