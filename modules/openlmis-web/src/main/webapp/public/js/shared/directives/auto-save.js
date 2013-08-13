/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

app.directive('autoSave', function ($route) {
  return {
    restrict: 'A',
    link: function (scope, element, attrs, ngModelController) {

      var save = {
        "refrigeratorReadings": function (e) {
          var fridge = {facilityId: $route.current.params.facility, distributionId: $route.current.params.distribution, serialNumber: scope.refrigerator.serialNumber}
        }

      };

      element.find('input, textarea').bind('blur', save[attrs.autoSave]);
    }
  };
});