'use strict';
// select-program.html, program-controller.js
//create-rnr-template.html, create-rnr-template-controller.js
angular.module('createRnRTemplate', ['openlmis', 'openlmis.services']).
    config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/select-program', {templateUrl:'../select-program.html'}).
        when('/create-rnr-template', {templateUrl:'../create-rnr-template.html'}).
        otherwise({redirectTo:'/select-program'});
}]);