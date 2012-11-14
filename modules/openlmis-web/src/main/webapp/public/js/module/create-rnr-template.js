'use strict';
// select-program.html, program-controller.js
//create-rnr-template.html, create-rnr-template-controller.js
angular.module('createRnRTemplate', ['openlmis']).
    config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/select-program', {controller:CreateRnrTemplateController, templateUrl:'../select-program.html'}).
        when('/create-rnr-template', {controller:SaveRnrTemplateController, templateUrl:'../create-rnr-template.html'}).
        otherwise({redirectTo:'/select-program'});
}]);