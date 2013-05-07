/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

app.directive('formToolbar',function () {
  return {
    restrict:'A',
    link:function (scope, element, attrs) {

      function fixToolbarWidth() {
        var toolbarWidth = $(document).width() - 26;
        angular.element("#action_buttons").css("width", toolbarWidth + "px");

        var headerHeight = $(".header").outerHeight(true);
        var navHeight = $(".navigation").outerHeight(true);
        var content = $(".content");
        var contentGutterHeight = content.outerHeight(true) - content.height();

        content.css('min-height', $(window).height() - (headerHeight + navHeight + contentGutterHeight));
      }

      fixToolbarWidth();
      $(window).on('resize', fixToolbarWidth);

    }
  };
});