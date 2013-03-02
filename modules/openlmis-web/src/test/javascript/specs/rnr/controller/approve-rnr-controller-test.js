describe('Approve Requisition controller', function () {

  var scope, ctrl, httpBackend, location, routeParams, controller, requisition,
      programRnrColumnList, nonFullSupplyLineItems, lineItems, columnDefinitions;
  beforeEach(module('openlmis.services'));

  beforeEach(inject(function ($httpBackend, $rootScope, $location, $controller) {
    scope = $rootScope.$new();
    location = $location;
    controller = $controller;
    httpBackend = $httpBackend;
    routeParams = {"rnr": "1", "facility": "1", "program": "1"};
    lineItems = [];
    nonFullSupplyLineItems = [];
    requisition = {'status': "AUTHORIZED", 'lineItems': lineItems, 'nonFullSupplyLineItems': nonFullSupplyLineItems};
    programRnrColumnList = [
      {'name': 'ProductCode', 'label': 'Product Code', 'visible': true},
      {'name': 'quantityApproved', 'label': 'quantity approved', 'visible': true},
      {'name': 'remarks', 'label': 'remarks', 'visible': true}
    ];
    ctrl = controller(ApproveRnrController, {$scope: scope, requisition: requisition, rnrColumns: programRnrColumnList, currency: '$',$location: location, $routeParams: routeParams});
  }));

  it('should set rnr in scope', function () {
    expect(scope.rnr).toEqual(requisition);
  });

  it('should set currency in scope', function () {
    expect(scope.currency).toEqual('$');
  });

  it('should set line items as data in full supply grid', function () {
    expect(scope.rnrGrid.data).toEqual('gridLineItems');
  });

  it('should set columns as columnDefs in non full supply grid', function () {
    expect(scope.rnrGrid.columnDefs).toEqual('columnDefinitions');
  });

  it('should save work in progress for rnr', function () {
    scope.rnr = {"id": "rnrId"};
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200, {'success': "R&R saved successfully!"});
    scope.saveRnr();
    httpBackend.flush();
    expect(scope.message).toEqual("R&R saved successfully!");
  });

  it('should not save work in progress if any line item has invalid approved quantity', function () {
    var lineItems = [
      {'quantityApproved': 'aaas'}
    ];
    scope.rnr = {"id": "rnrId", 'lineItems': lineItems};
    scope.saveRnr();
    expect(scope.error).toEqual("Please correct errors before saving.");
  });

  it('should not approve if any line item has empty approved quantity', function () {
    var lineItems = [
      {'quantityApproved': undefined}
    ];
    scope.rnr = {"id": "rnrId", 'lineItems': lineItems};
    scope.approveRnr();
    expect(scope.error).toEqual("Please complete the R&R form before approving");
    scope.approveRnr();
    expect(scope.error).toEqual("Please complete the R&R form before approving");

  });

  it('should set full supply line items as gridData if supply type is not specified', function() {
    expect(scope.showNonFullSupply).toBeFalsy();
    expect(scope.gridLineItems).toEqual(requisition.lineItems);
  });

  it('should set full supply line items as gridData if supply type is full-supply', function() {
    routeParams.supplyType = 'full-supply';
    scope.$broadcast("$routeUpdate");
    expect(scope.showNonFullSupply).toBeFalsy();
    expect(scope.gridLineItems).toEqual(requisition.lineItems);
  });

  it('should set non full supply line items as gridData if supply type is non-full-supply', function() {
    routeParams.supplyType = 'non-full-supply';
    scope.$broadcast("$routeUpdate");
    expect(scope.showNonFullSupply).toBeTruthy();
    expect(scope.gridLineItems).toEqual(requisition.nonFullSupplyLineItems);
  });

  it('should approve a valid rnr', function () {
    var lineItems = [
      {'quantityApproved': 123}
    ];
    scope.rnr = {"id": "rnrId", 'lineItems': lineItems};
    httpBackend.expect('PUT', '/requisitions/rnrId/approve.json').respond({'success': "R&R approved successfully!"});

    scope.approveRnr();
    httpBackend.flush();

    expect(scope.$parent.message).toEqual("R&R approved successfully!");
  });

});
