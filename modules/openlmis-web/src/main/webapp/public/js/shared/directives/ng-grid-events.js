/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

//  Description:
//  Bringing focus on the first field of modal upon open, restricting focus within the modal on tabbing

app.directive('ngGrid', function() {
  return {
    restrict: 'EA',
    link: function(scope, elm, attrs) {
      var shownExpr = attrs.ngShow;

      scope.$watch(shownExpr, function(isShown, oldShown) {
        if(isShown) {
          $(window).trigger("resize");
        }
      });
    }
  };
});
