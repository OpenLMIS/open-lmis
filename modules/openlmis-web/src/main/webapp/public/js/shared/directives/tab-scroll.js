/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

directives.directive('tabScroll', function () {
  return {
    restrict: 'A',

    link: function (scope, element, attrs) {
      setTimeout(function () {
        $(element).find(":tabbable").on('focus', function () {
          var distance = $(window).height() - ($(this).offset().top - $(window).scrollTop());
          var offset = 0 - distance;
          var bottomOffset = attrs.bottomOffset || 100;
          var scrollAmount = parseInt(bottomOffset + offset);

          if (distance < 120) {
            $(window).scrollTop($(window).scrollTop() + scrollAmount);
          }
        });
      });
    }
  };
});