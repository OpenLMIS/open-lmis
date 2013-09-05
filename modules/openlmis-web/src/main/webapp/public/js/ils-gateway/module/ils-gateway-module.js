/**
 * Created with IntelliJ IDEA.
 * User: henok
 * Date: 9/5/13
 * Time: 12:38 AM
 * To change this template use File | Settings | File Templates.
 */

angular.module('ils-gateway', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dialog']).
    config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/dashboard', {controller: ILSGatewayDashboardController, templateUrl: 'partials/dashboard.html'}).
            otherwise({redirectTo: '/dashboard'});
    }]).directive('onKeyup', function () {
        return function (scope, elm, attrs) {
            elm.bind("keyup", function () {
                scope.$apply(attrs.onKeyup);
            });
        };
    })
    .directive('select2Blur', function () {
        return function (scope, elm, attrs) {
            angular.element("body").on('mousedown', function (e) {
                $('.select2-dropdown-open').each(function () {
                    if (!$(this).hasClass('select2-container-active')) {
                        $(this).data("select2").blur();
                    }
                })
            });
        };
    })
    .run(function ($rootScope, AuthorizationService) {
        AuthorizationService.preAuthorize('ACCESS_ILS_GATEWAY');
    });

