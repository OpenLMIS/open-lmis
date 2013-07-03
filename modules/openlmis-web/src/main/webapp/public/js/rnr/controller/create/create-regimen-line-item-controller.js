/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function CreateRegimenLineItemController($scope, regimenColumnList) {

  $scope.regimenColumns = regimenColumnList;
};

CreateRegimenLineItemController.resolve = {

  regimenColumnList: function ($q, $timeout, $route, RegimenColumns) {
    var deferred = $q.defer();
    $timeout(function () {
      RegimenColumns.get({programId: $route.current.params.program}, function (data) {
        deferred.resolve(data.regimenColumns);
      }, {});
    }, 100);
    return deferred.promise;
  }
};