/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

rnrModule.directive('adjustHeight', function ($timeout) {
  return {
    restrict: 'A',
    link: function (scope, element, attrs) {
      var previousWindowWidth = window.innerWidth;
      var timeoutId;
      var adjustHeight = function () {
        $timeout.cancel(timeoutId);
        timeoutId = $timeout(function () {
          if (element.is(':hidden')) return;
          element.css('height', 'auto');
          var referenceElement = $('.' + attrs.adjustHeight + ':visible');

          if (element.height() > referenceElement.height()) return;

          element.css({height: referenceElement.height() + "px"});
        });
      };

      $(window).on('resize', function () {
        if (previousWindowWidth !== window.innerWidth) {
          adjustHeight();
        }
      });
      scope.$watch('visibleTab', adjustHeight);
    }
  };
});