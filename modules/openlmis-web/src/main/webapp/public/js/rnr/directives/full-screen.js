/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

app.directive('fullScreen', function () {
  return {
    restrict: 'A',
    link: function (scope, element) {
      var fullScreen = false;

      var progressFunc = function () {
        fixToolbarWidth();
        $('.rnr-body').trigger('scroll');
      };
      var completeFunc = function () {
        $('.rnr-body').trigger('scroll');
      };

      element.click(function () {
        fullScreen = !fullScreen;
        element.find('i').toggleClass('icon-resize-full icon-resize-small');
        angular.element(window).scrollTop(0);
        if (!$.browser.msie) {
          fullScreen ? angular.element('.toggleFullScreen').slideUp({'duration': 'slow', 'progress': progressFunc, complete: completeFunc}) :
            angular.element('.toggleFullScreen').slideDown({ 'duration': 'slow', 'progress': progressFunc, complete: completeFunc});
        }
        else {
          fullScreen ? angular.element('.toggleFullScreen').hide() : angular.element('.toggleFullScreen').show();
          $('.rnr-body').trigger('scroll');
        }
        fullScreen ? angular.element('.print-button').css('opacity', '1.0') : angular.element('.print-button').css('opacity', '0');
      });
    }
  };
});