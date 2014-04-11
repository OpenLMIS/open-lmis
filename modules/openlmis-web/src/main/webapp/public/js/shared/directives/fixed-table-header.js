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
//  Freezing the top header of R&R products table upon scroll

app.directive('fixedTableHeader', function ($timeout, $compile) {
  return {
    restrict: 'EA',
    link: function (scope, element) {
      var windowElement = angular.element(window);
      var parentElement = element.closest('.parent');
      var fixedHeader = $("<div class='header-fixed'></div>");
      fixedHeader.hide();

      var cloneAndAppendTableHeader = function () {
        var table = $("<table class='table table-bordered'></table>");
        table.append(element.find('thead').clone());
        fixedHeader.append(table);
        element.parent().append(fixedHeader);
        fixedHeader = $compile(fixedHeader)(scope);
      };

      var removeLinksFromFixedHeader = function () {
        fixedHeader.find('a').parent().remove();
      };

      $timeout(function () {
        cloneAndAppendTableHeader();
        removeLinksFromFixedHeader();
        parentElement.scroll(function () {
          fixedHeader.scrollLeft(parentElement.scrollLeft());
        });
        windowElement.scrollTop(0); //Reset scroll on page refresh in firefox
      });

      function setWidthAndHeightFromParent() {
        var setFixedHeaderWidth = function() {
          fixedHeader.width(parentElement.width());
        };

        var setFixedHeaderRowCellsWidth = function(fixedRow, parentRow) {
          var fixedRowHeadCells = $(fixedRow).find('th').toArray();
          var parentRowHeadCells = parentRow.find('th').toArray();
          fixedRowHeadCells.forEach(function(fixedTH, thIndex) {
            var parentTH = $(parentRowHeadCells[thIndex]);
            $(fixedTH).width(parentTH.width());
            $(fixedTH).css('min-width', parentTH.width());
          });
        };

        var setFixedHeaderRowsWidth = function() {
          var fixedHeaderRows = fixedHeader.find('tr').toArray();
          var parentHeaderRows = parentElement.find('tr').toArray();


          fixedHeaderRows.forEach(function(fixedRow, rowIndex) {
            var parentRow = $(parentHeaderRows[rowIndex]);
            $(fixedRow).width(parentRow.width());
            $(fixedRow).css('min-width', parentRow.width());

            setFixedHeaderRowCellsWidth(fixedRow, parentRow);

          });
        };

        setFixedHeaderWidth();
        setFixedHeaderRowsWidth();
      }

      var fixedHeaderHidden = true;
      windowElement.scroll(function () {
        if (element.is(':hidden'))
          return;
        if ((element.offset().top - windowElement.scrollTop()) < 0) {
          if (!fixedHeaderHidden)
            return;
          setWidthAndHeightFromParent();
          fixedHeader.show();
          fixedHeader.scrollLeft(parentElement.scrollLeft());
          fixedHeaderHidden = false;
          return;
        }
        if (!fixedHeaderHidden) {
          fixedHeader.hide();
          fixedHeaderHidden = true;
        }
      });

      $(window).on('resize', function () {
        setWidthAndHeightFromParent();
      });
    }
  };
});
