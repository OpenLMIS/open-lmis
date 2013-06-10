'use strict';
require(['../../../shared/app' , '../controller/average-consumption-controller'], function (app) {
    app.loadApp();
    angular.module('averageconsumption', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle'])
    .config(['$routeProvider', function ($routeProvider) {
      $routeProvider.
        when('/list', {controller:AverageConsumptionReportController, templateUrl:'partials/list.html',reloadOnSearch:false}).
        otherwise({redirectTo:'/list'});
    }]).run(
        function ($rootScope, AuthorizationService) {
            AuthorizationService.preAuthorize('VIEW_REPORTS');
        }
    );

    angular.bootstrap(document, ['averageconsumption']);
});