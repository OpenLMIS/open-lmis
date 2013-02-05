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