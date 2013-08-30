angular.module('district_consumption', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle'])
    .config(['$routeProvider', function ($routeProvider) {
      $routeProvider.
        when('/list', {controller:DistrictConsumptionReportController, templateUrl:'partials/list.html',reloadOnSearch:false}).
        otherwise({redirectTo:'/list'});
    }]).run(function ($rootScope, AuthorizationService) {
        AuthorizationService.preAuthorize('VIEW_DISTRICT_CONSUMPTION_REPORT');
    });
