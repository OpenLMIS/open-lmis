function UserSearchController($scope,SearchUserByFirstOrLastName){

  SearchUserByFirstOrLastName.get({}, function (data) {
    $scope.userList = data.userList;
  }, {});

  $scope.filterUserByName = function (query) {
    var filteredUsers= [];
    query = query || "";

    angular.forEach($scope.userList, function (user) {
      if (user.firstname.toLowerCase().indexOf(query.toLowerCase()) >= 0 || user.lastname.toLowerCase().indexOf(query.toLowerCase()) >= 0) {
        filteredUsers.push(facility);
      }
    });
    $scope.resultCount = filteredUsers.length;
    return filteredUsers;
  };
}