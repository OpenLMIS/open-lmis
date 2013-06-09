'use strict';
require(['../../../shared/app' , '../controller/summary-controller'], function (app) {
    app.loadApp();
    angular.module('summary', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle'])
        .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/list', {controller:SummaryReportController, templateUrl:'partials/list.html',reloadOnSearch:false}).
            otherwise({redirectTo:'/list'});
        }]).run(
            function ($rootScope, AuthorizationService) {
                AuthorizationService.preAuthorize('VIEW_REPORTS');
            }
    );

    angular.bootstrap(document, ['summary']);
});