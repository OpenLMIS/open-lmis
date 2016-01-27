/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

/* App Module */



var app = angular.module('openlmis', ['openlmis.services', 'angular-google-analytics', 'openlmis.localStorage', 'ui.directives', 'ngCookies', 'ngRoute'],
  function ($httpProvider) {
    var interceptor = ['$q', '$window', 'loginConfig', function ($q, $window, loginConfig) {
      var requestCount = 0;

      function responseSuccess(response) {
        if (!(--requestCount))
          angular.element('#loader').hide();
        return response;
      }

      function responseError(response) {
        if (!(--requestCount))
          angular.element('#loader').hide();
        switch (response.status) {
          case 403:
            $window.location = "/public/pages/access-denied.html";
            break;
          case 401:
            loginConfig.preventReload = (response.config.method != 'GET');
            loginConfig.modalShown = true;
            break;
          default:
            break;
        }
        return $q.reject(response);
      }

      function request(config) {
        if ((++requestCount) > 0)
          angular.element('#loader').show();
        config.headers["X-Requested-With"] = "XMLHttpRequest";
        return config;
      }

      return {
        'request': request,
        'response': responseSuccess,
        'responseError': responseError
      };
    }];
    $httpProvider.interceptors.push(interceptor);
  });

app.value("loginConfig", {modalShown: false, preventReload: false});

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
      var validationFunction = app[attrs.numericValidator.split(',')[0]],
          integerPartLength = attrs.numericValidator.split(',')[1],
          fractionalPartLength = attrs.numericValidator.split(',')[2],
          min = parseFloat(attrs.min),
          max = parseFloat(attrs.max),
          maxlength = parseFloat(attrs.maxlength),
          range = !isUndefined(min) && !isUndefined(max),
          onlyMin = !range && !isUndefined(min),
          onlyMax = !range && !isUndefined(max);

      function getErrorHolder() {
        var errorHolder = element.attr('error-holder');
        if (isUndefined(errorHolder)) {
          errorHolder = element.attr('name');
        }
        return errorHolder;
      }

      function checkCondition(value) {
        var condition = true;

        if (maxlength) {
          condition = value.toString().length <= maxlength;
        } else if (range) {
          condition = value >= min && value <= max;
        } else if (onlyMin) {
          condition = value >= min;
        } else if (onlyMax) {
          condition = value <= max;
        }

        return condition;
      }

      function allowKey(e) {
        var decimal = ".",
            negative = true,
            decimalPlaces = -1,
            elem = $(element);

        // get the key that was pressed
        var key = e.charCode ? e.charCode : e.keyCode ? e.keyCode : 0;

        // allow enter/return key (only when in an input box)
        if (key == 13 && elem.prop('nodeName').toLowerCase() == "input") {
            return true;
        } else if (key == 13) {
            return false;
        } else if (e.shiftKey && (key == 35 || key == 36 || key == 37)) {
            //dont allow #, $, %
            return false;
        }

        var allow = false;

        // allow Ctrl+A
        if ((e.ctrlKey && key == 97 /* firefox */) || (e.ctrlKey && key == 65) /* opera */) {
            return true;
        }

        // allow Ctrl+X (cut)
        if ((e.ctrlKey && key == 120 /* firefox */) || (e.ctrlKey && key == 88) /* opera */) {
            return true;
        }

        // allow Ctrl+C (copy)
        if ((e.ctrlKey && key == 99 /* firefox */) || (e.ctrlKey && key == 67) /* opera */) {
            return true;
        }

        // allow Ctrl+Z (undo)
        if ((e.ctrlKey && key == 122 /* firefox */) || (e.ctrlKey && key == 90) /* opera */) {
            return true;
        }

        // allow or deny Ctrl+V (paste), Shift+Ins
        if ((e.ctrlKey && key == 118 /* firefox */) || (e.ctrlKey && key == 86) /* opera */ || (e.shiftKey && key == 45)) {
            return true;
        }

        // if a number was not pressed
        if (key < 48 || key > 57) {
            var value = elem.val();

            /* '-' only allowed at start and if negative numbers allowed */
            if($.inArray('-', value.split('')) !== 0 && negative && key == 45 && (value.length === 0 || parseInt($.fn.getSelectionStart(elem), 10) === 0)) {
                return true;
            }

            /* only one decimal separator allowed */
            if(decimal && key == decimal.charCodeAt(0) && $.inArray(decimal, value.split('')) != -1) {
                allow = false;
            }

            // check for other keys that have special purposes
            if(
                key != 8 /* backspace */ &&
                key != 9 /* tab */ &&
                key != 13 /* enter */ &&
                key != 35 /* end */ &&
                key != 36 /* home */ &&
                key != 37 /* left */ &&
                key != 38 /* up */ &&
                key != 39 /* right */ &&
                key != 40 /* down */ &&
                key != 46 /* del */
            ) {
                allow = false;
            } else {
                // for detecting special keys (listed above)
                // IE does not support 'charCode' and ignores them in keypress anyway
                if(typeof e.charCode != "undefined") {
                    // special keys have 'keyCode' and 'which' the same (e.g. backspace)
                    if(e.keyCode == e.which && e.which !== 0) {
                        allow = true;

                        // . and delete share the same code, don't allow . (will be set to true later if it is the decimal point)
                        if(e.which == 46) {
                            allow = false;
                        }
                    } else if(e.keyCode !== 0 && e.charCode === 0 && e.which === 0) {
                        // or keyCode != 0 and 'charCode'/'which' = 0
                        allow = true;
                    }
                }
            }

            // if key pressed is the decimal and it is not already in the field
            if(decimal && key == decimal.charCodeAt(0)) {
                if($.inArray(decimal, value.split('')) == -1) {
                    allow = true;
                } else {
                    allow = false;
                }
            }
        } else {
            allow = true;

            // remove extra decimal places
            if(decimal && decimalPlaces > 0) {
                var selectionStart = $.fn.getSelectionStart(elem);
                var selectionEnd = $.fn.getSelectionEnd(elem);
                var dot = $.inArray(decimal, elem.val().split(''));

                if (selectionStart === selectionEnd && dot >= 0 && selectionStart > dot && elem.val().length > dot + decimalPlaces) {
                    allow = false;
                }
            }

        }

        return allow;
      }

      element.bind('keypress', function (e) {
        var allow = allowKey(e),
            errorHolder = getErrorHolder(),
            value = allow ? (ctrl.$modelValue || '') : '',
            valueAsNumber = parseFloat(value);

        if (allow && (isNaN(valueAsNumber) || checkCondition(valueAsNumber))) {
          validationFunction(value.toString(), errorHolder, integerPartLength, fractionalPartLength);
        } else {
          document.getElementById(errorHolder).style.display = allow ? 'none' : 'block';
        }

        return allow;
      });

      element.bind('blur', function () {
        var value = ctrl.$modelValue;
        var valueAsNumber = parseFloat(value);

        if (isNaN(valueAsNumber) || checkCondition(valueAsNumber)) {
          validationFunction(value, getErrorHolder(), integerPartLength, fractionalPartLength);
        }
      });

      ctrl.$parsers.unshift(function (viewValue) {
        var valueAsNumber = parseFloat(viewValue);

        if ((isNaN(valueAsNumber) || checkCondition(valueAsNumber)) && validationFunction(viewValue, getErrorHolder(), integerPartLength, fractionalPartLength)) {
          if (viewValue === "")  viewValue = undefined;
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

  var valid = (value === undefined || value === null || value.length === 0) ? true : NUMERIC_REGEXP_FIXED_PRECISION.test(value);

  if (errorHolder !== undefined && REGEX_FOR_DIGITS_AFTER_DECIMAL.test(value) === false) {
    document.getElementById(errorHolder).style.display =
      ((value === undefined || value === null || value.length === 0) ? true : (NUMBER_REGEXP.test(value))) ? 'none' : 'block';
  }

  return valid;
};

app.positiveNumericValue = function (value, errorHolder, integerPartLength, fractionalPartLength) {
  var str = '^(\\d{0,'.concat(integerPartLength).concat('}\\.\\d{0,').concat(fractionalPartLength)
    .concat('}|\\d{0,').concat(integerPartLength).concat('})$');
  var NUMERIC_REGEXP_FIXED_PRECISION = new RegExp(str);
  str = '\\.\\d{'.concat(fractionalPartLength).concat('}.$');
  var REGEX_FOR_DIGITS_AFTER_DECIMAL = new RegExp(str);
  str = '^\\d*\\.?(\\d{1,'.concat(fractionalPartLength).concat('})?$');
  var NUMBER_REGEXP = new RegExp(str);

  var valid = (value === undefined) ? true : NUMERIC_REGEXP_FIXED_PRECISION.test(value);
  if (value === '.') valid = false;

  if (errorHolder !== undefined && REGEX_FOR_DIGITS_AFTER_DECIMAL.test(value) === false) {
    document.getElementById(errorHolder).style.display = ((value === undefined || value === "") ? true : (NUMBER_REGEXP.test(value))) ? 'none' : 'block';
  }

  return valid;
};


app.integer = function (value, errorHolder, length) {
  var str = '^[-]?\\d{0,'.concat(length).concat('}$');
  var INTEGER_REGEXP_FIXED_LENGTH = new RegExp(str);
  str = '\\d{'.concat(length).concat('}.$');
  var REGEX_FOR_SIX_DIGITS = new RegExp(str);
  var INTEGER_REGEXP = /^[-]?\d*$/;
  var valid = (value === undefined || value === null) ? true : INTEGER_REGEXP_FIXED_LENGTH.test(value);

  if (errorHolder !== undefined && REGEX_FOR_SIX_DIGITS.test(value) === false) {
    document.getElementById(errorHolder).style.display = ((value === undefined || value === null) ? true : (INTEGER_REGEXP.test(value))) ? 'none' : 'block';
  }

  return valid;
};


app.positiveInteger = function (value, errorHolder, length) {
  var str = isUndefined(length) ? "^\\d*$" : "^\\d{0,".concat(length).concat("}$"),
      POSITIVE_INTEGER_REGEXP_FIXED_LENGTH = new RegExp(str),
      valid;

  if (value === undefined || value === null)
    valid = true;
  else if (value.toString().length >= 0)
    valid = POSITIVE_INTEGER_REGEXP_FIXED_LENGTH.test(value);

  if (errorHolder !== undefined && document.getElementById(errorHolder) !== null) {
    document.getElementById(errorHolder).style.display = (valid) ? 'none' : 'block';
  }

  return valid;
};

app.run(function ($rootScope, messageService) {
  $rootScope.$on('$routeChangeStart', function () {
    angular.element('#ui-datepicker-div').hide();
    angular.element('#select2-drop').hide();
    angular.element('#select2-drop-mask').hide();
    //TODO delete modal window
    angular.element('body > .modal-backdrop').hide();
    angular.element('.dialog').parent('.modal').remove();
  });

  $rootScope.getLocalMessage = function(key){
    return messageService.get(key);
  };

  var setState = function(state) {
    $rootScope.appCacheState = state;
    $rootScope.$apply();
  };

  window.applicationCache.addEventListener('progress', function () {
    setState("progress");
  });

  window.applicationCache.addEventListener('error', function () {
    setState("error");
  });

  window.applicationCache.addEventListener('cached', function () {
    setState("cached");
  });

  window.applicationCache.addEventListener('updateready', function () {
    setState("cached");
  });

});

function isUndefined(value) {
  return (value === null || value === undefined || value.toString().trim().length === 0);
}


var _gaq = _gaq || [];

angular.module('angular-google-analytics', []).run(
  ['gglAnalytics','localStorageService',
  function (gglAnalytics, localStorageService) {
    if(gglAnalytics){

      var googleAccount = localStorageService.get('GOOGLE_ANALYTICS_TRACKING_CODE');
      if(googleAccount !== null){
        _gaq.push(['_setAccount', googleAccount]);
        var ga = document.createElement('script');
        ga.type = 'text/javascript';
        ga.async = false;
        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
        var s = document.getElementsByTagName('script')[0];
        s.parentNode.insertBefore(ga, s);
      } 
    }

  }])
  .service('gglAnalytics',
  ['$rootScope', 'localStorageService' ,'$window',
  function ($rootScope, localStorageService, $window) {

    var enableTracking = localStorageService.get('ENABLE_GOOGLE_ANALYTICS');
    if(enableTracking === null || !enableTracking){
      return false;
    }
    function track() {
      var path = $window.location.href;
      var user = localStorageService.get('USERNAME');

      $window._gaq.push(['_setCustomVar',1, 'Who', user, 2]);
      $window._gaq.push(['set','&uid',user]);
      $window._gaq.push(['_trackPageview', path]);
    }

    //fire on each route change
    $rootScope.$on('$viewContentLoaded', track);

    return true;
  }]);
