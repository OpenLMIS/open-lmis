'use strict';
angular.module('facility', ['openlmis']).
  config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/search', {controller:FacilitySearchController, templateUrl:'partials/search.html', resolve: FacilitySearchController.resolve}).
    when('/create-facility', {controller:FacilityController, templateUrl:'partials/create.html'}).
    when('/edit/:facilityId', {controller:FacilityController, templateUrl:'partials/create.html'}).
    otherwise({redirectTo:'/create-facility'});
}]);

