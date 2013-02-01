'use strict';
angular.module('resetPassword', ['openlmis']).
  config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/token/:token', {controller:ResetPasswordController, resolve : ResetPasswordController.resolve}).
    otherwise({redirectTo:'/create-user'});
}]);


