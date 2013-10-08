angular.module('facilitylist', ['openlmis' , 'ngTable', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
        when('/list', {controller:ListFacilitiesController, templateUrl:'partials/list.html',reloadOnSearch:false}).
        otherwise({redirectTo:'/list'});
    }]).run(function ($rootScope, AuthorizationService) {
        AuthorizationService.preAuthorize('VIEW_FACILITY_REPORT');
    });