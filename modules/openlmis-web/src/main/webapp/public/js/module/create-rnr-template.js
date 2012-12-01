'use strict';
angular.module('createRnRTemplate', ['openlmis']).
  config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/select-program', {controller:CreateRnrTemplateController, templateUrl:'../select-program.html'}).
    when('/create-rnr-template', {controller:SaveRnrTemplateController, templateUrl:'../create-rnr-template.html'}).
    otherwise({redirectTo:'/select-program'});
}]);