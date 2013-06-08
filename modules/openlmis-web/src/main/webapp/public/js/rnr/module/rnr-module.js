/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
require(['../../shared/app', '../controller/initiate-rnr-controller','../controller/create-requisition-controller',
  '../controller/create-full-supply-controller','../controller/approve/approve-rnr-list-controller',
  '../controller/create-non-full-supply-controller','../controller/approve/approve-rnr-list-controller',
  '../controller/approve/approve-rnr-controller','../controller/approve/approve-full-supply-controller',
  '../controller/approve/approve-non-full-supply-controller','../controller/convert-to-order-list-controller',
  '../controller/view/view-rnr-list-controller','../controller/view/view-rnr-controller',
  '../controller/view/view-full-supply-controller','../controller/view/view-non-full-supply-controller',
  '../model/rnr-line-item','../model/loss-and-adjustment',
  '../model/rnr']
  , function (app) {
    app.loadApp();
    var rnrModule = angular.module('rnr', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle', 'ui.bootstrap.dialog']).config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
          when('/init-rnr', {controller: InitiateRnrController, templateUrl: 'partials/create/init.html', resolve: InitiateRnrController.resolve}).
          when('/create-rnr/:rnr/:facility/:program', {controller: CreateRequisitionController, templateUrl: 'partials/create/index.html', resolve: CreateRequisitionController.resolve, reloadOnSearch: false}).
          when('/rnr-for-approval', {controller: ApproveRnrListController, templateUrl: 'partials/approve/list-for-approval.html', resolve: ApproveRnrListController.resolve}).
          when('/requisitions-for-convert-to-order', {controller: ConvertToOrderListController, templateUrl: 'partials/convert-to-order-list.html', resolve: ConvertToOrderListController.resolve}).
          when('/view-requisitions', {controller: ViewRnrListController, templateUrl: 'partials/view/index.html', resolve: ViewRnrListController.resolve}).
          when('/rnr-for-approval/:rnr/:program', {controller: ApproveRnrController, templateUrl: 'partials/approve/approve.html', resolve: ApproveRnrController.resolve, reloadOnSearch: false}).
          when('/requisition/:rnr/:program', {controller: ViewRnrController, templateUrl: 'partials/view/view.html', resolve: ViewRnrController.resolve, reloadOnSearch: false}).
          otherwise({redirectTo: '/init-rnr'});
      }]).directive('rnrValidator',function () {
        return {
          require: '?ngModel',
          link: function (scope, element, attrs, ctrl) {
            var validationFunction = rnrModule[attrs.rnrValidator];

            element.bind('blur', function () {
              validationFunction(ctrl.$modelValue, element.attr('name'));
            });
            ctrl.$parsers.unshift(function (viewValue) {
              if (validationFunction(viewValue, element.attr('name'))) {
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
      }).run(function ($rootScope) {
        $rootScope.pageSize = 20;
      });

    angular.bootstrap(document, ['rnr']);

    //TODO: remove name to id mapping
    rnrModule.positiveInteger = function (value, errorHolder) {
      var INTEGER_REGEXP = /^\d*$/;
      var valid = (value == undefined) ? true : INTEGER_REGEXP.test(value);

      if (errorHolder != undefined) {
        document.getElementById(errorHolder).style.display = (valid) ? 'none' : 'block';
      }

      return valid;
    };

    function parseIntWithBaseTen(number) {
      return parseInt(number, 10);
    }

  });


