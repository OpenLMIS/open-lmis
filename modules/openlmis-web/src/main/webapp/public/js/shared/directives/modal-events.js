/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

//  Description:
//  Bringing focus on the first field of modal upon open, restricting focus within the modal on tabbing

app.directive('modal', function() {
  return {
    restrict: 'EA',
    link: function(scope, elm, attrs) {
      var shownExpr = attrs.modal || attrs.show;

      scope.$watch(shownExpr, function(isShown, oldShown) {
        setTimeout(function() {
          if (isShown) {
            var tabbables = elm.find(":tabbable");

            tabbables.first().focus();

            tabbables.last().bind("keydown", function(e) {
              if (e.which == 9 && !e.shiftKey) {
                tabbables.first().focus();
                e.preventDefault();
              }
            });

            tabbables.first().bind("keydown", function(e) {
              if (e.which == 9 && e.shiftKey) {
                tabbables.last().focus();
                e.preventDefault();
              }
            });

          } else {
            var tabbables = elm.find(":tabbable");
            tabbables.last().unbind("keydown");
            tabbables.first().unbind("keydown");
          }
        });

      });
    }
  };
});
