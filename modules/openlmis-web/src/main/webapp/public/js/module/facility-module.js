'use strict';
angular.module('facility', ['openlmis']).
  config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/create-facility', {controller:FacilityController, templateUrl:'partials/create.html'}).
    otherwise({redirectTo:'/create-facility'});
}]);
