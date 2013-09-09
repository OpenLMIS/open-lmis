/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

ResolveDistribution = {
  distribution: function (distributionService, $q, IndexedDB, $route) {
    var distributionDefer = $q.defer();
    var distributionId = utils.parseIntWithBaseTen($route.current.params.distribution);
    if (!distributionService.distribution || distributionService.distribution.id != distributionId) {
      IndexedDB.get('distributions', distributionId, function (e) {
        distributionService.distribution = new Distribution(e.target.result);
        distributionDefer.resolve(distributionService.distribution);
      }, {});
    } else {
      distributionDefer.resolve(distributionService.distribution);
    }

    return distributionDefer.promise;
  }
};
