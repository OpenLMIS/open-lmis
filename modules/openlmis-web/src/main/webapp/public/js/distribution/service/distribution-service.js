/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

distributionModule.service('distributionService', function (IndexedDB, $q, $route, $rootScope) {

  var thisService = this;

  function getDistribution() {
    var waitOn = $q.defer();
    IndexedDB.get('distributions', utils.parseIntWithBaseTen($route.current.params.distribution), function (e) {
      waitOn.resolve(e.target.result);
    }, {});

    return waitOn.promise;
  };

  var promise = getDistribution();

  promise.then(function (value) {
    thisService.distribution = value;
    $rootScope.$broadcast('distributionReceived');
  }, {})

});

