/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

distributionModule.service('distributionService', function ($dialog, messageService, SharedDistributions, IndexedDB) {

  var _this = this;

  function prepareDistribution(distribution, referenceData) {
    distribution.facilityDistributionData = {};
    $(referenceData.facilities).each(function (index, facility) {

      var productGroups = [];
      $(facility.supportedPrograms[0].programProducts).each(function (i, programProduct) {
        if (!programProduct.active || !programProduct.product.active) return;
        if (!programProduct.product.productGroup) return;
        if (_.findWhere(productGroups, {id: programProduct.product.productGroup.id})) return;

        productGroups.push(programProduct.product.productGroup);
      });

      var refrigeratorReadings = [];
      $(_.where(referenceData.refrigerators, {facilityId: facility.id})).each(function (i, refrigerator) {
        refrigeratorReadings.push({'refrigerator': refrigerator});
      });

      distribution.facilityDistributionData[facility.id] = {};
      distribution.facilityDistributionData[facility.id].refrigerators = {refrigeratorReadings: refrigeratorReadings};
      distribution.facilityDistributionData[facility.id].epiUse = {productGroups: productGroups};
    });

    return distribution;
  }

  this.applyNR = function (applyFunc) {
    var dialogOpts = {
      id: "distributionInitiated",
      header: messageService.get('label.apply.nr.all'),
      body: messageService.get('message.apply.nr')
    };

    var callback = function () {
      return function (result) {
        if (!result) return;

        applyFunc(_this.distribution);
        $($('input[not-recorded]').last()).trigger("blur"); //for auto save
      }
    };

    OpenLmisDialog.newDialog(dialogOpts, callback(), $dialog, messageService);
  };

  this.isCached = function (distribution) {
    return !!_.find(SharedDistributions.distributionList, function (cachedDistribution) {
      return cachedDistribution.deliveryZone.id == distribution.deliveryZone.id &&
        cachedDistribution.program.id == distribution.program.id &&
        cachedDistribution.period.id == distribution.period.id;
    });
  };

  this.put = function (distribution, referenceData) {
    distribution = prepareDistribution(distribution, referenceData);

    IndexedDB.put('distributions', distribution, function () {
    }, {}, function () {
      SharedDistributions.update();
    });

    referenceData.distributionId = distribution.id;
    IndexedDB.put('distributionReferenceData', referenceData, function () {
    }, {});
  };
});

