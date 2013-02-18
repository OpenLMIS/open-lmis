'use strict';
angular.module('facility', ['openlmis']).
  config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/search', {controller:FacilitySearchController, templateUrl:'partials/search.html'}).
    when('/create-facility', {controller:FacilityController, templateUrl:'partials/create.html', resolve: FacilityController.resolve}).
    when('/edit/:facilityId', {controller:FacilityController, templateUrl:'partials/create.html', resolve: FacilityController.resolve}).
    otherwise({redirectTo:'/search'});
}]).run(function($rootScope) {
  $rootScope.facilitySelected = "selected";
});

