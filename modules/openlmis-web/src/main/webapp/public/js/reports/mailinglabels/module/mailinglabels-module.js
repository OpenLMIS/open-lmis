'use strict';
require(['../../../shared/app' , '../controller/list-mailinglabels-controller'], function (app) {
    app.loadApp();
    angular.module('mailinglabels', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle'])
        .config(['$routeProvider', function ($routeProvider) {
            $routeProvider.
                when('/list', {controller:ListMailinglabelsController, templateUrl:'partials/list.html',reloadOnSearch:false}).
                otherwise({redirectTo:'/list'});
        }]).run(
        function ($rootScope, AuthorizationService) {
            AuthorizationService.preAuthorize('VIEW_REPORTS');
        }
    );

    angular.bootstrap(document, ['mailinglabels']);
});