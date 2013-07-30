/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function CreateRegimenLineItemController($scope) {


  $scope.showCategory = function (index) {
    return !((index > 0 ) && ($scope.rnr.regimenLineItems[index].category.name == $scope.rnr.regimenLineItems[index - 1].category.name));
  };

  $scope.getId = function (prefix, parent) {
    return prefix + "_" + parent.$parent.$index;
  };

};