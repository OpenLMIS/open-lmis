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
//  Freezing the top two columns of header of R&R products table upon scroll

app.directive('fixedTableHeaderColumns', function() {
  return {
    restrict: 'A',
    link: function (scope, element, attr, ctrl) {
      var fixedHeaderColumns = $("<div id='header-columns-fixed' class='header-fixed'></div>");
      fixedHeaderColumns.hide();

      var cloneAndAppendTableHeaderColumns = function() {
        var table = $("<table class='table table-bordered'><thead></thead></table>");

        var firstTableCell = $(element.find('thead th')[0]).clone();
        copyWidthAndHeight($(element.find('thead th')[0]), firstTableCell);

        var secondTableCell = $(element.find('thead th')[1]).clone();
        copyWidthAndHeight($(element.find('thead th')[1]), secondTableCell);

        table.append(firstTableCell);
        table.append(secondTableCell);
        fixedHeaderColumns.append(table);
        element.parent().append(fixedHeaderColumns);
      }

      setTimeout(function() {
        cloneAndAppendTableHeaderColumns();
      });

      scope.$watch($(window).scrollTop(), function() {
        viewFixedHeaderOnScroll(fixedHeaderColumns, element);
      });

    }
  };
});
