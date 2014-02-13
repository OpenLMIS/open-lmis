/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

distributionModule.service('distributionService', function ($dialog, SharedDistributions, IndexedDB) {

  var _this = this;

  this.applyNR = function (applyFunc) {
    var dialogOpts = {
      id: "distributionInitiated",
      header: 'label.apply.nr.all',
      body: 'message.apply.nr'
    };

    var callback = function () {
      return function (result) {
        if (!result) return;

        applyFunc();
        _this.save(_this.distribution);
      };
    };

    OpenLmisDialog.newDialog(dialogOpts, callback(), $dialog);
  };

  this.isCached = function (distribution) {
    return !!_.find(SharedDistributions.distributionList, function (cachedDistribution) {
      return cachedDistribution.deliveryZone.id == distribution.deliveryZone.id &&
        cachedDistribution.program.id == distribution.program.id &&
        cachedDistribution.period.id == distribution.period.id;
    });
  };

  this.save = function (distribution) {
    IndexedDB.put('distributions', distribution, null, null, SharedDistributions.update);
  };

  this.deleteDistribution = function (id) {
    IndexedDB.delete('distributions', id, null, null, function () {
      SharedDistributions.update();
    });
  };
});


