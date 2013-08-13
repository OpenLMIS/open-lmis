/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

app.directive('autoSave', function () {
  return {
    restrict: 'A',
    link: function (scope, element, attrs, ngModelController) {

      console.log(element);
      var inputs = element.find("input[type='text']");
      var radios = element.find("input[type='radio']");
      var checkboxes = element.find("input[type='checkbox']");
      var textareas = element.find("textarea");

      inputs.each(function(index, input) {
        $(input).on('blur', function(e) {
          console.log(scope);
        });
      });

    }
  };
});