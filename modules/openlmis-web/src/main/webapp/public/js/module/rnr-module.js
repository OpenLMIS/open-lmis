'use strict';
angular.module('rnr', ['openlmis', 'openlmis.services']).
    config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/new-rnr', {controller:RnrController, templateUrl:'select-facility.html'}).
        when('/rnr-header', {controller:HeaderController, templateUrl:'rnr-header.html'}).
        when('/access-denied', {templateUrl:'access-denied.html'}).
        otherwise({redirectTo:'/new-rnr'});
}]);
