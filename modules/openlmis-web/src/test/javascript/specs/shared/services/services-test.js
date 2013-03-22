/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
      var requisitions = {"rnr_list":[]};
      httpBackend.expect('GET', "/requisitions-for-approval.json").respond(requisitions);
      requisitionForApprovalService.get({}, function (data) {
        expect(data.rnr_list).toEqual(requisitions.rnr_list);
      }, function () {
      });
    });
  })
});