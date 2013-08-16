angular.module('product', ['openlmis','ui.bootstrap.modal', 'ui.bootstrap.dialog', 'ui.bootstrap.dropdownToggle']).config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/list', {controller: ProductController, templateUrl: 'partials/list.html'}).
            otherwise({redirectTo: '/list'});
    }]).run(function ($rootScope, AuthorizationService) {
        $rootScope.productSelected = "selected";
        AuthorizationService.preAuthorize('MANAGE_PRODUCT');
    });
