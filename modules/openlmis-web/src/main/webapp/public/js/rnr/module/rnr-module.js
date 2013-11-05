/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

var rnrModule = angular.module('rnr', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle', 'ui.bootstrap.dialog']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
      when('/init-rnr', {controller: InitiateRnrController, templateUrl: 'partials/create/init.html', resolve: InitiateRnrController.resolve}).
      when('/create-rnr/:rnr/:facility/:program', {controller: CreateRequisitionController, templateUrl: 'partials/create/index.html', resolve: CreateRequisitionController.resolve, reloadOnSearch: false}).
      when('/rnr-for-approval', {controller: ApproveRnrListController, templateUrl: 'partials/approve/list-for-approval.html', resolve: ApproveRnrListController.resolve}).
      when('/requisitions-for-convert-to-order', {controller: ConvertToOrderListController, templateUrl: 'partials/convert-to-order-list.html', reloadOnSearch: false}).
      when('/view-requisitions', {controller: ViewRnrListController, templateUrl: 'partials/view/index.html', resolve: ViewRnrListController.resolve}).
      when('/rnr-for-approval/:rnr/:program', {controller: ApproveRnrController, templateUrl: 'partials/approve/approve.html', resolve: ApproveRnrController.resolve, reloadOnSearch: false}).
      when('/requisition/:rnr/:program', {controller: ViewRnrController, templateUrl: 'partials/view/view.html', resolve: ViewRnrController.resolve, reloadOnSearch: false}).
      otherwise({redirectTo: '/init-rnr'});
  }]).directive('rnrValidator', function () {
    return {
      require: '?ngModel',
      link: function (scope, element, attrs, ctrl) {
        rnrModule[attrs.rnrValidator](element, ctrl, scope);
      }
    };
  });

rnrModule.positiveInteger = function (element, ctrl, scope) {
  element.bind('blur', function () {
    validationFunction(ctrl.$viewValue, element.attr('name'));
  });
  ctrl.$parsers.unshift(function (viewValue) {
    if (validationFunction(viewValue, element.attr('name'))) {
      if (viewValue === "")  viewValue = undefined;
      return viewValue;
    } else {
      ctrl.$viewValue = ctrl.$modelValue;
      ctrl.$render();
      return ctrl.$modelValue;
    }
  });
  function validationFunction(value, errorHolder) {
    var INTEGER_REGEXP = /^\d*$/;
    var valid = (value === undefined) ? true : INTEGER_REGEXP.test(value);
    if (errorHolder !== undefined) {
      document.getElementById(errorHolder).style.display = (valid) ? 'none' : 'block';
    }
    return valid;
  }
};

rnrModule.date = function (element, ctrl, scope) {

  var shouldSetError = element.attr('showError');

  scope.$watch(shouldSetError, function () {
    ctrl.setError = scope[shouldSetError];
    setTimeout(validationFunction, 0);
  });

  element.keyup(function () {
    if (isUndefined(ctrl.$viewValue)) document.getElementById(element.attr('name')).style.display = 'none';
  });
  element.bind('blur', validationFunction);

  element.bind('focus', function () {
    document.getElementById(element.attr('name')).style.display = 'none';
  });

  function validationFunction() {
    var DATE_REGEXP = /^(0[1-9]|1[012])[/]((2)\d\d\d)$/;
    var valid = (isUndefined(ctrl.$viewValue)) ? true : DATE_REGEXP.test(ctrl.$viewValue);

    var errorHolder = document.getElementById(element.attr('name'));
    errorHolder.style.display = (valid) ? 'none' : 'block';
    if (ctrl.setError)
      ctrl.$setValidity('date', valid);
  }
};

rnrModule.directive('adjustHeight', function ($timeout) {
  return {
    restrict: 'A',
    link: function (scope, element, attrs) {
      var adjustHeight = function () {
        element.css('height', 'auto');
        var referenceElement = $('.' + attrs.adjustHeight);

        if (element.height() > referenceElement.height()) return;

        element.css({height: referenceElement.height() + "px"});
      };

      $timeout(adjustHeight);
      $(window).on('resize', adjustHeight);
    }
  };
});


