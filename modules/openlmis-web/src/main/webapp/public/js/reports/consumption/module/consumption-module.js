'use strict';
require(['../../../shared/app' , '../controller/consumption-controller'], function (app) {
    app.loadApp();
    angular.module('consumption', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle'])
    .config(['$routeProvider', function ($routeProvider) {
      $routeProvider.
        when('/list', {controller:ConsumptionReportController, templateUrl:'partials/list.html',reloadOnSearch:false}).
        otherwise({redirectTo:'/list'});
    }]).run(
        function ($rootScope, AuthorizationService) {
            AuthorizationService.preAuthorize('VIEW_REPORTS');
        }
    );

angular.bootstrap(document, ['consumption']);
});