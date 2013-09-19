/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

'use strict';

/* App Module */
var app = angular.module('openlmis', ['openlmis.services', 'openlmis.localStorage', 'ui.directives', 'ngCookies'],
  function ($httpProvider) {
    var interceptor = ['$rootScope', '$q', '$window', function (scope, $q, $window) {
      function responseSuccess(response) {
        angular.element('#loader').hide();
        return response;
      }

      function responseError(response) {
        angular.element('#loader').hide();
        switch (response.status) {
          case 403:
            $window.location = "/public/pages/access-denied.html";
            break;
          case 401:
            scope.modalShown = true;
            break;
          default:
            break;
        }
        return $q.reject(response);
      }

      return {
        'response': responseSuccess,
        'responseError': responseError
      }
    }];
    $httpProvider.interceptors.push(interceptor);
  });

app.directive('dateValidator', function () {
  return {
    require: '?ngModel',
    link: function (scope, element, attrs, ctrl) {
      app[attrs.dateValidator](element, ctrl, scope);
    }
  };
});

app.directive('numericValidator', function () {
  return {
    require: '?ngModel',
    link: function (scope, element, attrs, ctrl) {
      var validationFunction = app[attrs.numericValidator.split(',')[0]];
      var integerPartLength = attrs.numericValidator.split(',')[1];
      var fractionalPartLength = attrs.numericValidator.split(',')[2];

      function getErrorHolder() {
        var errorHolder = element.attr('error-holder');
        if (isUndefined(errorHolder)) {
          errorHolder = element.attr('name');
        }
        return errorHolder;
      }

      element.bind('blur', function () {
        validationFunction(ctrl.$modelValue, getErrorHolder(), integerPartLength, fractionalPartLength);
      });
      ctrl.$parsers.unshift(function (viewValue) {
        if (validationFunction(viewValue, getErrorHolder(), integerPartLength, fractionalPartLength)) {
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

app.date = function (element, ctrl, scope) {

  var shouldSetError = element.attr('showError');

  scope.$watch(shouldSetError, function () {
    ctrl.setError = scope[shouldSetError];
    setTimeout(validationFunction, 0);
  });

  element.keyup(function () {
    if (isUndefined(ctrl.$viewValue)) document.getElementById(element.attr('error-holder')).style.display = 'none';
  });
  element.bind('blur', validationFunction);

  element.bind('focus', function () {
    document.getElementById(element.attr('error-holder')).style.display = 'none';
  });

  function validationFunction() {
    var DATE_REGEXP = /^(0[1-9]|1[012])[/]((2)\d\d\d)$/;
    var valid = (isUndefined(ctrl.$viewValue)) ? true : DATE_REGEXP.test(ctrl.$viewValue);

    var errorHolder = document.getElementById(element.attr('error-holder'));
    errorHolder.style.display = (valid) ? 'none' : 'block';
    if (ctrl.setError)
      ctrl.$setValidity('date', valid);
  }
};


app.numericValue = function (value, errorHolder, integerPartLength, fractionalPartLength) {
  var str = '^-?(\\d{0,' + integerPartLength + '}\\.\\d{0,' + fractionalPartLength + '}|\\d{0,' + integerPartLength + '})$';
  var NUMERIC_REGEXP_FIXED_PRECISION = new RegExp(str);
  str = '\\.\\d{' + fractionalPartLength + '}.$';
  var REGEX_FOR_DIGITS_AFTER_DECIMAL = new RegExp(str);
  str = '^-?\\d*\\.?\\d{1,' + fractionalPartLength + '}$';
  var NUMBER_REGEXP = new RegExp(str);

  var valid = (value == undefined || value.length == 0) ? true : NUMERIC_REGEXP_FIXED_PRECISION.test(value);

  if (errorHolder != undefined && REGEX_FOR_DIGITS_AFTER_DECIMAL.test(value) != true) {
    document.getElementById(errorHolder).style.display = ((value == undefined || value.length == 0) ? true : (NUMBER_REGEXP.test(value))) ? 'none' : 'block';
  }

  return valid;
};

app.positiveNumericValue = function (value, errorHolder, integerPartLength, fractionalPartLength) {
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


app.integer = function (value, errorHolder) {
  var INTEGER_REGEXP_FIXED_LENGTH = /^[-]?\d{0,6}$/;
  var REGEX_FOR_SIX_DIGITS = /\d{6}.$/
  var INTEGER_REGEXP = /^[-]?\d*$/;
  var valid = (value == undefined) ? true : INTEGER_REGEXP_FIXED_LENGTH.test(value);

  if (errorHolder != undefined && REGEX_FOR_SIX_DIGITS.test(value) != true) {
    document.getElementById(errorHolder).style.display = ((value == undefined) ? true : (INTEGER_REGEXP.test(value))) ? 'none' : 'block';
  }

  return valid;
};


app.positiveInteger = function (value, errorHolder) {
  var POSITIVE_INTEGER_REGEXP_FIXED_LENGTH = /^\d*$/;

  var valid = (value == undefined) ? true : POSITIVE_INTEGER_REGEXP_FIXED_LENGTH.test(value);

  if (errorHolder != undefined && document.getElementById(errorHolder) != null) {
    document.getElementById(errorHolder).style.display = (valid) ? 'none' : 'block';
  }

  return valid;
};

app.config(function ($httpProvider) {
  var spinnerFunction = function (data) {
    angular.element('#loader').show();
    return data;
  };
  $httpProvider.defaults.transformRequest.push(spinnerFunction);
  $httpProvider.defaults.headers.common["X-Requested-With"] = "XMLHttpRequest";
});

app.run(function ($rootScope) {
  $rootScope.$on('$routeChangeStart', function () {
    angular.element('#ui-datepicker-div').hide();
    angular.element('#select2-drop').hide();
    angular.element('#select2-drop-mask').hide();
    //TODO delete modal window
    angular.element('body > .modal-backdrop').hide();
    angular.element('.dialog').parent('.modal').remove();
  });
});

function isUndefined(value) {
  return (value == null || value == undefined || value.toString().trim().length == 0);
}


