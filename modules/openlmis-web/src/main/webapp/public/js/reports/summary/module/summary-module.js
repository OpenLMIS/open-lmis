angular.module('summary', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle'])
        .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/list', {controller:SummaryReportController, templateUrl:'partials/list.html',reloadOnSearch:false}).
            otherwise({redirectTo:'/list'});
        }]).run(
            function ($rootScope, AuthorizationService) {
                AuthorizationService.preAuthorize('VIEW_SUMMARY_REPORT');
            }
    );