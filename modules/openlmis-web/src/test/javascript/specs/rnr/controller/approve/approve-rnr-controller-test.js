/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('Approve Requisition controller', function () {

  var scope, ctrl, httpBackend, location, routeParams, controller, requisition, messageService, regimenTemplate,
    programRnrColumnList, nonFullSupplyLineItems, lineItems, regimenLineItems, dialog, rnrLineItem, regimenColumns;
  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));
  beforeEach(module('ui.bootstrap.dialog'));

  beforeEach(inject(function ($httpBackend, $rootScope, $location, $controller, _messageService_) {
    scope = $rootScope.$new();
    location = $location;
    controller = $controller;
    httpBackend = $httpBackend;
    messageService = _messageService_;
    routeParams = {"rnr": "1", "program": "1", "supplyType": "full-supply"};
    lineItems = [];
    nonFullSupplyLineItems = [];
    regimenLineItems = [];
    requisition = {'status': "AUTHORIZED", 'lineItems': lineItems, 'nonFullSupplyLineItems': nonFullSupplyLineItems, regimenLineItems: regimenLineItems, period: {numberOfMonths: 5}};
    $rootScope.pageSize = 2;
    scope.approvalForm = {};
    programRnrColumnList = [
      {'name': 'ProductCode', 'label': 'Product Code', 'visible': true},
      {'name': 'quantityApproved', 'label': 'quantity approved', 'visible': true},
      {'name': 'remarks', 'label': 'remarks', 'visible': true}
    ];
    regimenColumns = [
      {"test": "test"}
    ];

    regimenTemplate = {regimenColumns: regimenColumns};
    rnrLineItem = new RnrLineItem({"fullSupply": true});
    ctrl = controller(ApproveRnrController, {$scope: scope, requisition: requisition, rnrColumns: programRnrColumnList, regimenTemplate: regimenTemplate,
      currency: '$', $location: location, $routeParams: routeParams});
  }));

  it('should set rnr in scope', function () {
    var spyOnRnr = spyOn(window, 'Rnr').andCallThrough();
    ctrl = controller(ApproveRnrController, {$scope: scope, requisition: requisition, rnrColumns: programRnrColumnList, regimenTemplate: regimenTemplate,
      currency: '$', $location: location, $routeParams: routeParams});
    expect(spyOnRnr).toHaveBeenCalledWith(requisition, programRnrColumnList);
  });

  it('should set currency in scope', function () {
    expect(scope.currency).toEqual('$');
  });

  it('should save work in progress for rnr', function () {
    scope.rnr = new Rnr({"id": "rnrId"});
    scope.pageLineItems = [rnrLineItem];
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200, {'success': "R&R saved successfully!"});
    scope.approvalForm.$dirty = true;
    scope.approvalForm.$setPristine = function () {
      scope.approvalForm.pristine = true
    };
    scope.saveRnr();
    httpBackend.flush();
    expect(scope.message).toEqual("R&R saved successfully!");
    expect(scope.approvalForm.pristine).toBeTruthy();
  });

  it('should not approve and set error class if any full supply line item has empty approved quantity but should save', function () {
    scope.rnr = new Rnr({"id": "rnrId"});
    scope.pageLineItems = [rnrLineItem];
    spyOn(scope.rnr, 'validateFullSupplyForApproval').andReturn('some error');
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200);
    scope.approveRnr();
    expect(scope.fullSupplyTabError).toBeTruthy();
    expect(scope.error).toEqual("some error");
  });

  it('should not approve if any non full supply line item has empty approved quantity but should save', function () {
    scope.rnr = new Rnr({"id": "rnrId"});
    scope.pageLineItems = [rnrLineItem];
    spyOn(scope.rnr, 'validateFullSupplyForApproval').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupplyForApproval').andReturn('some error');
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200);
    scope.approveRnr();
    expect(scope.nonFullSupplyTabError).toBeTruthy();
    expect(scope.error).toEqual("some error");
  });

  it('should reset visible tab if supply type is not specified', function () {
    expect(scope.visibleTab).toEqual('full-supply');
  });

  it('should set visible tab to full-supply if supply type is full-supply', function () {
    routeParams.supplyType = 'full-supply';
    scope.$broadcast("$routeUpdate");
    expect(scope.visibleTab).toEqual("full-supply");
  });

  it('should set Error pages according to tab', function () {
    scope.numberOfPages = 5;
    scope.approvalForm.$dirty = true;
    scope.approvalForm.$setPristine = function () {
      scope.approvalForm.pristine = true
    };
    scope.errorPages = {fullSupply: [5], nonFullSupply: [7]};
    scope.rnr.id = "rnrId";
    routeParams.page = 1;
    routeParams.supplyType = 'non-full-supply';
    scope.pageLineItems = [rnrLineItem];
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200, {"success": "saved successfully"});
    scope.$broadcast("$routeUpdate");
    httpBackend.flush();
    expect(scope.shownErrorPages).toEqual(scope.errorPages.nonFullSupply);
  });

  it('should set showNonFullSupply flag if supply type is non-full-supply', function () {
    scope.numberOfPages = 5;
    scope.approvalForm.$dirty = true;
    scope.approvalForm.$setPristine = function () {
      scope.approvalForm.pristine = true
    };
    scope.rnr.id = "rnrId";
    routeParams.page = 1;
    routeParams.supplyType = 'non-full-supply';
    scope.pageLineItems = [rnrLineItem];
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200, {"success": "saved successfully"});
    scope.$broadcast("$routeUpdate");
    httpBackend.flush();
    expect(scope.visibleTab).toEqual('non-full-supply');
  });

  it('should display confirm modal if approve button is clicked on valid Rnr', function () {
    scope.rnr = new Rnr({"id": "rnrId"}, []);
    scope.pageLineItems = [rnrLineItem];
    spyOn(scope.rnr, 'validateFullSupplyForApproval').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupplyForApproval').andReturn('');
    spyOn(OpenLmisDialog, 'newDialog');
    scope.approveRnr();
    httpBackend.expectGET('/public/pages/partials/dialogbox.html').respond(200);
    expect(OpenLmisDialog.newDialog).toHaveBeenCalled();
  });

  it('should approve Rnr if ok is clicked on the confirm modal', function () {
    scope.rnr = new Rnr({"id": "rnrId"}, []);
    scope.pageLineItems = [rnrLineItem];
    scope.approvalForm.$dirty = true;
    scope.approvalForm.$setPristine = function () {
      scope.approvalForm.pristine = true
    };
    spyOn(scope.rnr, 'validateFullSupplyForApproval').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupplyForApproval').andReturn('');
    httpBackend.expect('PUT', '/requisitions/rnrId/approve.json').respond({'success': "R&R approved successfully!"});
    scope.dialogCloseCallback(true);
    httpBackend.flush();
    expect(scope.$parent.message).toEqual("R&R approved successfully!");
  });

  it('should calculate number of pages for a pageSize of 2 and 4 lineItems', function () {
    requisition.fullSupplyLineItems = [
      {'id': 1},
      {'id': 2},
      {'id': 3},
      {'id': 4}
    ];
    ctrl = controller(ApproveRnrController, {$scope: scope, requisition: requisition, rnrColumns: programRnrColumnList, currency: '$',
      regimenTemplate: regimenTemplate, $location: location, $routeParams: routeParams});

    expect(2).toEqual(scope.numberOfPages);
  });

  it('should calculate number of pages for a pageSize of 2 and 4 nonFullSupplyLineItems', function () {
    routeParams.supplyType = 'non-full-supply';
    requisition.nonFullSupplyLineItems = [
      {'id': 1},
      {'id': 2},
      {'id': 3},
      {'id': 4}
    ];
    ctrl = controller(ApproveRnrController, {$scope: scope, requisition: requisition, rnrColumns: programRnrColumnList, currency: '$',
      regimenTemplate: regimenTemplate, $location: location, $routeParams: routeParams});

    expect(2).toEqual(scope.numberOfPages);
  });

  it('should determine lineItems to be displayed on page 1 for page size 2', function () {
    requisition.fullSupplyLineItems = [
      {'id': 1},
      {'id': 2},
      {'id': 3},
      {'id': 4}
    ];
    ctrl = controller(ApproveRnrController, {$scope: scope, requisition: requisition, rnrColumns: programRnrColumnList, currency: '$',
      regimenTemplate: regimenTemplate, $location: location, $routeParams: routeParams});

    expect(scope.pageLineItems[0].id).toEqual(1);
    expect(scope.pageLineItems[1].id).toEqual(2);
    expect(scope.pageLineItems.length).toEqual(2);
  });

  it('should determine lineItems to be displayed on page 2 for page size 2', function () {
    routeParams.page = 2;
    requisition.fullSupplyLineItems = [
      {'id': 1},
      {'id': 2},
      {'id': 3},
      {'id': 4}
    ];
    ctrl = controller(ApproveRnrController, {$scope: scope, requisition: requisition, rnrColumns: programRnrColumnList, currency: '$',
      regimenTemplate: regimenTemplate, $location: location, $routeParams: routeParams});

    expect(scope.pageLineItems[0].id).toEqual(3);
    expect(scope.pageLineItems[1].id).toEqual(4);
    expect(scope.pageLineItems.length).toEqual(2);
  });

  it('should set current page 1 if page not defined', function () {
    expect(scope.currentPage).toEqual(1);
  });

  it('should set current page to 1 if page not within valid range', function () {
    routeParams.page = -95;
    ctrl = controller(ApproveRnrController, {$scope: scope, requisition: requisition, rnrColumns: programRnrColumnList, currency: '$',
      regimenTemplate: regimenTemplate, $location: location, $routeParams: routeParams});

    expect(scope.currentPage).toEqual(1);
  });

  it('should save rnr on page change only if dirty', function () {
    scope.numberOfPages = 5;
    scope.approvalForm.$dirty = true;
    scope.approvalForm.$setPristine = function () {
      scope.approvalForm.pristine = true
    };
    routeParams.page = 2;
    scope.rnr.id = "rnrId";
    scope.pageLineItems = [rnrLineItem];
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200, {'success': "success message"});
    scope.$broadcast('$routeUpdate');
    httpBackend.flush();
    expect(scope.message).toEqual('success message');
  });

  it('should set message while saving if set message flag true', function () {
    scope.rnr = new Rnr({"id": "rnrId"});
    scope.pageLineItems = [rnrLineItem];
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200, {'success': "success message"});
    scope.approvalForm.$dirty = true;
    scope.approvalForm.$setPristine = function () {
      scope.approvalForm.pristine = true
    };
    scope.saveRnr(false);
    httpBackend.flush();
    expect(scope.message).toEqual('success message');
  });

  it('should not set message while saving if set message flag false', function () {
    scope.rnr = new Rnr({"id": "rnrId"});
    scope.pageLineItems = [rnrLineItem];
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200, {'success': "success message"});
    scope.approvalForm.$dirty = true;
    scope.approvalForm.$setPristine = function () {
      scope.approvalForm.pristine = true
    };
    scope.saveRnr(true);
    httpBackend.flush();
    expect(scope.message).toEqual('');
  });

  it('should calculate pages which have errors on approve', function () {
    scope.rnr = new Rnr({"id": "1", "fullSupplyLineItems": [
      {id: 1},
      {id: 2},
      {id: 3}
    ], period: {numberOfMonths: 7}}, null);
    scope.pageLineItems = [rnrLineItem];

    scope.pageSize = 5;
    spyOn(scope.rnr, 'getErrorPages').andReturn({nonFullSupply: [1, 2], fullSupply: [2, 4]});
    spyOn(scope.rnr, 'validateFullSupplyForApproval').andReturn("");
    spyOn(scope.rnr, 'validateNonFullSupplyForApproval').andReturn("some error");
    httpBackend.expect('PUT', '/requisitions/1/save.json').respond(200, {'success': "success message"});
    scope.approveRnr();

    expect(scope.errorPages).toEqual({nonFullSupply: [1, 2], fullSupply: [2, 4]});
    expect(scope.rnr.getErrorPages).toHaveBeenCalledWith(5);
  });

  it('should return true if error on full supply page', function () {
    scope.errorPages = {fullSupply: [1]};
    scope.visibleTab = 'full-supply';
    var result = scope.checkErrorOnPage(1);
    expect(result).toBeTruthy();
  });

  it('should return false if no error on full supply page', function () {
    scope.errorPages = {fullSupply: []};
    scope.visibleTab = 'full-supply';
    var result = scope.checkErrorOnPage(1);
    expect(result).toBeFalsy();
  });

  it('should return true if error on non full supply page', function () {
    scope.errorPages = {nonFullSupply: [1]};
    scope.visibleTab = 'non-full-supply';
    var result = scope.checkErrorOnPage(1);
    expect(result).toBeTruthy();
  });

  it('should return false if no error on non full supply page', function () {
    scope.errorPages = {nonFullSupply: []};
    scope.visibleTab = 'non-full-supply';
    var result = scope.checkErrorOnPage(1);
    expect(result).toBeFalsy();
  });
});
