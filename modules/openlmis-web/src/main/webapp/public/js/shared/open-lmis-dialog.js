/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

var OpenLmisDialog = {
  new:function (overrideOpts, callback, $dialog) {
    var defaults = {
      id:"",
      header:"Change role type",
      body:"If you change the type, all selections in current type will be removed?",
      ok:{label:"Continue", value:true},
      cancel:{label:"Cancel", value:false}
    };

    var opts = {
      templateUrl:'/public/pages/partials/dialogbox.html',
      controller:function ($scope, dialog) {

        $scope.dialogClose = function (result) {
          dialog.close(result);
        };

        $scope.dialogOptions = _.extend(defaults, overrideOpts);
      }
    };

    $dialog.dialog(opts).open().then(callback);
  }
};
