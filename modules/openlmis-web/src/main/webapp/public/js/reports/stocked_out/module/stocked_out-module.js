'use strict';
require(['../../../shared/app' , '../controller/stocked_out-controller'], function (app) {
    app.loadApp();

    angular.module('stocked_out', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle'])
    .config(['$routeProvider', function ($routeProvider) {
      $routeProvider.
        when('/list', {controller:StockedOutController, templateUrl:'partials/list.html',reloadOnSearch:false}).
        otherwise({redirectTo:'/list'});
    }]).run(
    function ($rootScope, AuthorizationService) {
        AuthorizationService.preAuthorize('VIEW_REPORTS');
    }
);

    angular.bootstrap(document, ['stocked_out']);
});