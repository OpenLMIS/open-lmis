angular.module('stock_imbalance', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/list', {controller:StockImbalanceController(), templateUrl:'partials/list.html',reloadOnSearch:false}).
            otherwise({redirectTo:'/list'});
    }]).run(
    function ($rootScope, AuthorizationService) {
        AuthorizationService.preAuthorize('VIEW_STOCK_IMBALANCE_REPORT');
    }
);