'use strict';
angular.module('facilitylist', ['openlmis'])
.config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/list', {controller:ListFacilitiesController, templateUrl:'partials/list.html'}).
    otherwise({redirectTo:'/list'});
}]).run(function($rootScope) {
   // $rootScope.Selected = "selected";
  });