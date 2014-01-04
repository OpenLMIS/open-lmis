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

      function apply(displayMessage) {
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
            if (element.hasClass('welcome-message')) {
              element.attr('title', displayMessage);
            }
        }
      }

      var refreshMessages = function () {
        var key = scope.$eval(keyWithArgs[0]) || keyWithArgs[0];
        var message = messageService.get(key) || key;

        if (keyWithArgs[1]) {
          message = replaceArgs(scope, message, keyWithArgs);
        }
        apply(message);
      };

      scope.$watch("[" + keyWithArgs.toString() + "]", refreshMessages, true);
      scope.$on('messagesPopulated', refreshMessages);

      function argumentMatcher(index) {
        return ['{', index - 1, '}'].join('');
      }

      function replaceArgs(scope, message, args) {
        $(args).each(function (index, arg) {
          if (index > 0) {
            var argValue = scope.$eval(arg) || arg;
            message = message.replace(argumentMatcher(index), argValue);
          }
        });
        return message;
      }
    }
  };
});
