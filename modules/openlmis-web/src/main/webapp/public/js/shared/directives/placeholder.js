/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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
          if (ctrl.$modelValue !== '') {
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
          if (val === attr.placeholder)   val = '';
          value = val || '';
        });

        element.bind('focus', function () {
          if (value === '' || (ctrl.$modelValue === undefined)) unPlaceholder();
        });

        element.bind('blur', function () {
          if (element.val() === '') {
            placeholder();
          }
        });

        ctrl.$formatters.unshift(function (val) {
          if (!val || (val === attr.placeholder)) {
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