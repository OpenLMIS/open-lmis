'use strict';

require(['../../../shared/app' , '../controller/district-consumption-controller'], function (app) {

    app.loadApp();
    angular.module('district_consumption', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle'])
    .config(['$routeProvider', function ($routeProvider) {
      $routeProvider.
        when('/list', {controller:DistrictConsumptionReportController, templateUrl:'partials/list.html',reloadOnSearch:false}).
        otherwise({redirectTo:'/list'});
    }]).run(function ($rootScope, AuthorizationService) {
            AuthorizationService.preAuthorize('VIEW_DISTRICT_CONSUMPTION_REPORT');
        });
        angular.bootstrap(document, ['district_consumption']);
    });