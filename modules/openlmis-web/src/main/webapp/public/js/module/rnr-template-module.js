'use strict';
angular.module('createRnRTemplate', ['openlmis']).
    config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/select-program', {controller:ConfigureRnRTemplateController, templateUrl:'../select-program.html'}).
        when('/create-rnr-template', {controller:ConfigureRnRTemplateController, templateUrl:'partials/form.html'}).
        when('/save-rnr-template', {controller:SaveRnrTemplateController, templateUrl:'partials/form.html'}).
        otherwise({redirectTo:'/select-program'});
}]);