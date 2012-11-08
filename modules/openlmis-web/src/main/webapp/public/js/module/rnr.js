'use strict';
angular.module('rnr', ['openlmis', 'openlmis.services']).
    config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/select-facility', {templateUrl:'select-facility.html'}).
        when('/select-program', {controller:RnrController, templateUrl:'select-program-for-facility.html'}).
        when('/rnr-header', {templateUrl:'rnr-header.html'}).
        otherwise({redirectTo:'/select-facility'});
}]);