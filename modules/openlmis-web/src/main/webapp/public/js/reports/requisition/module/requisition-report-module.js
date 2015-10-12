
angular.module('requisition-report', ['openlmis', 'angularCombine',"ngTable"])
    .config(['$routeProvider',
        function ($routeProvider) {
        $routeProvider.
            when('/list', {
                controller:RequisitionReportController,
                templateUrl:'partials/list.html',
                reloadOnSearch:false}).
            otherwise({redirectTo:'/list'});
    }]).run(
    function ($rootScope, AuthorizationService) {

    }
).config(function(angularCombineConfigProvider) {
        angularCombineConfigProvider.addConf(/filter-/, '/public/pages/reports/shared/filters.html');
    });