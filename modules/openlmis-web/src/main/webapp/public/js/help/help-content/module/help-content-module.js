angular.module('help', ['openlmis','ui.bootstrap.modal', 'ui.bootstrap.dialog', 'ui.bootstrap.dropdownToggle','textAngular']).config(['$routeProvider', function ($routeProvider) {

    $routeProvider.
        when('/list',{controller: HelpContentController, templateUrl: 'partials/list.html'}).
        when('/edit/:id', {controller: HelpContentEditCotntroller, templateUrl: 'partials/edit.html'}).
        when('/create', {controller: HelpContentCreateController, templateUrl: 'partials/create.html'}).
        when('/viewhelp/:id', {controller: HelpFileContentController, templateUrl: 'partials/helpDetail.html'}).
        when('/dashboard',{controller: HelpDashboardContentController, templateUrl: 'partials/helpDashboard.html'}).
        when('/viewDashboard',{controller: HelpTreeViewController, templateUrl: 'partials/helpView.html'}).
        otherwise({redirectTo: '/viewDashboard'});
}]).run(function ($rootScope, AuthorizationService) {
    $rootScope.helpContentSelected = "selected";
//    AuthorizationService.preAuthorize('MANAGE_PRODUCT');
});
