/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

directives.directive('openlmisMessage', function (messageService) {
  return {
    restrict: 'A',
    link: function (scope, element, attrs) {
      var key = scope[attrs.openlmisMessage] || attrs.openlmisMessage;
      var keyWithArgs = key.split("|");
      scope.$watch("[" + keyWithArgs.toString() + "]", function () {
        var key = scope[keyWithArgs[0]] || keyWithArgs[0];
        var displayMessage = messageService.get(key) || key;
        if (!isUndefined(keyWithArgs) && keyWithArgs.length > 1) {
          displayMessage = replaceArgs(scope, displayMessage, keyWithArgs);
        }
        element[0].localName == "input" ? element.attr("value", displayMessage) : element.html(displayMessage);
      }, true);
    }
  }
});
