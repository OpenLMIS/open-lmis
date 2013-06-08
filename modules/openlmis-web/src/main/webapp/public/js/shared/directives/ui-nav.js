/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

directives.directive('uiNav', function () {
  return {
    restrict: 'A',

    link: function (scope, element, attrs) {
      //Identify all the menu lists
      var lists = $(".navigation ul");

      //Sort the lists based their nesting, innermost to outermost
      lists.sort(function (a, b) {
        return $(b).parents("ul").length - $(a).parents("ul").length;
      });

      setTimeout(function () {

        lists.each(function () {
          var display = false;

          //Check if all the child items are hidden
          $(this).children("li:not(.beak)").each(function () {
            if ($(this).css('display') != 'none') {
              display = true;
              return false;
            }
          });
          //Hide the list and its containing li in case all the children are hidden
          if (!display) {
            $(this).parent().hide();
            $(this).parent().parent().hide();
          }
        });

        $(".navigation li > a").on("click", function () {
          $(this).next(".submenu").show();
        });
      });
    }
  };
});