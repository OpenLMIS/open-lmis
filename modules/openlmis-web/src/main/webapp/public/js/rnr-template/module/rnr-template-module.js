'use strict';
angular.module('createRnRTemplate', ['openlmis']).config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/select-program', {
      controller:ConfigureRnRTemplateController,
      templateUrl:'../select-program.html',
      resolve:ConfigureRnRTemplateController.resolve }).

    when('/create-rnr-template/:programId', {
      controller:SaveRnrTemplateController,
      templateUrl:'partials/form.html',
      resolve:SaveRnrTemplateController.resolve }).

    otherwise({redirectTo:'/select-program'});
}]);