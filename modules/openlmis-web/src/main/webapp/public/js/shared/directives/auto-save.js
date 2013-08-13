/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

app.directive('autoSave', function ($route) {
  return {
    restrict: 'A',
    link: function (scope, element, attrs, ngModelController) {

      var save = function (e) {
//        var fridge = {facilityId: $route.current.params.facility, distributionZpp: $route.current.params.zpp, serialNumber: scope.refrigerator.serialNumber}
//        var scope[attrs.autoSave];
      };

      element.find('input, textarea').bind('blur', save);
    }
  };
});