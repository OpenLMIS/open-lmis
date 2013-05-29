/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

var OpenLmisDialog = {
  newDialog:function (overrideOpts, callback, $dialog) {
    var defaults = {
      id:"",
      header:"Header",
      body:"Body",
      ok:{label:"OK", value:true},
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
    var closeCallback = function(result) {
      var tabbables = olDialog.modalEl.find(":tabbable");
      tabbables.last().unbind("keydown");
      tabbables.first().unbind("keydown");

      callback(result);
    };

    var olDialog = $dialog.dialog(opts);
    olDialog.open().then(closeCallback);



    var autoFocus = function() {
      if(olDialog.isOpen()) {
        var tabbables = olDialog.modalEl.find(":tabbable");
        tabbables.first().focus();
        tabbables.last().bind("keydown", function(e) {
          if (e.which == 9 && !e.shiftKey) {
            tabbables.first().focus();
            e.preventDefault();
          }

        });
        tabbables.first().bind("keydown", function(e) {
          if (e.which == 9 && e.shiftKey) {
            tabbables.last().focus();
            e.preventDefault();
          }
        });
      }
      else {
        setTimeout(function() {
          autoFocus();
        }, 10);
      }
    };
    autoFocus();
  }
};
