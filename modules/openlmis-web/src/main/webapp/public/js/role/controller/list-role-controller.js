function ListRoleController($scope, Roles) {
  Roles.get({}, function(data) {
       $scope.roles = data.roles;
  }, {}
  );
}
