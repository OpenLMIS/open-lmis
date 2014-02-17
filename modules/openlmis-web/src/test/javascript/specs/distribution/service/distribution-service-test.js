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

  it('should prepare distribution and save in database', function () {
    var distribution =
    {
      "id": 6,
      "deliveryZone": {"id": 8, "code": "Sul", "name": "Sul Province"},
      "program": {"id": 5, "code": "VACCINES", "name": "VACCINES", "description": "VACCINES", "active": true, "templateConfigured": false, "regimenTemplateConfigured": false, "push": true},
      "period": {"id": 3, "scheduleId": 2, "name": "Dec2012", "description": "Dec2012", "startDate": 1354300200000, "endDate": 1356892200000, "numberOfMonths": 1},
      "facilityDistributions": { "44": {"refrigerators": {}}},
      "status": "INITIATED", "zpp": "8_5_3"
    };

    distributionService.save(distribution);

    expect(distribution.facilityDistributions).toEqual({ "44": {"refrigerators": {}}});
    expect(indexedDB.put.calls[0].args).toEqual(['distributions', distribution, null, null, jasmine.any(Function)]);
  });

  it('should delete distribution from cache', function () {
    distributionService.deleteDistribution(2);

    expect(indexedDB.delete).toHaveBeenCalledWith('distributions', 2, null, null, jasmine.any(Function));
  });

});