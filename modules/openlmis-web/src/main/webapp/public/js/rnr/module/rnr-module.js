/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

var rnrModule = angular.module('rnr', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle']).config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/init-rnr', {controller:InitiateRnrController, templateUrl:'partials/init.html', resolve: InitiateRnrController.resolve}).
    when('/create-rnr/:rnr/:facility/:program', {controller:CreateRequisitionController, templateUrl:'partials/create.html', resolve:CreateRequisitionController.resolve, reloadOnSearch:false}).
    when('/rnr-for-approval', {controller:ApproveRnrListController, templateUrl:'partials/list.html', resolve:ApproveRnrListController.resolve}).
    when('/requisitions-for-convert-to-order', {controller:ConvertToOrderListController, templateUrl:'partials/convert-to-order-list.html', resolve:ConvertToOrderListController.resolve}).
    when('/view-requisitions', {controller:ViewRnrListController, templateUrl:'partials/view-rnr.html', resolve:ViewRnrListController.resolve}).
    when('/rnr-for-approval/:rnr/:program', {controller:ApproveRnrController, templateUrl:'partials/approve.html', resolve:ApproveRnrController.resolve, reloadOnSearch:false}).
    when('/requisition/:rnr/:program', {controller:ViewRnrController, templateUrl:'partials/view.html', resolve: ViewRnrController.resolve, reloadOnSearch:false}).
    otherwise({redirectTo:'/init-rnr'});
}]).directive('rnrValidator',function () {
    return {
      require:'?ngModel',
      link:function (scope, element, attrs, ctrl) {
        var validationFunction = rnrModule[attrs.rnrValidator];

        element.bind('blur', function () {
          var viewValue = ctrl.$viewValue;
          validationFunction(viewValue, element.attr('name'));
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

rnrModule.positiveInteger = function (value, errorHolder) {
  var toggleErrorMessageDisplay = function (valid, errorHolder) {
    if (valid) {
      document.getElementById(errorHolder).style.display = 'none';
    } else {
      document.getElementById(errorHolder).style.display = 'block';
    }
  };

  var INTEGER_REGEXP = /^\d*$/;
  var valid = (value == undefined) ? true : INTEGER_REGEXP.test(value);

  if (errorHolder != undefined) toggleErrorMessageDisplay(valid, errorHolder);

  return valid;
};

function parseIntWithBaseTen(number) {
  return parseInt(number, 10);
}

