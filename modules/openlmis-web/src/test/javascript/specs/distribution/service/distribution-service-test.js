/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('Distribution Service', function () {


  var distributionService;
  var indexedDB;
  beforeEach(module('distribution'));

  beforeEach(function () {
    module(function ($provide) {
      $provide.value('IndexedDB', {put: function () {
      }, get: function () {
      }, delete: function () {
      }});
    });
    inject(function (IndexedDB, _distributionService_, SharedDistributions) {
      indexedDB = IndexedDB;
      distributionService = _distributionService_;
      spyOn(indexedDB, 'put');
      spyOn(indexedDB, 'get');
      spyOn(indexedDB, 'delete');
      SharedDistributions.distributionList = [
        {deliveryZone: {id: 1, name: 'zone1'}, program: {id: 1, name: 'program1'}, period: {id: 1, name: 'period1'}},
        {deliveryZone: {id: 2}, program: {id: 2}, period: {id: 2}}
      ];
    })
  });

  it('should check if distribution is already cached', function () {
    var distribution = {deliveryZone: {id: 1}, program: {id: 1}, period: {id: 1}};
    expect(distributionService.isCached(distribution)).toEqual(true);
  });

  it('should prepare distribution and put in database', function () {
    var distribution =
    {
      "id": 6,
      "deliveryZone": {"id": 8, "code": "Sul", "name": "Sul Province"},
      "program": {"id": 5, "code": "VACCINES", "name": "VACCINES", "description": "VACCINES", "active": true, "templateConfigured": false, "regimenTemplateConfigured": false, "push": true},
      "period": {"id": 3, "scheduleId": 2, "name": "Dec2012", "description": "Dec2012", "startDate": 1354300200000, "endDate": 1356892200000, "numberOfMonths": 1},
      "facilityDistributions": { "44": {"refrigerators": {}}},
      "status": "INITIATED", "zpp": "8_5_3"
    }

    var referenceData =
    {
      facilities: [
        {"id": 44, "code": "F14A", "name": "Facility14A",
          "geographicZone": {"id": 18, "code": "District7", "name": "District7",
            "level": {"id": 4, "code": "district", "name": "District", "levelNumber": 4},
            "parent": {"code": "Sul", "name": "Sul", "level": {"code": "province", "name": "Province"}}},
          "catchmentPopulation": 80000, "virtualFacility": false,
          "supportedPrograms": []}
      ],
      refrigerators: []
    };

    distributionService.put(distribution, referenceData);

    expect(distribution.facilityDistributions).toEqual({ 44: { refrigerators: { refrigeratorReadings: []}}});
    expect(indexedDB.put.calls[0].args).toEqual(['distributions', distribution, jasmine.any(Function), {}, jasmine.any(Function)]);
    expect(indexedDB.put.calls[1].args).toEqual(['distributionReferenceData', referenceData, jasmine.any(Function), {}]);
  });

  it('should delete distribution from cache', function () {
    distributionService.deleteDistribution(2);

    expect(indexedDB.delete).toHaveBeenCalledWith('distributions', 2, null, null, jasmine.any(Function));
    expect(indexedDB.delete).toHaveBeenCalledWith('distributionReferenceData', 2);
  });

  it('should get reference data for a distribution', function () {
    distributionService.getReferenceData(1, function () {
    });

    expect(indexedDB.get).toHaveBeenCalledWith('distributionReferenceData', 1, jasmine.any(Function));
  })

});