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
//  Freezing the top column of R&R products table upon scroll

app.directive('fixedTableHeader', function() {
  return {
    restrict: 'EA',
    link: function (scope, element, attr, ctrl) {
      var fixedHeader = $("<div class='header-fixed'></div>");
      fixedHeader.hide();
      setTimeout(function() {
        var table = $("<table class='table table-bordered'></table>");
        table.append(element.find('thead').clone());
        fixedHeader.append(table);
        element.parent().append(fixedHeader);
        fixedHeader.css('width',element.parent().css('width'));
      });

      scope.$watch($(window).scrollTop(), function() {
        angular.element(window).scroll(function() {
          fixedHeader.hide();
          if (angular.element(this).scrollTop() >= element.offset().top && !element.is(':hidden') && fixedHeader.is(':hidden')) {
           fixedHeader.show();
           fixedHeader.scrollLeft(element.parent().scrollLeft());
          }
        });

        element.parent().scroll(function() {
          fixedHeader.scrollLeft(angular.element(this).scrollLeft());
        });
      });

      $(window).on('resize', function() {
        fixedHeader.css('width',element.parent().css('width'));
      });
    }
  };
});