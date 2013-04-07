/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
function ViewNonFullSupplyController($scope) {

  $scope.visibleNonFullSupplyColumns = _.filter($scope.visibleColumns, function (column) {
    return _.contains(RnrLineItem.visibleForNonFullSupplyColumns, column.name) || column.name == 'quantityApproved';
  });
}
