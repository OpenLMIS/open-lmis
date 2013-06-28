/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
    }
  };
});