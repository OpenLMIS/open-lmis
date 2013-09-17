/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

describe('Distribution Service', function () {


  var distributionService;
  var indexedDB;
  beforeEach(module('distribution'));

  beforeEach(function () {
    module(function ($provide) {
      $provide.value('IndexedDB', {put: function () {
      }});
    });

    inject(function ($injector, _distributionService_, SharedDistributions) {
      indexedDB = $injector.get('IndexedDB');
      distributionService = _distributionService_;
      spyOn(indexedDB, 'put');
      SharedDistributions.distributionList = [
        {deliveryZone: {id: 1, name: 'zone1'}, program: {id: 1, name: 'program1'}, period: {id: 1, name: 'period1'}},
        {deliveryZone: {id: 2}, program: {id: 2}, period: {id: 2}}
      ];
    });

  });

  it('should check if distribution is already cached', function () {
    var distribution = {deliveryZone: {id: 1}, program: {id: 1}, period: {id: 1}};
    expect(distributionService.isCached(distribution)).toEqual(true);
  });

  it('should prepare distribution and put in database', function () {
    var distribution = {"id": 6, "deliveryZone": {"id": 8, "code": "Sul", "name": "Sul Province"}, "program": {"id": 5, "code": "VACCINES", "name": "VACCINES", "description": "VACCINES", "active": true, "templateConfigured": false, "regimenTemplateConfigured": false, "push": true}, "period": {"id": 3, "scheduleId": 2, "name": "Dec2012", "description": "Dec2012", "startDate": 1354300200000, "endDate": 1356892200000, "numberOfMonths": 1}, "status": "INITIATED", "zpp": "8_5_3"};
    var referenceData = {facilities: [
      {"id": 44, "code": "F14A", "name": "Facility14A", "geographicZone": {"id": 18, "code": "District7", "name": "District7", "level": {"id": 4, "code": "district", "name": "District", "levelNumber": 4}, "parent": {"code": "Sul", "name": "Sul", "level": {"code": "province", "name": "Province"}}}, "catchmentPopulation": 80000, "virtualFacility": false, "supportedPrograms": [
        {"id": 39, "modifiedBy": 1, "createdDate": 1379396951887, "modifiedDate": 1379396951887, "facilityId": 44, "program": {"id": 5, "createdDate": 1379396951593, "modifiedDate": 1379396951593, "code": "VACCINES", "name": "VACCINES", "description": "VACCINES", "active": true, "templateConfigured": false, "regimenTemplateConfigured": false, "push": true}, "active": true, "startDate": 1352572200000, "programProducts": [
          {"id": 154, "program": {"id": 5, "createdDate": 1379396951593, "modifiedDate": 1379396951593, "code": "VACCINES", "name": "VACCINES", "description": "VACCINES", "active": true, "templateConfigured": false, "regimenTemplateConfigured": false, "push": true}, "product": {"id": 153, "createdDate": 1379396951632, "modifiedDate": 1379396951632, "code": "syringe5ml", "alternateItemCode": "a", "manufacturer": "Glaxo and Smith", "manufacturerCode": "a", "manufacturerBarCode": "a", "mohBarCode": "a", "gtin": "a", "type": "syringe5ml", "displayOrder": 3, "primaryName": "syringe5ml", "fullName": "TDF/FTC/EFV", "genericName": "TDF/FTC/EFV", "alternateName": "TDF/FTC/EFV", "description": "TDF/FTC/EFV", "strength": "300/200/600", "productGroup": {"id": 4, "createdDate": 1379396951621, "modifiedDate": 1379396951621, "code": "syringe", "name": "syringe"}, "dispensingUnit": "Strip", "dosesPerDispensingUnit": 10, "storeRefrigerated": true, "storeRoomTemperature": true, "hazardous": true, "flammable": true, "controlledSubstance": true, "lightSensitive": true, "approvedByWHO": true, "contraceptiveCYP": 1.0, "packSize": 10, "alternatePackSize": 30, "packLength": 2.2, "packWidth": 2.0, "packHeight": 2.0, "packWeight": 2.0, "packsPerCarton": 2, "cartonLength": 2.0, "cartonWidth": 2.0, "cartonHeight": 2.0, "cartonsPerPallet": 2, "expectedShelfLife": 2, "specialStorageInstructions": "a", "specialTransportInstructions": "a", "active": true, "fullSupply": true, "tracer": true, "packRoundingThreshold": 1, "roundToZero": false, "archived": true}, "dosesPerMonth": 30, "active": true, "currentPrice": "12.50", "facilityId": 44}
        ]}
      ]}
    ], refrigerators: []};

    distributionService.put(distribution, referenceData);

    expect(distribution.facilityDistributionData).toEqual({ 44: { refrigerators: { refrigeratorReadings: []}, epiUse: { productGroups: [
      { id: 4, createdDate: 1379396951621, modifiedDate: 1379396951621, code: 'syringe', name: 'syringe'}
    ]}}});
    expect(indexedDB.put.calls[0].args).toEqual(['distributions', distribution, jasmine.any(Function), {}, jasmine.any(Function)]);
    expect(indexedDB.put.calls[1].args).toEqual(['distributionReferenceData', referenceData, jasmine.any(Function), {}]);
  });

});