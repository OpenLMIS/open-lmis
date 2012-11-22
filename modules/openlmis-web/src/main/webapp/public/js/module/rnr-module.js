'use strict';
angular.module('rnr', ['openlmis']).
    config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/init-rnr', {controller:InitiateRnrController, templateUrl:'partials/init.html'}).
        when('/create-rnr', {controller:CreateRnrController, templateUrl:'partials/create.html'}).
        otherwise({redirectTo:'/init-rnr'});
}]);
