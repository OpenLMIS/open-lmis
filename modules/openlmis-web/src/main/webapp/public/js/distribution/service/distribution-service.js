/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

distributionModule.service('distributionService', function ($dialog, messageService) {

  var _this = this;

  this.applyNR = function(applyFunc) {
    var dialogOpts = {
      id: "distributionInitiated",
      header: messageService.get('label.apply.nr.all'),
      body: messageService.get('message.apply.nr')
    };

    var callback = function () {
      return function (result) {
        if(!result) return;

        applyFunc(_this.distribution);
        $($('input[not-recorded]').last()).trigger("blur"); //for auto save
      }
    };

    OpenLmisDialog.newDialog(dialogOpts, callback(), $dialog, messageService);
  }
});

