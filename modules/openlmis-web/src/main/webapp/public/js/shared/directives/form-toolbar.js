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
//  1. Fixing the width of the translucent toolbar (Submit, Cancel, etc) at the bottom of some forms
//  2. Fixing the height of the white content box to take up minimum screen height
//  3. Updating above 2 on window resize

app.directive('formToolbar',function () {
  return {
    restrict:'A',
    link:function (scope, element, attrs) {
      setTimeout(fixToolbarWidth, 100);
      $(window).on('resize', fixToolbarWidth);
      scope.$watch(function() {
        return $('.content').width();
      }, function(newValue, oldValue) {
        if(newValue !== oldValue) {
          fixToolbarWidth();
        }
      });
    }
  };
});

var fixToolbarWidth = function() {
  var toolbarWidth = $(document).width() - 26;
  angular.element("#action_buttons").css("width", toolbarWidth + "px");
  angular.element("#podFooter").css("width", (toolbarWidth - 28) + "px");

  var headerHeight = $(".header").outerHeight(true);
  var navHeight = $(".navigation").outerHeight(true);
  var content = $(".content");
  var contentGutterHeight = content.outerHeight(true) - content.height();

  content.css('min-height', $(window).height() - (headerHeight + navHeight + contentGutterHeight));
};