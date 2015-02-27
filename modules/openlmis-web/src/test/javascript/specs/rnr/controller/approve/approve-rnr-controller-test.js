/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('Approve Requisition controller', function () {

  var scope, ctrl, httpBackend, location, routeParams, controller, requisition, regimenTemplate,
    programRnrColumnList, nonFullSupplyLineItems, lineItems, regimenLineItems, equipmentLineItems, dialog, rnrLineItem, regimenColumns, requisitionService, pageSize;
  beforeEach(module('openlmis'));
  beforeEach(module('ui.bootstrap.dialog'));

  beforeEach(inject(function ($httpBackend, $rootScope, $location, $controller, _messageService_, _requisitionService_) {
    scope = $rootScope.$new();
    location = $location;
    controller = $controller;
    httpBackend = $httpBackend;
    requisitionService = _requisitionService_;
    spyOn(requisitionService, 'populateScope');
    spyOn(_messageService_, 'get').andCallFake(function (arg) {
      if (arg == 'label.currency.symbol') {
        return '$';
      }
      return ':P';
    });
    routeParams = {"rnr": "1", "program": "1", "supplyType": "fullSupply"};
    lineItems = [];
    nonFullSupplyLineItems = [];
    regimenLineItems = [];
    equipmentLineItems = [];
    requisition = {'status': "AUTHORIZED", 'lineItems': lineItems, 'nonFullSupplyLineItems': nonFullSupplyLineItems,
      regimenLineItems: regimenLineItems, equipmentLineItems: equipmentLineItems, period: {numberOfMonths: 5}};
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
    pageSize = 2;
    regimenTemplate = {regimenColumns: regimenColumns};
    rnrLineItem = new RegularRnrLineItem({"fullSupply": true});
    ctrl = controller(ApproveRnrController, {$scope: scope, requisitionData: {rnr: requisition, canApproveRnr: true, numberOfMonths: 5}, rnrColumns: programRnrColumnList,
      regimenTemplate: regimenTemplate, pageSize: pageSize, $location: location, $routeParams: routeParams, requisitionService: requisitionService,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      comments: [],
      equipmentOperationalStatus:[]});
  }));

  it('should set rnr in scope', function () {
    var spyOnRnr = spyOn(window, 'Rnr').andCallThrough();
    var requisitionData = {rnr: requisition, canApproveRnr: true};
    ctrl = controller(ApproveRnrController, {$scope: scope, requisitionData: requisitionData, rnrColumns: programRnrColumnList, regimenTemplate: regimenTemplate,
      currency: '$', pageSize: pageSize, $location: location, $routeParams: routeParams,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      comments: [],
      equipmentOperationalStatus:[]});
    //expect(spyOnRnr).toHaveBeenCalledWith(requisitionData.rnr, programRnrColumnList, requisitionData.numberOfMonths);
  });

  it('should set scope variables', function () {
    expect(requisitionService.populateScope).toHaveBeenCalledWith(scope, location, routeParams);
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
    scope.approvalForm.$dirty = true;
    scope.pageLineItems = [rnrLineItem];

    spyOn(scope.rnr, 'validateFullSupplyForApproval').andReturn('some error');
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200);

    scope.approveRnr();
    httpBackend.flush();

    expect(scope.fullSupplyTabError).toBeTruthy();
    expect(scope.error).toEqual("some error");
  });

  it('should not approve if any non full supply line item has empty approved quantity but should save', function () {
    scope.rnr = new Rnr({"id": "rnrId"});
    scope.pageLineItems = [rnrLineItem];
    scope.approvalForm.$dirty = true;

    spyOn(scope.rnr, 'validateFullSupplyForApproval').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupplyForApproval').andReturn('some error');
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200);

    scope.approveRnr();
    httpBackend.flush();

    expect(scope.nonFullSupplyTabError).toBeTruthy();
    expect(scope.error).toEqual("some error");
  });

  it('should reset visible tab if supply type is not specified', function () {
    expect(scope.visibleTab).toEqual('fullSupply');
  });

  it('should set visible tab to fullSupply if supply type is fullSupply', function () {
    routeParams.supplyType = 'fullSupply';
    scope.$broadcast("$routeUpdate");
    expect(scope.visibleTab).toEqual("fullSupply");
  });

  it('should retain error pages on route change', function () {
    scope.numberOfPages = 5;
    scope.approvalForm.$dirty = true;
    scope.approvalForm.$setPristine = function () {
      scope.approvalForm.pristine = true;
    };
    scope.errorPages = {fullSupply: [5], nonFullSupply: [7]};
    scope.rnr.id = "rnrId";
    routeParams.page = 1;
    routeParams.supplyType = 'nonFullSupply';
    scope.page.nonFullSupply = [rnrLineItem];
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200, {"success": "saved successfully"});
    scope.$broadcast("$routeUpdate");
    httpBackend.flush();
    expect(scope.errorPages.nonFullSupply).toEqual([7]);
    expect(scope.errorPages.fullSupply).toEqual([5]);
  });

  it('should set showNonFullSupply flag if supply type is nonFullSupply', function () {
    scope.numberOfPages = 5;
    scope.approvalForm.$dirty = true;
    scope.approvalForm.$setPristine = function () {
      scope.approvalForm.pristine = true
    };
    scope.rnr.id = "rnrId";
    routeParams.page = 1;
    routeParams.supplyType = 'nonFullSupply';
    scope.pageLineItems = [rnrLineItem];
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200, {"success": "saved successfully"});
    scope.$broadcast("$routeUpdate");
    httpBackend.flush();
    expect(scope.visibleTab).toEqual('nonFullSupply');
  });

  it('should display confirm modal if approve button is clicked on valid Rnr', function () {
    scope.rnr = new Rnr({"id": "rnrId"}, []);
    scope.pageLineItems = [rnrLineItem];

    spyOn(scope.rnr, 'validateFullSupplyForApproval').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupplyForApproval').andReturn('');
    httpBackend.expectGET('/public/pages/template/dialog/dialogbox.html').respond(200);

    scope.approveRnr();
    httpBackend.flush();
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
    var requisitionData = {rnr: requisition, canApproveRnr: true};

    ctrl = controller(ApproveRnrController, {$scope: scope, requisitionData: requisitionData, rnrColumns: programRnrColumnList, currency: '$', pageSize: pageSize,
      regimenTemplate: regimenTemplate, $location: location, $routeParams: routeParams,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      comments: [],
      equipmentOperationalStatus:[]});

    expect(2).toEqual(scope.numberOfPages);
  });

  it('should calculate number of pages for a pageSize of 2 and 4 nonFullSupplyLineItems', function () {
    routeParams.supplyType = 'nonFullSupply';
    requisition.nonFullSupplyLineItems = [
      {'id': 1},
      {'id': 2},
      {'id': 3},
      {'id': 4}
    ];
    var requisitionData = {rnr: requisition, canApproveRnr: true};

    ctrl = controller(ApproveRnrController, {$scope: scope, requisitionData: requisitionData, rnrColumns: programRnrColumnList, currency: '$', pageSize: pageSize,
      regimenTemplate: regimenTemplate, $location: location, $routeParams: routeParams,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      comments: [],
      equipmentOperationalStatus:[]});

    expect(2).toEqual(scope.numberOfPages);
  });

  it('should determine lineItems to be displayed on page 1 for page size 2', function () {
    requisition.fullSupplyLineItems = [
      {'id': 1},
      {'id': 2},
      {'id': 3},
      {'id': 4}
    ];
    var requisitionData = {rnr: requisition, canApproveRnr: true};

    ctrl = controller(ApproveRnrController, {$scope: scope, requisitionData: requisitionData, rnrColumns: programRnrColumnList, currency: '$', pageSize: pageSize,
      regimenTemplate: regimenTemplate, $location: location, $routeParams: routeParams,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      comments: [],
      equipmentOperationalStatus:[]});

    expect(scope.page.fullSupply[0].id).toEqual(1);
    expect(scope.page.fullSupply[1].id).toEqual(2);
    expect(scope.page.fullSupply.length).toEqual(2);
  });

  it('should determine lineItems to be displayed on page 2 for page size 2', function () {
    routeParams.page = 2;
    requisition.fullSupplyLineItems = [
      {'id': 1},
      {'id': 2},
      {'id': 3},
      {'id': 4}
    ];
    var requisitionData = {rnr: requisition, canApproveRnr: true};

    ctrl = controller(ApproveRnrController, {$scope: scope, requisitionData: requisitionData, rnrColumns: programRnrColumnList, currency: '$', pageSize: pageSize,
      regimenTemplate: regimenTemplate, $location: location, $routeParams: routeParams,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      comments: [],
      equipmentOperationalStatus:[]});

    expect(scope.page.fullSupply[0].id).toEqual(3);
    expect(scope.page.fullSupply[1].id).toEqual(4);
    expect(scope.page.fullSupply.length).toEqual(2);
  });

  it('should set current page 1 if page not defined', function () {
    expect(scope.currentPage).toEqual(1);
  });

  it('should set current page to 1 if page not within valid range', function () {
    routeParams.page = -95;
    var requisitionData = {rnr: requisition, canApproveRnr: true};

    ctrl = controller(ApproveRnrController, {$scope: scope, requisitionData: requisitionData, rnrColumns: programRnrColumnList, currency: '$', pageSize: pageSize,
      regimenTemplate: regimenTemplate, $location: location, $routeParams: routeParams,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      comments: [],
      equipmentOperationalStatus:[]});

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
    scope.approvalForm.$dirty = true;

    spyOn(scope.rnr, 'getErrorPages').andReturn({nonFullSupply: [1, 2], fullSupply: [2, 4], regimen: []});
    spyOn(scope.rnr, 'validateFullSupplyForApproval').andReturn("");
    spyOn(scope.rnr, 'validateNonFullSupplyForApproval').andReturn("some error");

    httpBackend.expect('PUT', '/requisitions/1/save.json').respond(200, {'success': "success message"});

    scope.approveRnr();
    httpBackend.flush();

    expect(scope.errorPages).toEqual({nonFullSupply: [1, 2], fullSupply: [2, 4], regimen: []});
    expect(scope.rnr.getErrorPages).toHaveBeenCalledWith(5);
  });

  it('should return true if error on full supply page', function () {
    scope.errorPages = {fullSupply: [1]};
    scope.visibleTab = 'fullSupply';
    var result = scope.checkErrorOnPage(1);
    expect(result).toBeTruthy();
  });

  it('should return false if no error on full supply page', function () {
    scope.errorPages = {fullSupply: []};
    scope.visibleTab = 'fullSupply';
    var result = scope.checkErrorOnPage(1);
    expect(result).toBeFalsy();
  });

  it('should return true if error on non full supply page', function () {
    scope.errorPages = {nonFullSupply: [1]};
    scope.visibleTab = 'nonFullSupply';
    var result = scope.checkErrorOnPage(1);
    expect(result).toBeTruthy();
  });

  it('should return false if no error on non full supply page', function () {
    scope.errorPages = {nonFullSupply: []};
    scope.visibleTab = 'nonFullSupply';
    var result = scope.checkErrorOnPage(1);
    expect(result).toBeFalsy();
  });
});
