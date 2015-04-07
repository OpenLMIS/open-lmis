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


app.positiveInteger = function (value, errorHolder) {
  var POSITIVE_INTEGER_REGEXP_FIXED_LENGTH = /^[0-9]*$/, valid;

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
