/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

Distribution.resolve = {
  distribution: function (distributionService, $q, IndexedDB, $route) {
    var distributionDefer = $q.defer();
    if (!distributionService.distribution) {
      IndexedDB.get('distributions', utils.parseIntWithBaseTen($route.current.params.distribution), function (e) {
        distributionService.distribution = e.target.result;
        distributionDefer.resolve(distributionService.distribution);
      }, {});
    } else {
      distributionDefer.resolve(distributionService.distribution);
    }

    return distributionDefer.promise;
  }
};
