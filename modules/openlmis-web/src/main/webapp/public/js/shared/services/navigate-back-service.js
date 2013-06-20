/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

services.factory('navigateBackService', function() {
   var navigateBack = {};

   navigateBack.setData = function(data) {
    $.extend(navigateBack,  data)
   };

   return navigateBack;
});