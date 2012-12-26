'use strict';
angular.module('role', ['openlmis']).config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/create', {controller:SaveRoleController, templateUrl:'partials/create.html'}).
    when('/list', {controller:ListRoleController, templateUrl:'partials/list.html'}).
    when('/edit/:id', {controller:SaveRoleController, templateUrl:'partials/list.html'}).
    otherwise({redirectTo:'/list'});
}]);