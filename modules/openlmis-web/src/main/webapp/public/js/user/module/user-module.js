'use strict';
angular.module('user', ['openlmis', 'ngGrid']).
    config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
      when('/search', {controller:UserSearchController, templateUrl:'partials/search.html'}).
      when('/create-user', {controller:UserController, templateUrl:'partials/create.html'}).
      when('/edit/:userId', {controller:UserController, templateUrl:'partials/create.html'}).
      when('/token/:token', {controller:ResetPasswordController, resolve : ResetPasswordController.resolve}).
    otherwise({redirectTo:'/create-user'});
}]).directive('onKeyup', function () {
      return function (scope, elm, attrs) {
        elm.bind("keyup", function () {
          scope.$apply(attrs.onKeyup);
        });
      };
    });


