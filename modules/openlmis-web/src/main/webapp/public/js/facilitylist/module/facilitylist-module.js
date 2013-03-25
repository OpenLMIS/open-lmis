'use strict';
angular.module('facilitylist', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle'])
.config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/list', {controller:ListFacilitiesController, templateUrl:'partials/list.html'}).
    otherwise({redirectTo:'/list'});
}]).run(function($rootScope) {
   // $rootScope.Selected = "selected";
  });