'use strict';
angular.module('rnr', ['openlmis']).
    config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/select-facility', {templateUrl:'select-facility.html'}).
        otherwise({redirectTo:'/select-facility'});
}]);