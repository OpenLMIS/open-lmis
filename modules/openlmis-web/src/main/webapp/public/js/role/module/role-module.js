'use strict';
angular.module('role', ['openlmis', 'ui.bootstrap.modal']).config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/create', {controller:RoleController, templateUrl:'partials/create.html'}).
    when('/list', {controller:ListRoleController, templateUrl:'partials/list.html'}).
    when('/edit/:id', {controller:RoleController, templateUrl:'partials/create.html'}).
    otherwise({redirectTo:'/list'});
}]).run(function($rootScope, AuthorizationService) {
    $rootScope.roleSelected = "selected";
    AuthorizationService.hasPermission('MANAGE_ROLE');
  });