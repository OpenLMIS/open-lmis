/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

'use strict';

/* App Module */
var app = angular.module('openlmis', ['openlmis.services', 'openlmis.localStorage', 'ui.directives'],function ($routeProvider, $locationProvider, $httpProvider) {
  var interceptor = ['$rootScope', '$q', '$window', function (scope, $q, $window) {
    function success(response) {
      angular.element('#loader').hide();
      return response;
    }

    function error(response) {
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

    return function (promise) {
      return promise.then(success, error);
    };
  }];
  $httpProvider.responseInterceptors.push(interceptor);
});

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
    //TODO delete modal window
    angular.element('body > .modal-backdrop').hide();
    angular.element('.dialog').parent('.modal').remove();
  });
});

function isUndefined(value) {
  return (value == null || value == undefined || value.toString().trim().length == 0);
}

function replaceArgs(scope, displayMessage, args) {
  $.each(args, function (index, arg) {
    if (index > 0) {
      var value = scope[arg] || arg;
      displayMessage = displayMessage.replace("{" + (index - 1) + "}", value);
    }
  });
  return displayMessage;
}

jQuery.support.placeholder = !!function () {
  return "placeholder" in document.createElement("input");
}();
