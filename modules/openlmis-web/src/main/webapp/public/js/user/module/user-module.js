'use strict';
angular.module('user', ['openlmis', 'ngGrid', 'ui.bootstrap.modal']).
    config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
      when('/search', {controller:UserSearchController, templateUrl:'partials/search.html'}).
      when('/create-user', {controller:UserController, templateUrl:'partials/create.html'}).
      when('/edit/:userId', {controller:UserController, templateUrl:'partials/create.html'}).
    otherwise({redirectTo:'/search'});
}]).directive('onKeyup', function () {
      return function (scope, elm, attrs) {
        elm.bind("keyup", function () {
          scope.$apply(attrs.onKeyup);
        });
      };
    }).run(function($rootScope, AuthorizationService) {
    $rootScope.userSelected = "selected";
    AuthorizationService.hasPermission('MANAGE_USERS');
  });


