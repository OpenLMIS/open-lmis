angular.module('order_fill_rate', ['openlmis','angularCombine', 'ngTable', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/list', {controller:OrderFillRateController, templateUrl:'partials/list.html',reloadOnSearch:false}).
            otherwise({redirectTo:'/list'});
    }]).run(
    function ($rootScope, AuthorizationService) {
        AuthorizationService.preAuthorize('VIEW_ORDER_FILL_RATE_REPORT');
    }
).config(function (angularCombineConfigProvider) {
        angularCombineConfigProvider.addConf(/filter-/, '/public/pages/reports/shared/filters.html');
    });