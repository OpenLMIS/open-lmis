'use strict';
angular.module('role', ['openlmis']).config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/create', {
      controller:RoleController,
      templateUrl:'partials/create.html',
      resolve:RoleController.resolve }).

    otherwise({redirectTo:'/create'});
}]);