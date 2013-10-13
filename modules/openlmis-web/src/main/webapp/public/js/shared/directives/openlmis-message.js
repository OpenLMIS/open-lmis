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
            if (value === null || value === undefined) {
              value = arg;
            }
            displayMessage = displayMessage.replace("{" + (index - 1) + "}", value);
          }
        });
        return displayMessage;
      }
    }
  };
});
