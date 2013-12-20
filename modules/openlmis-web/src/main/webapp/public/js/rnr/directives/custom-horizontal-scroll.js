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
//  Adding custom horizontal scroll

app.directive('customHorizontalScroll', function ($timeout) {
  return {
    restrict: 'A',
    link: function (scope, element) {
      var scrollBar = {};
      var container = element;
      container.scrollableElement = container.find('.scrollable');
      scrollBar.track = $("<div class='scrollbar'></div>");
      scrollBar.handle = $("<div class='scroll-handle'></div>");
      scrollBar.track.append(scrollBar.handle);
      container.append(scrollBar.track);

      container.setScrollableWidth = function () {
        container.scrollableElement.width = container.scrollableElement.is(":visible") ? container.scrollableElement.outerWidth() : 0;
        container.scrollableWidth = container.scrollableElement.width - container.outerWidth();
      };

      container.syncWithScrollBarHandle = function () {
        var containerScrollAmount = scrollBar.handle.moveAmount * scrollBar.scrollMultiplier;
        this.scrollLeft(this.leftPosition + containerScrollAmount);
      };

      container.getBottomOffset = function () {
        return $(window).height() - (container.offset().top + container.outerHeight() - $(window).scrollTop()) - 90;
      };

      scrollBar.track.setWidth = function () {
        this.width(container.outerWidth());
      };

      scrollBar.handle.setWidth = function () {
        scrollBar.scrollMultiplier = Math.floor(container.scrollableElement.width / scrollBar.track.width());
        scrollBar.track.widthToBeScrolled = container.scrollableWidth / scrollBar.scrollMultiplier;
        this.width(scrollBar.track.width() - scrollBar.track.widthToBeScrolled);
      };

      scrollBar.handle.setScrollRange = function () {
        this.startX = scrollBar.track.offset().left;
        this.endX = this.startX + scrollBar.track.widthToBeScrolled;
      };

      scrollBar.handle.setLeftPosition = function (leftPosition) {
        this.offset({left: leftPosition});
      };

      scrollBar.handle.syncWithScrollableElement = function () {
        var handleScrollAmount = container.scrollLeft() / scrollBar.scrollMultiplier;
        scrollBar.handle.setLeftPosition(scrollBar.handle.startX + handleScrollAmount);
      };

      scrollBar.handle.moveOnScroll = function () {
        if (this.offset().left >= this.startX) {
          this.setLeftPosition(this.offset().left + this.moveAmount);

          if (this.offset().left < this.startX) {
            this.setLeftPosition(this.startX);
          }

          if (this.offset().left > this.endX) {
            this.setLeftPosition(this.endX);
          }
          container.syncWithScrollBarHandle();
        }
      };

      function setupAndShowScrollBar() {
        scrollBar.track.setWidth();
        scrollBar.handle.setWidth();
        scrollBar.handle.setScrollRange();
        scrollBar.handle.syncWithScrollableElement();
        scrollBar.track.show();
      }

      function pageScrollHandler() {
        if (container.getBottomOffset() > 0) {
          scrollBar.track.hide();
        } else {
          resetScroll();
        }
      }

      function startScroll() {
        container.setScrollableWidth();
        if (container.scrollableWidth > 0) {
          setupAndShowScrollBar();

          container.scroll(scrollBar.handle.syncWithScrollableElement);

          scrollBar.handle.mousedown(function (e) {
            e.preventDefault();
            scrollBar.handle.leftPosition = scrollBar.handle.offset().left;
            container.leftPosition = container.scrollLeft();
            var initialMouseX = e.pageX;

            $('body').mousemove(function (e) {
              var finalMouseX = e.pageX;
              scrollBar.handle.moveAmount = finalMouseX - initialMouseX;
              scrollBar.handle.moveOnScroll();
            });
          });

          $(document).mouseup(function () {
            $('body').unbind('mousemove');
          });

          $(window).scroll(pageScrollHandler);

          if (container.getBottomOffset() > 0) {
            scrollBar.track.hide();
          } else {
            scrollBar.track.show();
          }

        } else {
          scrollBar.track.hide();
        }
      }

      function unbindAllEvents() {
        scrollBar.handle.unbind();
        $(document).unbind('mouseup');
        container.unbind("scroll", scrollBar.handle.syncWithScrollableElement);
        $(window).unbind("scroll", pageScrollHandler);
      }

      function resetScroll() {
        unbindAllEvents();
        startScroll();
      }

      $timeout(startScroll);
      $(window).on('resize', resetScroll);
    }
  };
});
