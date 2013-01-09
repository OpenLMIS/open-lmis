'use strict';
angular.module('upload', ['openlmis']).config(['$routeProvider', function ($routeProvider) {
  $routeProvider.when('/upload', {controller:UploadController, templateUrl:'partials/form.html'}).
    otherwise({redirectTo:'/upload'});
}]);