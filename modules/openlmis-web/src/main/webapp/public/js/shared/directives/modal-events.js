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
//  Bringing focus on the first field of modal upon open, restricting focus within the modal on tabbing

app.directive('modal', function () {
  return {
    restrict: 'AC',
    link: function (scope, elm, attrs) {
      var shownExpr = attrs.modal;

      if (attrs.show)
        shownExpr = '[' + shownExpr + ', ' + attrs.show + ']';

      scope.$watch(shownExpr, function (isShown, oldShown) {
        var tabbables;
        var backdrop;
        var focusTabbableFirstChild = function (e) {
          tabbables.first().focus();
        };

        setTimeout(function () {
          if (isShown) {
            tabbables = elm.find(":tabbable");
            backdrop = angular.element("body .modal-backdrop");

            tabbables.first().focus();

            tabbables.last().bind("keydown", function (e) {
              if (e.which == 9 && !e.shiftKey) {
                tabbables.first().focus();
                e.preventDefault();
              }
            });

            tabbables.first().bind("keydown", function (e) {
              if (e.which == 9 && e.shiftKey) {
                tabbables.last().focus();
                e.preventDefault();
              }
            });

            backdrop.bind("click", focusTabbableFirstChild);

          } else {
            tabbables = elm.find(":tabbable");
            backdrop = angular.element("body .modal-backdrop");

            tabbables.last().unbind("keydown");
            tabbables.first().unbind("keydown");
            backdrop.unbind("click", focusTabbableFirstChild);
          }
        });

      }, true);
    }
  };
});
