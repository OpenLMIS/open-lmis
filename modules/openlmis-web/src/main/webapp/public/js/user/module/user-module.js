'use strict';
angular.module('user', ['openlmis', 'ngGrid']).
    config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
      when('/search', {controller:UserSearchController, templateUrl:'partials/search.html'}).
      when('/create-user', {controller:UserController, templateUrl:'partials/create.html'}).
      when('/edit/:userId', {controller:UserController, templateUrl:'partials/create.html'}).
      otherwise({redirectTo:'/create-user'});
}]).directive('onKeyup', function () {
      return function (scope, elm, attrs) {
        elm.bind("keyup", function () {
          scope.$apply(attrs.onKeyup);
        });
      };
    }).directive('uiValidateEquals', function() {

  return {
    restrict: 'A',
    require: 'ngModel',
    link: function(scope, elm, attrs, ctrl) {

      function validateEqual(myValue, otherValue) {
        if (myValue === otherValue) {
          ctrl.$setValidity('equal', true);
          return myValue;
        } else {
          ctrl.$setValidity('equal', false);
          return undefined;
        }
      }

      scope.$watch(attrs.uiValidateEquals, function(otherModelValue) {
        validateEqual(ctrl.$viewValue, otherModelValue);
      });

      ctrl.$parsers.unshift(function(viewValue) {
        return validateEqual(viewValue, scope.$eval(attrs.uiValidateEquals));
      });

      ctrl.$formatters.unshift(function(modelValue) {
        return validateEqual(modelValue, scope.$eval(attrs.uiValidateEquals));
      });
    }
  };
});


