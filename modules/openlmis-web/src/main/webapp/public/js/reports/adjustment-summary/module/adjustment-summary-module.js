'use strict';
require(['../../../shared/app' , '../controller/adjustment-summary-controller'], function (app) {
    app.loadApp();
    angular.module('adjustmentsummary', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle'])
    .config(['$routeProvider', function ($routeProvider) {
      $routeProvider.
        when('/list', {controller:AdjustmentSummaryReportController, templateUrl:'partials/list.html',reloadOnSearch:false}).
        otherwise({redirectTo:'/list'});
    }]).run(
        function ($rootScope, AuthorizationService) {
            AuthorizationService.preAuthorize('VIEW_ADJUSTMENT_SUMMARY_REPORT');
        }
    );

    angular.bootstrap(document, ['adjustmentsummary']);
});