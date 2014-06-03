/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("Services", function () {

  beforeEach(module('openlmis.services'));
  describe("ApproveRnrService", function () {

    var httpBackend, requisitionForApprovalService;

    beforeEach(inject(function (_$httpBackend_, RequisitionForApproval) {
      httpBackend = _$httpBackend_;
      requisitionForApprovalService = RequisitionForApproval;
    }));

    it('should GET R&Rs pending for approval', function () {
      var requisitions = {"rnr_list": []};
      httpBackend.expect('GET', "/requisitions-for-approval.json").respond(requisitions);
      requisitionForApprovalService.get({}, function (data) {
        expect(data.rnr_list).toEqual(requisitions.rnr_list);
      }, function () {
      });
    });
  });

  describe("SupplyLineSearchService", function () {

    var httpBackend, supplyLineSearchService;

    beforeEach(inject(function (_$httpBackend_, SupplyLinesSearch) {
      httpBackend = _$httpBackend_;
      supplyLineSearchService = SupplyLinesSearch;
    }));

    it('should GET searched supplyLines', function () {
      var supplyLinesResponse = {"supplyLines": [], "pagination": {}};
      httpBackend.expect('GET', "/supplyLines/search.json").respond(supplyLinesResponse);
      supplyLineSearchService.get({}, function (data) {
        expect(data.supplyLines).toEqual(supplyLinesResponse.supplyLines);
        expect(data.pagination).toEqual(supplyLinesResponse.pagination);
      }, function () {
      });
    });
  })

});