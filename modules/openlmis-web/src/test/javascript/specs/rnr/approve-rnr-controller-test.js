describe('Approve Requisition controller', function () {

  var scope, ctrl, httpBackend, location, routeParams, requisitionHeader, controller, requisition,
    programRnrColumnList, nonFullSupplyLineItems, lineItems, columnDefinitions, lossesAndAdjustmentsReferenceData;

  beforeEach(module('openlmis.services'));

  beforeEach(inject(function ($httpBackend, $rootScope, $location, $controller) {
    scope = $rootScope.$new();
    location = $location;
    controller = $controller;
    httpBackend = $httpBackend;
    routeParams = {"rnr":"1", "facility":"1", "program":"1"};
    lineItems = [];
    nonFullSupplyLineItems = [];
    requisition = {'status':"AUTHORIZED", 'lineItems':lineItems, 'nonFullSupplyLineItems' : nonFullSupplyLineItems};
    programRnrColumnList = [
      {'name':'ProductCode', 'label':'Product Code'},
      {'name':'quantityApproved', 'label':'quantity approved'},
      {'name':'remarks', 'label':'remarks'}
    ];
    httpBackend.expect('GET', '/requisitions/lossAndAdjustments/reference-data.json').respond({"lossAdjustmentTypes":{}});
    httpBackend.expect('GET', '/reference-data/currency.json').respond({"currency":'$'});
    ctrl = controller(ApproveRnrController, {$scope:scope, requisition:requisition, programRnrColumnList:programRnrColumnList, $location:location, $routeParams:routeParams});
  }));

  it('should set rnr in scope', function () {
    expect(scope.rnr).toEqual(requisition);
  });

  it('should set losses and adjustment types', function () {
    httpBackend.flush();
    expect(scope.allTypes).toEqual({});
  });

  it('should set currency in scope', function () {
    httpBackend.flush();
    expect(scope.currency).toEqual('$');
  });


  it('should set line-items in scope', function () {
    expect(scope.lineItems).toEqual(lineItems);
  });

  it('should set non full supply line-items in scope', function () {
    expect(scope.rnr.nonFullSupplyLineItems).toEqual(nonFullSupplyLineItems);
  });

  it('should set columns list in scope', function () {
    expect(scope.programRnrColumnList).toEqual(programRnrColumnList);
  });

  it('should set line items as data in full supply grid', function () {
    expect(scope.fullSupplyGrid.data).toEqual('rnr.lineItems');
  });

  it('should set non full supply line items as  data in non full supply grid', function () {
    expect(scope.nonFullSupplyGrid.data).toEqual('rnr.nonFullSupplyLineItems');
  });

  it('should save work in progress for rnr', function () {
    scope.rnr = {"id":"rnrId"};
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond({'success':"R&R saved successfully!"});
    scope.saveRnr();
    httpBackend.flush();
    expect(scope.message).toEqual("R&R saved successfully!");
  });

  it('should not save work in progress if any line item has invalid approved quantity', function () {
    var lineItems = [
      {'quantityApproved':'aaas'}
    ];
    scope.lineItems = lineItems;
    scope.requisition = {"id":"rnrId", 'lineItems':lineItems};
    scope.saveRnr();
    expect(scope.error).toEqual("Please correct errors before saving.");
  });

  it('should not approve if any line item has empty approved quantity', function () {
    var lineItems = [
      {'quantityApproved':undefined}
    ];
    scope.rnr = {"id":"rnrId", 'lineItems':lineItems};
    scope.approveRnr();
    expect(scope.error).toEqual("Please complete the highlighted fields on the R&R form before approving");
    lineItems = [
      {'quantityApproved':null}
    ];
    scope.approveRnr();
    expect(scope.error).toEqual("Please complete the highlighted fields on the R&R form before approving");

  });

  it('should approve a valid rnr', function () {
    var lineItems = [
      {'quantityApproved':123}
    ];
    scope.rnr= {"id":"rnrId", 'lineItems':lineItems};
    httpBackend.expect('PUT', '/requisitions/rnrId/approve.json').respond({'success':"R&R approved successfully!"});

    scope.approveRnr();
    httpBackend.flush();

    expect(scope.$parent.message).toEqual("R&R approved successfully!");
  });

})
;
