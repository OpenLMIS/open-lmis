/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ViewLoadAmountController($scope, facilities) {

  $scope.facilities = facilities;

}

ViewLoadAmountController.resolve = {
  facilities: function (FacilitiesProgramProducts, $route,$timeout, $q) {
    var deferred = $q.defer();
    $timeout(function () {
      FacilitiesProgramProducts.get({deliveryZoneId: $route.current.params.deliveryZoneId, programId: $route.current.params.programId}, function (data) {
        deferred.resolve(data.facilities);
      }, {});
    }, 100);

    return deferred.promise;
  }
};
