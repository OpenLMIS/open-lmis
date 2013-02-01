'use strict';
angular.module('resetPassword', ['openlmis']).
  config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/token/:token', {controller:ValidateTokenController, resolve : ValidateTokenController.resolve}).
    when('/reset/:token', {controller:ResetPasswordController, templateUrl : 'partials/reset-password-form.html'}).
    when('/reset/password/complete', {controller:ResetCompleteController, templateUrl : 'partials/reset-password-complete.html'}).
    otherwise({redirectTo:'/token/:token'});
}]);


