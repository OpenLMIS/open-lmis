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

app.directive('fixedTableHeader', function() {
  return {
    restrict: 'EA',
    link: function (scope, element, attr, ctrl) {
      var fixedHeader = $("<div class='header-fixed'></div>");
      fixedHeader.hide();

      var cloneAndAppendTableHeader = function() {
        var table = $("<table class='table table-bordered'></table>");
        table.append(element.find('thead').clone());
        fixedHeader.append(table);
        element.parent().append(fixedHeader);
      }
      setTimeout(function() {
        cloneAndAppendTableHeader();
        fixedHeader.css('width',element.parent().css('width'));
      });

      scope.$watch($(window).scrollTop(), function() {
        viewFixedHeaderOnScroll(fixedHeader, element);
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

var viewFixedHeaderOnScroll = function(fixedHeaderElement, element) {
  angular.element(window).scroll(function() {
    fixedHeaderElement.hide();
    if (angular.element(this).scrollTop() >= element.offset().top && !element.is(':hidden') && fixedHeaderElement.is(':hidden')) {
      fixedHeaderElement.show();
      fixedHeaderElement.scrollLeft(element.parent().scrollLeft());
    }
  });
}