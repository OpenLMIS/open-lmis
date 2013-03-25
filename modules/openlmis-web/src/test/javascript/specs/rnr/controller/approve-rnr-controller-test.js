/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('Approve Requisition controller', function () {

  var scope, ctrl, httpBackend, location, routeParams, controller, requisition,
    programRnrColumnList, nonFullSupplyLineItems, lineItems, columnDefinitions;
  beforeEach(module('openlmis.services'));

  beforeEach(inject(function ($httpBackend, $rootScope, $location, $controller) {
    scope = $rootScope.$new();
    location = $location;
    controller = $controller;
    httpBackend = $httpBackend;
    routeParams = {"rnr":"1", "program":"1"};
    lineItems = [];
    nonFullSupplyLineItems = [];
    requisition = {'status':"AUTHORIZED", 'lineItems':lineItems, 'nonFullSupplyLineItems':nonFullSupplyLineItems, period:{numberOfMonths:5}};
    $rootScope.pageSize = 2;
    programRnrColumnList = [
      {'name':'ProductCode', 'label':'Product Code', 'visible':true},
      {'name':'quantityApproved', 'label':'quantity approved', 'visible':true},
      {'name':'remarks', 'label':'remarks', 'visible':true}
    ];
    ctrl = controller(ApproveRnrController, {$scope:scope, requisition:requisition, rnrColumns:programRnrColumnList,
      currency:'$', $location:location, $routeParams:routeParams});
  }));

  it('should set rnr in scope', function () {
    var spyOnRnr = spyOn(window, 'Rnr').andCallThrough();
    ctrl = controller(ApproveRnrController, {$scope:scope, requisition:requisition, rnrColumns:programRnrColumnList,
      currency:'$', $location:location, $routeParams:routeParams});
    expect(spyOnRnr).toHaveBeenCalledWith(requisition, programRnrColumnList);
  });

  it('should set currency in scope', function () {
    expect(scope.currency).toEqual('$');
  });

  it('should set paged line items as data in full supply grid', function () {
    expect(scope.rnrGrid.data).toEqual('pageLineItems');
  });

  it('should set columns as columnDefs in non full supply grid', function () {
    expect(scope.rnrGrid.columnDefs).toEqual('columnDefinitions');
  });

  it('should save work in progress for rnr', function () {
    scope.rnr = {"id":"rnrId"};
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200, {'success':"R&R saved successfully!"});
    scope.saveRnr();
    httpBackend.flush();
    expect(scope.message).toEqual("R&R saved successfully!");
  });

  it('should not approve and set error class if any full supply line item has empty approved quantity but should save', function () {
    scope.rnr = new Rnr({"id":"rnrId"});
    spyOn(scope.rnr, 'validateFullSupplyForApproval').andReturn('some error');
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200);
    scope.approveRnr();
    expect(scope.fullSupplyTabError).toBeTruthy();
    expect(scope.error).toEqual("some error");
  });

  it('should not approve if any non full supply line item has empty approved quantity but should save', function () {
    scope.rnr = new Rnr({"id":"rnrId"});
    spyOn(scope.rnr, 'validateFullSupplyForApproval').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupplyForApproval').andReturn('some error');
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200);
    scope.approveRnr();
    expect(scope.nonFullSupplyTabError).toBeTruthy();
    expect(scope.error).toEqual("some error");
  });

  it('should reset showNonFullSupply flag if supply type is not specified', function () {
    expect(scope.showNonFullSupply).toBeFalsy();
  });

  it('should reset showNonFullSupply flag if supply type is full-supply', function () {
    routeParams.supplyType = 'full-supply';
    scope.$broadcast("$routeUpdate");
    expect(scope.showNonFullSupply).toBeFalsy();
  });

  it('should set Error pages according to tab', function () {
    scope.numberOfPages = 5;
    scope.isDirty = true;
    scope.errorPages = {fullSupply:[5], nonFullSupply:[7]};
    scope.rnr.id = "rnrId";
    routeParams.page = 1;
    routeParams.supplyType = 'non-full-supply';
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200, {"success":"saved successfully"});
    scope.$broadcast("$routeUpdate");
    httpBackend.flush();
    expect(scope.shownErrorPages).toEqual(scope.errorPages.nonFullSupply);
  });

  it('should set showNonFullSupply flag if supply type is non-full-supply', function () {
    scope.numberOfPages = 5;
    scope.isDirty = true;
    scope.rnr.id = "rnrId";
    routeParams.page = 1;
    routeParams.supplyType = 'non-full-supply';
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200, {"success":"saved successfully"});
    scope.$broadcast("$routeUpdate");
    httpBackend.flush();
    expect(scope.showNonFullSupply).toBeTruthy();
  });

  it('should approve a valid rnr', function () {
    scope.rnr = new Rnr({"id":"rnrId"}, []);
    spyOn(scope.rnr, 'validateFullSupplyForApproval').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupplyForApproval').andReturn('');
    httpBackend.expect('PUT', '/requisitions/rnrId/approve.json').respond({'success':"R&R approved successfully!"});

    scope.approveRnr();
    httpBackend.flush();

    expect(scope.$parent.message).toEqual("R&R approved successfully!");
  });

  it('should calculate number of pages for a pageSize of 2 and 4 lineItems', function () {
    requisition.fullSupplyLineItems = [
      {'id':1},
      {'id':2},
      {'id':3},
      {'id':4}
    ];
    ctrl = controller(ApproveRnrController, {$scope:scope, requisition:requisition, rnrColumns:programRnrColumnList, currency:'$', $location:location, $routeParams:routeParams});

    expect(2).toEqual(scope.numberOfPages);
  });

  it('should calculate number of pages for a pageSize of 2 and 4 nonFullSupplyLineItems', function () {
    routeParams.supplyType = 'non-full-supply';
    requisition.nonFullSupplyLineItems = [
      {'id':1},
      {'id':2},
      {'id':3},
      {'id':4}
    ];
    ctrl = controller(ApproveRnrController, {$scope:scope, requisition:requisition, rnrColumns:programRnrColumnList, currency:'$', $location:location, $routeParams:routeParams});

    expect(2).toEqual(scope.numberOfPages);
  });

  it('should determine lineItems to be displayed on page 1 for page size 2', function () {
    requisition.fullSupplyLineItems = [
      {'id':1},
      {'id':2},
      {'id':3},
      {'id':4}
    ];
    ctrl = controller(ApproveRnrController, {$scope:scope, requisition:requisition, rnrColumns:programRnrColumnList, currency:'$', $location:location, $routeParams:routeParams});

    expect(scope.pageLineItems[0].id).toEqual(1);
    expect(scope.pageLineItems[1].id).toEqual(2);
    expect(scope.pageLineItems.length).toEqual(2);
  });

  it('should determine lineItems to be displayed on page 2 for page size 2', function () {
    routeParams.page = 2;
    requisition.fullSupplyLineItems = [
      {'id':1},
      {'id':2},
      {'id':3},
      {'id':4}
    ];
    ctrl = controller(ApproveRnrController, {$scope:scope, requisition:requisition, rnrColumns:programRnrColumnList, currency:'$', $location:location, $routeParams:routeParams});

    expect(scope.pageLineItems[0].id).toEqual(3);
    expect(scope.pageLineItems[1].id).toEqual(4);
    expect(scope.pageLineItems.length).toEqual(2);
  });

  it('should set current page 1 if page not defined', function () {
    expect(scope.currentPage).toEqual(1);
  });

  it('should set current page to 1 if page not within valid range', function () {
    routeParams.page = -95;
    ctrl = controller(ApproveRnrController, {$scope:scope, requisition:requisition, rnrColumns:programRnrColumnList, currency:'$', $location:location, $routeParams:routeParams});

    expect(scope.currentPage).toEqual(1);
  });

  it('should save rnr on page change only if dirty', function () {
    scope.numberOfPages = 5;
    scope.isDirty = true;
    routeParams.page = 2;
    scope.rnr.id = "rnrId";
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200, {'success':"success message"});
    scope.$broadcast('$routeUpdate');
    httpBackend.flush();
    expect(scope.message).toEqual('success message');
  });

  it('should fill packs to ship based on approved quantity', function () {
    var lineItem = new RnrLineItem();
    lineItem.quantityApproved = '67';
    scope.rnr = new Rnr();
    scope.rnr.fullSupplyLineItems = [lineItem];
    spyOn(lineItem, 'fillPacksToShip');

    scope.fillPacksToShip(lineItem);

    expect(lineItem.fillPacksToShip).toHaveBeenCalled();
  });

  it('should remove non numeric characters from quantity approved before calculations', function () {
    var lineItem = new RnrLineItem();
    lineItem.quantityApproved = '67hj';
    scope.rnr = new Rnr();
    scope.rnr.fullSupplyLineItems = [lineItem];

    scope.fillPacksToShip(lineItem);

    expect(lineItem.quantityApproved).toEqual(67);
  });

  it('should set message while saving if set message flag true', function () {
    scope.rnr = {"id":"rnrId"};
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200, {'success':"success message"});
    scope.saveRnr(false);
    httpBackend.flush();
    expect(scope.message).toEqual('success message');
  });

  it('should not set message while saving if set message flag false', function () {
    scope.rnr = {"id":"rnrId"};
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200, {'success':"success message"});
    scope.saveRnr(true);
    httpBackend.flush();
    expect(scope.message).toEqual('');
  });

  it('should calculate pages which have errors on approve', function () {
    scope.rnr = new Rnr({"id":"1", "fullSupplyLineItems":[
      {id:1},
      {id:2},
      {id:3}
    ], period:{numberOfMonths:7}}, null);

    scope.pageSize = 5;
    spyOn(scope.rnr, 'getErrorPages').andReturn({nonFullSupply:[1, 2], fullSupply:[2, 4]});
    spyOn(scope.rnr, 'validateFullSupplyForApproval').andReturn("");
    spyOn(scope.rnr, 'validateNonFullSupplyForApproval').andReturn("some error");
    httpBackend.expect('PUT', '/requisitions/1/save.json').respond(200, {'success':"success message"});
    scope.approveRnr();

    expect(scope.errorPages).toEqual({nonFullSupply:[1, 2], fullSupply:[2, 4]});
    expect(scope.rnr.getErrorPages).toHaveBeenCalledWith(5);
  });

  it('should return true if error on full supply page', function () {
    scope.errorPages = {fullSupply:[1]};
    scope.showNonFullSupply = false;
    var result = scope.checkErrorOnPage(1);
    expect(result).toBeTruthy();
  });

  it('should return false if no error on full supply page', function () {
    scope.errorPages = {fullSupply:[]};
    scope.showNonFullSupply = false;
    var result = scope.checkErrorOnPage(1);
    expect(result).toBeFalsy();
  });

  it('should return true if error on non full supply page', function () {
    scope.errorPages = {nonFullSupply:[1]};
    scope.showNonFullSupply = true;
    var result = scope.checkErrorOnPage(1);
    expect(result).toBeTruthy();
  });

  it('should return false if no error on non full supply page', function () {
    scope.errorPages = {nonFullSupply:[]};
    scope.showNonFullSupply = true;
    var result = scope.checkErrorOnPage(1);
    expect(result).toBeFalsy();
  });

  it('should call toggle expand in a grid if collapsed', function () {
    var row = {collapsed:true, toggleExpand: function(){} };
    spyOn(row, 'toggleExpand');
    scope.rowToggle(row);
    expect(row.toggleExpand).toHaveBeenCalled();
  });

  it('should not call toggle expand in a grid if collapsed', function () {
    var row = {collapsed:false, toggleExpand: function(){} };
    spyOn(row, 'toggleExpand');
    scope.rowToggle(row);
    expect(row.toggleExpand.calls.length).toEqual(0);
  });

});
