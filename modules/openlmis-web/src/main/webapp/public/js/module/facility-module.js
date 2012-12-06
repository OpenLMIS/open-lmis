'use strict';
angular.module('facility', ['openlmis']).
  config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/create-facility', {controller:FacilityController, templateUrl:'partials/create.html'}).
    when('/edit/:facilityId', {controller:FacilityController, templateUrl:'partials/create.html'}).
    when('/search', {controller:FacilitySearchController, templateUrl:'partials/search.html'}).
    otherwise({redirectTo:'/create-facility'});
}]);
