  angular.module('product-allowed-for-facility', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dialog']).
    config(['$routeProvider', function ($routeProvider) {
      $routeProvider.
        when('/list', {controller: ProductAllowedForFacilityListController, templateUrl: 'partials/list.html'}).
        otherwise({redirectTo: '/list'});
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
      $rootScope.productAllowedForFacilitySelected = "selected";
      AuthorizationService.preAuthorize('MANAGE_PRODUCT_ALLOWED_FOR_FACILITY');
    });

