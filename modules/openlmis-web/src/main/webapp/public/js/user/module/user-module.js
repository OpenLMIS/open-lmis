'use strict';
angular.module('user', ['openlmis','ngGrid']).
  config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/search', {controller:UserSearchController, templateUrl:'partials/search.html'}).
    when('/create-user', {controller:UserController, templateUrl:'partials/create.html'}).
    when('/edit/:userId', {controller:UserController, templateUrl:'partials/create.html'}).
    otherwise({redirectTo:'/create-user'});
}]);

