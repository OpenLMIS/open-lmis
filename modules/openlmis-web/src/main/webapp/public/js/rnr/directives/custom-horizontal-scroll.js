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

app.directive('customHorizontalScroll', function () {
  return {
    restrict: 'A',
    link: function (scope, element, attrs) {
      var myScroll = {};
      var scrollBar = $("<div class='scrollbar'><div class='scroll-handle'></div></div>");
      myScroll.scrollHandle = scrollBar.find('.scroll-handle');
      element.append(scrollBar);

      function setupScroll() {
        myScroll.containerWidth = element.outerWidth();
        myScroll.scrollableWidth = element.find('.scrollable').css("display") != "none" ? element.find('.scrollable').first().outerWidth() : 0;

        myScroll.extraWidth = myScroll.scrollableWidth - myScroll.containerWidth;

        if (myScroll.extraWidth > 0) {
          scrollBar.show();
          scrollBar.width(myScroll.containerWidth);
          myScroll.multiplier = Math.floor(myScroll.extraWidth / myScroll.containerWidth) + 1;

          myScroll.scrollHandle.width(myScroll.containerWidth - (myScroll.extraWidth / myScroll.multiplier));

          myScroll.handleStartOffset = scrollBar.offset().left;
          myScroll.handleEndOffset = myScroll.containerWidth - myScroll.scrollHandle.width() + myScroll.handleStartOffset;

          var scrollHandlePosition = element.scrollLeft() / myScroll.multiplier + myScroll.handleStartOffset;
          myScroll.scrollHandle.offset({left: scrollHandlePosition});

          myScroll.scrollHandle.mousedown(function (e) {
            e.preventDefault();
            var handleLeftPosition = myScroll.scrollHandle.offset().left;
            var elementLeftPosition = element.scrollLeft();
            var initialMouseX = e.pageX;

            $('body').mousemove(function (e) {
              var finalMouseX = e.pageX;
              var diff = finalMouseX - initialMouseX;

              if (myScroll.scrollHandle.offset().left >= myScroll.handleStartOffset) {
                myScroll.scrollHandle.offset({left: handleLeftPosition + diff });

                if (myScroll.scrollHandle.offset().left < myScroll.handleStartOffset) {
                  myScroll.scrollHandle.offset({left: myScroll.handleStartOffset});
                }
                  if (myScroll.scrollHandle.offset().left > myScroll.handleEndOffset) {
                  myScroll.scrollHandle.offset({left: myScroll.handleEndOffset});
                }

                element.scrollLeft(elementLeftPosition + (diff * myScroll.multiplier));
              }
            });
          });

          $(document).mouseup(function (e) {
            $('body').unbind('mousemove');
          });

          element.scroll(elementScrollHandler);

          $('.rnr-body').scroll(pageScrollHandler);

          if (getContainerBottomOffset() > 0) {
            scrollBar.hide();
          } else {
            scrollBar.show();
          }

        } else {
          scrollBar.hide();
        }
      }

      function elementScrollHandler() {
        var scrollHandlePosition = element.scrollLeft() / myScroll.multiplier + myScroll.handleStartOffset;
        scrollBar.find('.scroll-handle').offset({left: scrollHandlePosition});
      }

      function pageScrollHandler() {
        if (getContainerBottomOffset() > 0) {
          scrollBar.hide();
        } else {
          resetScroll();
        }
      }

      function getContainerBottomOffset() {
        var distance = $(window).height() - (element.offset().top + element.outerHeight()) - 90;
        return distance;
      }

      setTimeout(setupScroll, 0);

      function resetScroll() {
        myScroll.scrollHandle.unbind();
        $(document).unbind('mouseup');
        element.unbind("scroll", elementScrollHandler);
        $('.rnr-body').unbind("scroll", pageScrollHandler);

        setupScroll();
      }

      $(window).on('resize', resetScroll);
    }
  };
});
