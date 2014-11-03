/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

rnrModule.directive('adjustHeight', function ($timeout) {
  return {
    restrict: 'A',
    link: function (scope, element) {
      var previousWindowWidth = window.innerWidth;
      var timeoutId;
      var adjustHeight = function () {
        $timeout.cancel(timeoutId);
        timeoutId = $timeout(function () {
          if (element.is(':hidden')) return;

          var leftTable = $(element).find('.left-table');
          var rightTable = $(element).find('.right-table');

          var fixHeightOfTh = function () {
            var leftThHeight = leftTable.find('th:first-child').height();
            var rightThHeight = rightTable.find('th:first-child').height();
            if (leftThHeight > rightThHeight) {
              $(element).find('.right-table th').css({
                height: leftThHeight + "px"
              });
            } else {
              leftTable.find('th').css({
                height: rightThHeight + "px"
              });
            }
          };

          var fixHeightOfTd = function () {
            var leftTableBody = leftTable.find('tbody');
            var rightTableBody = rightTable.find('tbody');
            leftTableBody.find('tr').each(function (index) {
              if ($(this).find('td:first-child').height() > rightTableBody.find('tr').eq(index).find('td:first-child').height()) {
                rightTableBody.find('tr').eq(index).find('td').css({
                  height: leftTableBody.find('tr').eq(index).find('td:first-child').height() + "px"
                });
              } else {
                leftTableBody.find('tr').eq(index).find('td').css({
                  height: rightTableBody.find('tr').eq(index).find('td:first-child').height() + "px"
                });
              }
            });
          };

          var setHeightOfTdAndTh = function () {
            fixHeightOfTh();
            fixHeightOfTd();
          };
          setHeightOfTdAndTh();

        });
      };

      window.adjustHeight = adjustHeight;

      $(window).on('resize', function () {
        if (previousWindowWidth !== window.innerWidth) {
          adjustHeight();
        }
      });
      scope.$watch('page[visibleTab]', adjustHeight);
    }
  };
});