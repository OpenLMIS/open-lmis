'use strict';
angular.module('rnr', ['openlmis', 'openlmis.services']).
    config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('', {templateUrl:'select-facility.html'}).
        when('/rnr-header', {controller:HeaderController, templateUrl:'rnr-header.html'}).
        otherwise({redirectTo:''});
}]);