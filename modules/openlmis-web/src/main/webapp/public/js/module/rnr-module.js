'use strict';
angular.module('rnr', ['openlmis']).
    config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/new-rnr', {controller:RnrController, templateUrl:'select-facility.html'}).
        when('/rnr-header', {controller:RnrHeaderController, templateUrl:'rnr-header.html'}).
        otherwise({redirectTo:'/new-rnr'});
}]);
