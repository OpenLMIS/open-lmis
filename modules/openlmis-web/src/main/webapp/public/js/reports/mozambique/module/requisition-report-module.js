angular.module('requisition-report', ['openlmis', 'angularCombine', 'ngTable'])
    .config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/requisition', {
                    controller: RequisitionReportController,
                    templateUrl: 'requisition/partials/list.html',
                    reloadOnSearch: false
                }).
                when('/single-product', {
                    controller: SingleProductReportController,
                    templateUrl: 'single-product/partials/list.html',
                    reloadOnSearch: false
                }).
                otherwise({redirectTo: '/'});
        }]).run(
    function ($rootScope, AuthorizationService) {

    }
).config(function (angularCombineConfigProvider) {
        angularCombineConfigProvider.addConf(/filter-/, '/public/pages/reports/shared/filters.html');
    });