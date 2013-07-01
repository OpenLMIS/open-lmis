  angular.module('geographic-zones', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dialog']).
    config(['$routeProvider', function ($routeProvider) {
      $routeProvider.
        when('/search', {controller: GeographicZonesSearchController, templateUrl: 'partials/search.html'}).
        when('/create-geographic-zone', {controller: GeographicZonesController, templateUrl: 'partials/create.html'}).
        when('/edit/:geographicZoneId', {controller: GeographicZonesController, templateUrl: 'partials/create.html'}).
        otherwise({redirectTo: '/search'});
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
      $rootScope.geographicZonesSelected = "selected";
      AuthorizationService.preAuthorize('MANAGE_GEOGRAPHIC_ZONES');
    });

