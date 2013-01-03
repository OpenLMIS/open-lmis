'use strict';
angular.module('schedule', ['openlmis']).config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/list', {controller:ScheduleController, templateUrl:'partials/list.html'}).
    when('/manage-period/:id', {controller:SchedulePeriodController, templateUrl:'partials/period.html'}).
    otherwise({redirectTo:'/list'});
}]);