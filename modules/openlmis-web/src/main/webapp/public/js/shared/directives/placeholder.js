/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

//  Description:
//  Emulating the placeholder attribute for older browsers

app.directive('placeholder',function () {
  return {
    restrict:'A',
    require:'ngModel',
    link:function (scope, element, attr, ctrl) {
      var value;

      if (!jQuery.support.placeholder) {
        var placeholder = function () {
          if (ctrl.$modelValue != '') {
            ctrl.$modelValue = undefined;
            ctrl.$viewValue = attr.placeholder;
          }
          ctrl.$render();
          element.css("color", "#a2a2a2");
        };
        var unPlaceholder = function () {
          ctrl.$viewValue = undefined;
          element.css("color", "");
          ctrl.$render();
        };

        scope.$watch(attr.ngModel, function (val) {
          if (val == attr.placeholder)   val = '';
          value = val || '';
        });

        element.bind('focus', function () {
          if (value == '' || (ctrl.$modelValue == undefined)) unPlaceholder();
        });

        element.bind('blur', function () {
          if (element.val() == '') {
            placeholder();
          }
        });

        ctrl.$formatters.unshift(function (val) {
          if (!val || (val == attr.placeholder)) {
            placeholder();
            value = '';
            return attr.placeholder;
          }
          return val;
        });
      }
    }
  };
});


jQuery.support.placeholder = !!function () {
  return "placeholder" in document.createElement("input");
}();