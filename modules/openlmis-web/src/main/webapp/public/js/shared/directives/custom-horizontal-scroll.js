/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

//  Description:
//  Adding custom horizontal scroll

app.directive('customHorizontalScroll',function () {
  return {
    restrict:'A',
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
          myScroll.multiplier = Math.floor(myScroll.extraWidth/myScroll.containerWidth) + 1;

          myScroll.scrollHandle.width(myScroll.containerWidth - (myScroll.extraWidth/myScroll.multiplier));

          myScroll.handleStartOffset = scrollBar.offset().left;
          myScroll.handleEndOffset = myScroll.containerWidth - myScroll.scrollHandle.width() + myScroll.handleStartOffset;

          var scrollHandlePosition = element.scrollLeft()/myScroll.multiplier + myScroll.handleStartOffset;
          myScroll.scrollHandle.offset({left: scrollHandlePosition});

          myScroll.scrollHandle.mousedown(function(e) {
            e.preventDefault();
            var handleLeftPosition = myScroll.scrollHandle.offset().left;
            var elementLeftPosition = element.scrollLeft();
            var initialMouseX = e.pageX;

            $('body').mousemove(function(e) {
              var finalMouseX = e.pageX;
              var diff = finalMouseX - initialMouseX;

              if(myScroll.scrollHandle.offset().left >= myScroll.handleStartOffset) {
                myScroll.scrollHandle.offset({left: handleLeftPosition + diff });

                if(myScroll.scrollHandle.offset().left < myScroll.handleStartOffset) {
                  myScroll.scrollHandle.offset({left: myScroll.handleStartOffset});
                }
                if(myScroll.scrollHandle.offset().left > myScroll.handleEndOffset) {
                  myScroll.scrollHandle.offset({left: myScroll.handleEndOffset});
                }

                element.scrollLeft(elementLeftPosition + (diff * myScroll.multiplier));
              }
            });
          });

          $(document).mouseup(function(e) {
            $('body').unbind('mousemove');
          });

          element.scroll(elementScrollHandler);

          $(window).scroll(pageScrollHandler);

          if(getContainerBottomOffset() > 0) {
            scrollBar.hide();
          } else {
            scrollBar.show();
          }

        } else {
          scrollBar.hide();
        }
      }

      function elementScrollHandler() {
        var scrollHandlePosition = element.scrollLeft()/myScroll.multiplier + myScroll.handleStartOffset;
        scrollBar.find('.scroll-handle').offset({left: scrollHandlePosition});
      }

      function pageScrollHandler() {
        if(getContainerBottomOffset() > 0) {
          scrollBar.hide();
        } else {
          resetScroll();
        }
      }

      function getContainerBottomOffset() {
        var distance = $(window).height() - (element.offset().top + element.outerHeight() - $(window).scrollTop()) - 90;
        return distance;
      }

      setTimeout(setupScroll);

      function resetScroll() {
        myScroll.scrollHandle.unbind();
        $(document).unbind('mouseup');
        element.unbind("scroll", elementScrollHandler);
        $(window).unbind("scroll", pageScrollHandler);

        setupScroll();
      }

      $(window).on('resize', resetScroll);
    }
  };
});
