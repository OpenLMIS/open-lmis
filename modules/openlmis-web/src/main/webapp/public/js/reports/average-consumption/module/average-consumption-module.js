angular.module('averageconsumption', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle'])
    .config(['$routeProvider', function ($routeProvider) {
      $routeProvider.
        when('/list', {controller:AverageConsumptionReportController, templateUrl:'partials/list.html',reloadOnSearch:false}).
        otherwise({redirectTo:'/list'});
    }]).run(
        function ($rootScope, AuthorizationService) {
            AuthorizationService.preAuthorize('VIEW_AVERAGE_CONSUMPTION_REPORT');
        }
    );
