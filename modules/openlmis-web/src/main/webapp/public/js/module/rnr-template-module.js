'use strict';
angular.module('createRnRTemplate', ['openlmis']).
    config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/select-program', {controller:ConfigureRnRTemplateController, templateUrl:'../select-program.html'}).
        when('/create-rnr-template/:programCode', {controller:SaveRnrTemplateController, templateUrl:'partials/form.html', resolve:
            SaveRnrTemplateController.resolve }).
        otherwise({redirectTo:'/select-program'});
}]);