'use strict';
angular.module('role', ['openlmis']).config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/create', {controller:SaveRoleController, templateUrl:'partials/create.html'}).
    when('/list', {controller:ListRoleController, templateUrl:'partials/list.html'}).
    when('/edit/:id', {controller:SaveRoleController, templateUrl:'partials/create.html'}).
    otherwise({redirectTo:'/list'});
}]).run(function($rootScope) {
    $rootScope.roleSelected = "selected";
  });;