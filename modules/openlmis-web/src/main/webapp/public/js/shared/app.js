/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */


var loadNext = ['jQueryUi', 'angularResource', 'angularUi', 'uiBootstrap', 'bootstrap', 'localStorage', 'services', ''];
var loadFirst = ['jQuery', 'underscore', 'angular'];

requirejs.config({
  baseUrl:'/public/js/shared',
  paths:{
    jQuery:'../../lib/jquery/jquery-1.8.2.min',
    jQueryUi:'../../lib/jquery/jquery-ui-1.9.2.custom.min',
    underscore:'../../lib/underscore/underscore-min',
    angular:'../../lib/angular/angular',
    angularResource:'../../lib/angular/angular-resource',
    bootstrap:'../../lib/bootstrap/js/bootstrap.min',
    angularUi:'../../lib/angular-ui/angular-ui.min',
    uiBootstrap:'../../lib/angular-ui/bootstrap/ui-bootstrap-0.1.0.min',
    localStorage:'../../lib/localstorage/localStorage',
    services:'services/services',
    messageService:'services/message-service',
    navigateBackService:'services/navigate-back-service',
    authorizationService:'services/authorization-service',
    headerController:'controller/header-controller',
    loginController:'controller/login-controller',
    navigationController:'controller/navigation-controller',
    directives:'directives',
    commentBox:'directives/comment-box',
    formToolbar:'directives/form-toolbar',
    openlmisMessage:'directives/openlmis-message',
    openlmisPagination:'directives/openlmis-pagination',
    modalEvents:'directives/modal-events',
    placeholder:'directives/placeholder',
    tabScroll:'directives/tab-scroll',
    fixedTableHeader:'directives/fixed-table-header',
    uiNav:'directives/ui-nav',
    ngGrid:'../../lib/angular-ui/ng-grid/ng-grid-2.0.5.min',
    jQueryForm:'../../lib/jquery/jquery.form',
    select2:'../../lib/select2/select2',
    select2Ext:'select2-ext'
  },
  shim:{
    'angular':{deps:['jQuery']},
    'messageService':{deps:loadNext},
    'authorizationService':{deps:loadNext},
    'navigateBackService':{deps:loadNext},
    'loginController':{deps:loadNext},
    'headerController':{deps:loadNext},
    'navigationController':{deps:loadNext},
    'localStorageKeys':{deps:loadNext},
    'util':{deps:loadNext},
    'open-lmis-dialog':{deps:loadNext},
    'jQueryUi':{deps:loadFirst},
    'angularResource':{deps:loadFirst},
    'angularUi':{deps:loadFirst},
    'uiBootstrap':{deps:loadFirst},
    'bootstrap':{ deps:loadFirst},
    'localStorage':{deps:loadFirst},
    'services':{deps:loadFirst},
    'ngGrid':{deps:['angularResource']},
    'directives':{deps:['messageService'] },
    'commentBox':{deps:['directives']},
    'formToolbar':{deps:['directives']},
    'openlmisMessage':{ deps:['directives']},
    'openlmisPagination':{ deps:['directives']},
    'modalEvents':{ deps:['directives']},
    'placeholder':{ deps:['directives']},
    'tabScroll':{deps:['directives']},
    'fixedTableHeader':{deps:['directives']},
    'uiNav':{deps:['directives']},
    'jQueryForm':{deps:['jQuery']},
    'select2':{deps:['angularResource']},
    'select2Ext':{deps:['select2', 'angularResource']}
  }
});

define(["messageService", "authorizationService", "navigateBackService", "loginController", "headerController", "navigationController", "localStorageKeys", "util", "open-lmis-dialog",
  'commentBox', 'formToolbar', 'openlmisMessage', 'openlmisPagination', 'modalEvents', 'placeholder', 'tabScroll', 'fixedTableHeader', 'uiNav', 'ngGrid', 'jQueryForm', 'select2', 'select2Ext'], function () {

  var loadApp = function () {

    'use strict';

    /* App Module */
    var app = angular.module('openlmis', ['openlmis.services', 'openlmis.localStorage', 'ui.directives', 'openlmis.directives'], function ($routeProvider, $locationProvider, $httpProvider) {
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

    app.run(function ($rootScope, messageService) {
      messageService.populate();
      $rootScope.$on('$routeChangeStart', function () {
        angular.element('#ui-datepicker-div').hide();
        //TODO delete modal window
        angular.element('body > .modal-backdrop').hide();
        angular.element('.dialog').parent('.modal').remove();
      });
    });
  };

  var bootstrapApp = function () {
    loadApp();
    angular.bootstrap(document, ['openlmis']);
  }

  return {
    loadApp:loadApp,
    bootstrapApp:bootstrapApp
  };
});

function isUndefined(value) {
  return (value == null || value == undefined || value.toString().trim().length == 0);
}


