/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

'use strict';
var facilityModule = angular.module('facility', ['openlmis', 'ui.bootstrap.modal', 'ui.bootstrap.dialog']).
  config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/search', {controller:FacilitySearchController, templateUrl:'partials/search.html'}).
    when('/create-facility', {controller:FacilityController, templateUrl:'partials/create.html', resolve:FacilityController.resolve}).
    when('/edit/:facilityId', {controller:FacilityController, templateUrl:'partials/create.html', resolve:FacilityController.resolve}).
    otherwise({redirectTo:'/search'});
}]).run(function ($rootScope, AuthorizationService) {
    $rootScope.facilitySelected = "selected";
    AuthorizationService.preAuthorize('MANAGE_FACILITY');
  }).directive('numericValidator', function () {
    return {
      require:'?ngModel',
      link:function (scope, element, attrs, ctrl) {
        var validationFunction = facilityModule[attrs.numericValidator];

        element.bind('blur', function () {
          validationFunction(ctrl.$modelValue, element.attr('name'));
        });
        ctrl.$parsers.unshift(function (viewValue) {
          if (validationFunction(viewValue, element.attr('name'))) {
            if (viewValue == "")  viewValue = undefined;
            return viewValue;
          } else {
            ctrl.$setValidity('numeric', false);
            ctrl.$viewValue = ctrl.$modelValue;
            ctrl.$render();
            return ctrl.$modelValue;
          }
        });
      }
    };
  });

facilityModule.numericValue = function (value, errorHolder) {
  var NUMBER_REGEXP = /^\d*$/;
  var valid = (value == undefined) ? true : NUMBER_REGEXP.test(value);

  if (errorHolder != undefined) {
    document.getElementById(errorHolder).style.display = (valid) ? 'none' : 'block';
  }

  return valid;
};

