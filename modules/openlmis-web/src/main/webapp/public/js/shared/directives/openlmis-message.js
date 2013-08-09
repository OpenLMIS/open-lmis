/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

//  Description:
//  Including the text from the messages service

app.directive('openlmisMessage', function (messageService) {
  return {
    restrict: 'A',
    link: function (scope, element, attrs) {
      var keyWithArgs = attrs.openlmisMessage.split("|");

      function useExternalisedMessage(displayMessage) {
        var children = element.children();
        if (element[0].localName == 'textarea' || element[0].localName == 'select') {
          element.attr('placeholder', displayMessage);
          return;
        }

        switch (element.attr('type')) {
          case 'button':
          case 'submit':
            element.attr("value", displayMessage);
            break;
          case 'text':
          case 'password':
            element.attr('placeholder', displayMessage);
            break;

          default:
            element.html(displayMessage).append(children);
            if (element.hasClass('welcome-message')) {       // Adding same title as the HTML content to the welcome message on home page
              element.attr('title', displayMessage);
            }
            break;
        }
      }

      var refreshMessages = function () {
        var evaluatedVariable = scope;
        var existsInScope = true;
        $(keyWithArgs[0].split('.')).each(function(index, arg) {
          evaluatedVariable = evaluatedVariable[arg];
          if(!evaluatedVariable) {
            existsInScope = false;
            return false;
          }
          return true;
        });

        var key = existsInScope ? evaluatedVariable : keyWithArgs[0];
        var displayMessage = messageService.get(key) || key;
        if (!isUndefined(keyWithArgs) && keyWithArgs.length > 1) {
          displayMessage = replaceArgs(scope, displayMessage, keyWithArgs);
        }
        useExternalisedMessage(displayMessage);
      };

      scope.$watch("[" + keyWithArgs.toString() + "]", refreshMessages, true);
      scope.$on('messagesPopulated', refreshMessages);

      function replaceArgs(scope, displayMessage, args) {
        $.each(args, function (index, arg) {
          if (index > 0) {
            var value = scope[arg];
            if (value == null || value == undefined) {
              value = arg;
            }
            displayMessage = displayMessage.replace("{" + (index - 1) + "}", value);
          }
        });
        return displayMessage;
      }
    }
  }
});
