/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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

