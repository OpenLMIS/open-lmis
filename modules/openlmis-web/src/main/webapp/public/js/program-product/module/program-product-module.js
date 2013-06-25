/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

'use strict';
var programProductModule = angular.module('programProductModule', ['openlmis', 'ui.bootstrap.modal']).config(['$routeProvider', function ($routeProvider) {
      $routeProvider.
          when('/select-push-program', {
            controller: ProgramProductController,
            templateUrl: 'partials/list.html',
            resolve: ProgramProductController.resolve }).

          otherwise({redirectTo: '/select-push-program'});
    }]).directive('numericValidator', function () {
      return {
        require: '?ngModel',
        link: function (scope, element, attrs, ctrl) {
          var validationFunction = programProductModule[attrs.numericValidator.split(',')[0]];
          var integerPartLength = attrs.numericValidator.split(',')[1];
          var fractionalPartLength = attrs.numericValidator.split(',')[2];

          element.bind('blur', function () {
            validationFunction(ctrl.$modelValue, element.attr('name'), integerPartLength, fractionalPartLength);
          });
          ctrl.$parsers.unshift(function (viewValue) {
            if (validationFunction(viewValue, element.attr('name'), integerPartLength, fractionalPartLength)) {
              if (viewValue == "")  viewValue = undefined;
              return viewValue;
            } else {
              ctrl.$viewValue = ctrl.$modelValue;
              ctrl.$render();
              return ctrl.$modelValue;
            }
          });
        }
      };
    });

angular.bootstrap(document, ['programProductModule']);

programProductModule.numericValue = function (value, errorHolder, integerPartLength, fractionalPartLength) {
  var str = '^(\\d{0,' + integerPartLength + '}\\.\\d{0,' + fractionalPartLength + '}|\\d{0,' + integerPartLength + '})$';
  var NUMERIC_REGEXP_FIXED_PRECISION = new RegExp(str);
  str = '\\.\\d{' + fractionalPartLength + '}.$';
  var REGEX_FOR_DIGITS_AFTER_DECIMAL = new RegExp(str);
  str = '^\\d*\\.?\\d{1,' + fractionalPartLength + '}$';
  var NUMBER_REGEXP = new RegExp(str);

  var valid = (value == undefined) ? true : NUMERIC_REGEXP_FIXED_PRECISION.test(value);

  if (errorHolder != undefined && REGEX_FOR_DIGITS_AFTER_DECIMAL.test(value) != true) {
    document.getElementById(errorHolder).style.display = ((value == undefined) ? true : (NUMBER_REGEXP.test(value))) ? 'none' : 'block';
  }

  return valid;
};

programProductModule.positiveInteger = function (value, errorHolder) {
  var POSITIVE_INTEGER_REGEXP_FIXED_LENGTH = /^\d*$/;

  var valid = (value == undefined) ? true : POSITIVE_INTEGER_REGEXP_FIXED_LENGTH.test(value);

  if (errorHolder != undefined) {
    document.getElementById(errorHolder).style.display = (valid) ? 'none' : 'block';
  }

  return valid;
};

programProductModule.integer = function (value, errorHolder) {
  var INTEGER_REGEXP_FIXED_LENGTH = /^[-]?\d{0,6}$/;
  var REGEX_FOR_SIX_DIGITS = /\d{6}.$/
  var INTEGER_REGEXP = /^[-]?\d*$/;
  var valid = (value == undefined) ? true : INTEGER_REGEXP_FIXED_LENGTH.test(value);

  if (errorHolder != undefined && REGEX_FOR_SIX_DIGITS.test(value) != true) {
    document.getElementById(errorHolder).style.display = ((value == undefined) ? true : (INTEGER_REGEXP.test(value))) ? 'none' : 'block';
  }

  return valid;
};
