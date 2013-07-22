/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */


describe('ViewRnrController', function () {
  var scope, httpBackend, controller, routeParams, requisition, location;
  var columns = [
    {"id": 1, "name": "productCode", "position": 1, "source": {"description": "Reference Data", "name": "REFERENCE", "code": "R"}, "sourceConfigurable": false, "label": "Product Code", "formula": "", "indicator": "O", "used": true, "visible": true, "mandatory": true, "description": "Unique identifier for each commodity", "formulaValidationRequired": true},
    {"id": 2, "name": "product", "position": 2, "source": {"description": "Reference Data", "name": "REFERENCE", "code": "R"}, "sourceConfigurable": false, "label": "Product", "formula": "", "indicator": "R", "used": true, "visible": true, "mandatory": true, "description": "Primary name of the product", "formulaValidationRequired": true},
    {"id": 3, "name": "dispensingUnit", "position": 3, "source": {"description": "Reference Data", "name": "REFERENCE", "code": "R"}, "sourceConfigurable": false, "label": "Unit/Unit of Issue", "formula": "", "indicator": "U", "used": true, "visible": true, "mandatory": false, "description": "Dispensing unit for this product", "formulaValidationRequired": true},
    {"id": 4, "name": "beginningBalance", "position": 4, "source": {"description": "User Input", "name": "USER_INPUT", "code": "U"}, "sourceConfigurable": false, "label": "Beginning Balance", "formula": "", "indicator": "A", "used": true, "visible": false, "mandatory": false, "description": "Stock in hand of previous period.This is quantified in dispensing units", "formulaValidationRequired": true},
    {"id": 7, "name": "lossesAndAdjustments", "position": 7, "source": {"description": "User Input", "name": "USER_INPUT", "code": "U"}, "sourceConfigurable": false, "label": "Total Losses / Adjustments", "formula": "D1 + D2+D3...DN", "indicator": "D", "used": true, "visible": true, "mandatory": false, "description": "All kind of looses/adjustments made at the facility", "formulaValidationRequired": true}
  ];
  var regimenColumns = [];
  var regimenTemplate = {regimenColumns: regimenColumns};

  beforeEach(module('openlmis.services'));
  beforeEach(inject(function ($httpBackend, $rootScope, $controller, $location) {
    routeParams = {'programId': 2, 'rnr': 1, 'supplyType': 'full-supply'};
    scope = $rootScope.$new();
    httpBackend = $httpBackend;
    controller = $controller;
    location = $location;
    requisition = {lineItems: [], nonFullSupplyLineItems: [], regimenLineItems: [], period: {numberOfMonths: 3}};
    scope.pageSize = 2;
    routeParams.page = 1;
  }));


  it('should include approved quantity column if status approved', function () {
    columns.push({'name': 'quantityApproved', 'label': 'Approved Quantity', id: '99', visible: true});
    requisition = {lineItems: [], nonFullSupplyLineItems: [], regimenLineItems: [], status: 'APPROVED'};
    controller(ViewRnrController, {$scope: scope, $routeParams: routeParams, requisition: requisition, currency: {},
      pageSize: pageSize, rnrColumns: columns, regimenTemplate: regimenTemplate});

    expect(scope.visibleColumns.length).toEqual(5);
  });

  it('should include approved quantity column if status  ordered', function () {
    requisition = {lineItems: [], nonFullSupplyLineItems: [], regimenLineItems: [], status: 'RELEASED'};
    controller(ViewRnrController, {$scope: scope, $routeParams: routeParams, requisition: requisition, currency: {},
      pageSize: pageSize, rnrColumns: columns, regimenTemplate: regimenTemplate});

    expect(scope.visibleColumns.length).toEqual(5);
  });

  it('should not include approved quantity column if status not approved nor ordered', function () {
    requisition = {lineItems: [], nonFullSupplyLineItems: [], regimenLineItems: [], status: 'INITIATED'};
    controller(ViewRnrController, {$scope: scope, $routeParams: routeParams, requisition: requisition, currency: {},
      pageSize: pageSize, rnrColumns: columns, regimenTemplate: regimenTemplate});

    expect(scope.visibleColumns.length).toEqual(4);
  });

  it('should assign line items based on supply type', function () {
    routeParams.supplyType = 'full-supply';
    var rnr = {fullSupplyLineItems: [
      {'id': 1}
    ], nonFullSupplyLineItems: [], regimenLineItems: [], period: {numberOfMonths: 5}, status: 'INITIATED'};
    controller(ViewRnrController, {$scope: scope, $routeParams: routeParams, requisition: rnr, currency: {},
      pageSize: pageSize, rnrColumns: columns, regimenTemplate: regimenTemplate});
    expect(rnr.fullSupplyLineItems.length).toEqual(scope.pageLineItems.length);
  });

  it('should assign non full supply line items based on supply type', function () {
    routeParams.supplyType = 'non-full-supply';
    var rnr = {fullSupplyLineItems: [], nonFullSupplyLineItems: [
      {'id': 1}
    ], regimenLineItems: [], period: {numberOfMonths: 5}, status: 'INITIATED'};
    controller(ViewRnrController, {$scope: scope, $routeParams: routeParams, requisition: rnr, currency: {},
      pageSize: pageSize, rnrColumns: columns, regimenTemplate: regimenTemplate});
    expect(rnr.nonFullSupplyLineItems.length).toEqual(scope.pageLineItems.length);
  });

  it('should calculate number of pages for a pageSize of 2 and 4 lineItems', function () {
    requisition.fullSupplyLineItems = [
      {'id': 1},
      {'id': 2},
      {'id': 3},
      {'id': 4}
    ];
    controller(ViewRnrController, {$scope: scope, requisition: requisition, rnrColumns: columns, currency: '$', pageSize: pageSize,
      $location: location, $routeParams: routeParams, regimenTemplate: regimenTemplate});

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
    controller(ViewRnrController, {$scope: scope, requisition: requisition, rnrColumns: columns, currency: '$', pageSize: pageSize,
      $location: location, $routeParams: routeParams, regimenTemplate: regimenTemplate});

    expect(2).toEqual(scope.numberOfPages);
  });

  it('should determine lineItems to be displayed on page 1 for page size 2', function () {
    requisition.fullSupplyLineItems = [
      {'id': 1},
      {'id': 2},
      {'id': 3},
      {'id': 4}
    ];
    controller(ViewRnrController, {$scope: scope, requisition: requisition, rnrColumns: columns, currency: '$', pageSize: pageSize,
      $location: location, $routeParams: routeParams, regimenTemplate: regimenTemplate});

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
    controller(ViewRnrController, {$scope: scope, requisition: requisition, rnrColumns: columns, currency: '$', pageSize: pageSize,
      $location: location, $routeParams: routeParams, regimenTemplate: regimenTemplate});

    expect(scope.pageLineItems[0].id).toEqual(3);
    expect(scope.pageLineItems[1].id).toEqual(4);
    expect(scope.pageLineItems.length).toEqual(2);
  });

  it('should determine lineItems to be displayed on page 2 after page changes to 2', function () {
    routeParams.page = 1;
    requisition.fullSupplyLineItems = [
      {'id': 1},
      {'id': 2},
      {'id': 3},
      {'id': 4}
    ];
    controller(ViewRnrController, {$scope: scope, requisition: requisition, rnrColumns: columns, currency: '$', pageSize: pageSize,
      $location: location, $routeParams: routeParams, regimenTemplate: regimenTemplate});

    routeParams.page = 2;
    scope.$broadcast('$routeUpdate');

    expect(scope.pageLineItems[0].id).toEqual(3);
    expect(scope.pageLineItems[1].id).toEqual(4);
    expect(scope.pageLineItems.length).toEqual(2);
  });

  it('should change page in url if current page changes', function () {
    routeParams.page = 1;
    requisition.fullSupplyLineItems = [
      {'id': 1},
      {'id': 2},
      {'id': 3},
      {'id': 4}
    ];
    controller(ViewRnrController, {$scope: scope, requisition: requisition, rnrColumns: columns, currency: '$', pageSize: pageSize,
      $location: location, $routeParams: routeParams, regimenTemplate: regimenTemplate});
    spyOn(location, 'search').andCallThrough();
    scope.currentPage = 2;
    scope.$digest();

    expect(location.search).toHaveBeenCalledWith('page', 2);
  });

  it('should validate page and navigate to page 1 if invalid', function () {
    routeParams.page = "blah";
    requisition.fullSupplyLineItems = [
      {'id': 1},
      {'id': 2},
      {'id': 3},
      {'id': 4}
    ];
    spyOn(location, 'search').andCallThrough();

    controller(ViewRnrController, {$scope: scope, requisition: requisition, rnrColumns: columns, currency: '$', pageSize: pageSize,
      $location: location, $routeParams: routeParams, regimenTemplate: regimenTemplate});
    scope.$digest();
    expect(location.search).toHaveBeenCalledWith('page', 1);
  });

  it('should validate tab and navigate to full supply tab page 1 if invalid', function () {
    routeParams.supplyType = "blah";

    requisition.fullSupplyLineItems = [
      {'id': 1},
      {'id': 2},
      {'id': 3},
      {'id': 4}
    ];
    spyOn(location, 'search').andCallThrough();

    controller(ViewRnrController, {$scope: scope, requisition: requisition, rnrColumns: columns, currency: '$', pageSize: pageSize,
      $location: location, $routeParams: routeParams, regimenTemplate: regimenTemplate});
    scope.$broadcast('$routeUpdate');
    expect(location.search).toHaveBeenCalledWith('supplyType', 'full-supply');
  });


});
