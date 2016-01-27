/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */


describe('ViewRnrController', function () {
  var scope, httpBackend, controller, routeParams, requisition, location, messageService, requisitionService, pageSize;
  var columns = [
    {"id": 1, "name": "productCode", "position": 1, "source": {"description": "Reference Data", "name": "REFERENCE", "code": "R"}, "sourceConfigurable": false, "label": "Product Code", "formula": "", "indicator": "O", "used": true, "visible": true, "mandatory": true, "description": "Unique identifier for each commodity", "formulaValidationRequired": true},
    {"id": 2, "name": "product", "position": 2, "source": {"description": "Reference Data", "name": "REFERENCE", "code": "R"}, "sourceConfigurable": false, "label": "Product", "formula": "", "indicator": "R", "used": true, "visible": true, "mandatory": true, "description": "Primary name of the product", "formulaValidationRequired": true},
    {"id": 3, "name": "dispensingUnit", "position": 3, "source": {"description": "Reference Data", "name": "REFERENCE", "code": "R"}, "sourceConfigurable": false, "label": "Unit/Unit of Issue", "formula": "", "indicator": "U", "used": true, "visible": true, "mandatory": false, "description": "Dispensing unit for this product", "formulaValidationRequired": true},
    {"id": 4, "name": "beginningBalance", "position": 4, "source": {"description": "User Input", "name": "USER_INPUT", "code": "U"}, "sourceConfigurable": false, "label": "Beginning Balance", "formula": "", "indicator": "A", "used": true, "visible": false, "mandatory": false, "description": "Stock in hand of previous period.This is quantified in dispensing units", "formulaValidationRequired": true},
    {"id": 7, "name": "lossesAndAdjustments", "position": 7, "source": {"description": "User Input", "name": "USER_INPUT", "code": "U"}, "sourceConfigurable": false, "label": "Total Losses / Adjustments", "formula": "D1 + D2+D3...DN", "indicator": "D", "used": true, "visible": true, "mandatory": false, "description": "All kind of looses/adjustments made at the facility", "formulaValidationRequired": true}
  ];
  var regimenColumns = [];
  var regimenTemplate = {regimenColumns: regimenColumns};

  beforeEach(module('openlmis'));
  beforeEach(inject(function ($httpBackend, $rootScope, $controller, $location, _messageService_, _requisitionService_) {
    routeParams = {'programId': 2, 'rnr': 1, 'supplyType': 'fullSupply'};
    scope = $rootScope.$new();
    requisitionService = _requisitionService_;
    spyOn(requisitionService, 'getMappedVisibleColumns');
    httpBackend = $httpBackend;
    controller = $controller;
    location = $location;
    requisition = {lineItems: [], nonFullSupplyLineItems: [], regimenLineItems: [], equipmentLineItems :[], period: {numberOfMonths: 3}};
    pageSize = 2;
    routeParams.page = 1;
    messageService = _messageService_;
  }));


  it('should include approved quantity column if status approved', function () {
    columns.push({'name': 'quantityApproved', 'label': 'Approved Quantity', id: '99', visible: true});
    requisition = {lineItems: [], nonFullSupplyLineItems: [], regimenLineItems: [], equipmentLineItems :[], status: 'APPROVED'};
    controller(ViewRnrController, {$scope: scope, $routeParams: routeParams, requisitionData: {rnr: requisition}, currency: {},
      pageSize: pageSize, rnrColumns: columns, regimenTemplate: regimenTemplate,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      enableSkipPeriod: false,
      $dialog: {},
      comments: [],
      equipmentOperationalStatus:[]});

    var expectedColumns = angular.copy(columns);

    expect(requisitionService.getMappedVisibleColumns).toHaveBeenCalledWith(expectedColumns, jasmine.any(Array), jasmine.any(Array));
  });

  it('should include approved quantity column if status released', function () {
    requisition = {lineItems: [], nonFullSupplyLineItems: [], regimenLineItems: [], equipmentLineItems :[], status: 'RELEASED'};
    controller(ViewRnrController, {$scope: scope, $routeParams: routeParams, requisitionData: {rnr: requisition}, currency: {},
      pageSize: pageSize, rnrColumns: columns, regimenTemplate: regimenTemplate,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      enableSkipPeriod: false,
      $dialog: {},
      comments: [],
      equipmentOperationalStatus:[]});

    var expectedColumns = angular.copy(columns);

    expect(requisitionService.getMappedVisibleColumns).toHaveBeenCalledWith(expectedColumns, jasmine.any(Array), jasmine.any(Array));
  });

  it('should not include approved quantity column if status not approved nor released', function () {
    requisition = {lineItems: [], nonFullSupplyLineItems: [], regimenLineItems: [], equipmentLineItems :[], status: 'INITIATED'};
    controller(ViewRnrController, {$scope: scope, $routeParams: routeParams, requisitionData: {rnr: requisition}, currency: {},
      pageSize: pageSize, rnrColumns: columns, regimenTemplate: regimenTemplate,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      enableSkipPeriod: false,
      $dialog: {},
      comments: [],
      equipmentOperationalStatus:[]});

    var expectedColumns = angular.copy(columns);
    expectedColumns.splice(5, 1);

    expect(requisitionService.getMappedVisibleColumns).toHaveBeenCalledWith(expectedColumns, jasmine.any(Array), jasmine.any(Array));
  });

  it('should assign line items based on supply type', function () {
    routeParams.supplyType = 'fullSupply';
    var rnr = {fullSupplyLineItems: [
      {'id': 1}
    ], nonFullSupplyLineItems: [], regimenLineItems: [], equipmentLineItems :[], period: {numberOfMonths: 5}, status: 'INITIATED'};
    controller(ViewRnrController, {$scope: scope, $routeParams: routeParams, requisitionData: {rnr: rnr}, currency: {},
      pageSize: pageSize, rnrColumns: columns, regimenTemplate: regimenTemplate,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      enableSkipPeriod: false,
      $dialog: {},
      comments: [],
      equipmentOperationalStatus:[]});
    expect(rnr.fullSupplyLineItems.length).toEqual(scope.page.fullSupply.length);
  });

  it('should set requisition type as Regular for regular requisition', function () {
    var rnr = {emergency: false, fullSupplyLineItems: [
      {'id': 1}
    ], nonFullSupplyLineItems: [], regimenLineItems: [], equipmentLineItems :[], period: {numberOfMonths: 5}, status: 'INITIATED'};

    routeParams.supplyType = 'fullSupply';

    spyOn(messageService, "get").andReturn("requisition.type.regular");

    controller(ViewRnrController, {$scope: scope, $routeParams: routeParams, requisitionData: {rnr: rnr}, currency: {},
      pageSize: pageSize, rnrColumns: columns, regimenTemplate: regimenTemplate,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      enableSkipPeriod: false,
      $dialog: {},
      comments: [],
      equipmentOperationalStatus:[]});

    expect(scope.requisitionType).toEqual("requisition.type.regular");
  });


  it('should set requisition type as Emergency for emergency requisition', function () {
    var rnr = {emergency: true, fullSupplyLineItems: [
      {'id': 1}
    ], nonFullSupplyLineItems: [], regimenLineItems: [], equipmentLineItems :[], period: {numberOfMonths: 5}, status: 'INITIATED'};

    routeParams.supplyType = 'fullSupply';


    controller(ViewRnrController, {$scope: scope, $routeParams: routeParams, requisitionData: {rnr: rnr}, currency: {},
      pageSize: pageSize, rnrColumns: columns, regimenTemplate: regimenTemplate,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      enableSkipPeriod: false,
      $dialog: {},
      comments: [],
      equipmentOperationalStatus:[]});

    expect(scope.requisitionType).toEqual("requisition.type.emergency");
  });

  it('should assign non full supply line items based on supply type', function () {
    routeParams.supplyType = 'nonFullSupply';
    var rnr = {fullSupplyLineItems: [], nonFullSupplyLineItems: [
      {'id': 1}
    ], regimenLineItems: [], equipmentLineItems :[], period: {numberOfMonths: 5}, status: 'INITIATED'};
    controller(ViewRnrController, {$scope: scope, $routeParams: routeParams, requisitionData: {rnr: rnr}, currency: {},
      pageSize: pageSize, rnrColumns: columns, regimenTemplate: regimenTemplate,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      enableSkipPeriod: false,
      $dialog: {},
      comments: [],
      equipmentOperationalStatus:[]});
    expect(rnr.nonFullSupplyLineItems.length).toEqual(scope.page.nonFullSupply.length);
  });

  it('should calculate number of pages for a pageSize of 2 and 4 lineItems', function () {
    requisition.fullSupplyLineItems = [
      {'id': 1},
      {'id': 2},
      {'id': 3},
      {'id': 4}
    ];
    controller(ViewRnrController, {$scope: scope, requisitionData: {rnr: requisition}, rnrColumns: columns, currency: '$', pageSize: pageSize,
      $location: location, $routeParams: routeParams, regimenTemplate: regimenTemplate,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      enableSkipPeriod: false,
      $dialog: {},
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

    controller(ViewRnrController, {$scope: scope, requisitionData: {rnr: requisition}, rnrColumns: columns, currency: '$', pageSize: pageSize,
      $location: location, $routeParams: routeParams, regimenTemplate: regimenTemplate,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      enableSkipPeriod: false,
      $dialog: {},
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

    controller(ViewRnrController, {$scope: scope, requisitionData: {rnr: requisition}, rnrColumns: columns, currency: '$', pageSize: pageSize,
      $location: location, $routeParams: routeParams, regimenTemplate: regimenTemplate,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      enableSkipPeriod: false,
      $dialog: {},
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

    controller(ViewRnrController, {$scope: scope, requisitionData: {rnr: requisition}, rnrColumns: columns, currency: '$', pageSize: pageSize,
      $location: location, $routeParams: routeParams, regimenTemplate: regimenTemplate,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      enableSkipPeriod: false,
      $dialog: {},
      comments: [],
      equipmentOperationalStatus:[]});

    expect(scope.page.fullSupply[0].id).toEqual(3);
    expect(scope.page.fullSupply[1].id).toEqual(4);
    expect(scope.page.fullSupply.length).toEqual(2);
  });

  it('should determine lineItems to be displayed on page 2 after page changes to 2', function () {
    routeParams.page = 1;
    requisition.fullSupplyLineItems = [
      {'id': 1},
      {'id': 2},
      {'id': 3},
      {'id': 4}
    ];

    controller(ViewRnrController, {$scope: scope, requisitionData: {rnr: requisition}, rnrColumns: columns, currency: '$', pageSize: pageSize,
      $location: location, $routeParams: routeParams, regimenTemplate: regimenTemplate,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      enableSkipPeriod: false,
      $dialog: {},
      comments: [],
      equipmentOperationalStatus:[]});

    routeParams.page = 2;
    scope.$broadcast('$routeUpdate');

    expect(scope.page.fullSupply[0].id).toEqual(3);
    expect(scope.page.fullSupply[1].id).toEqual(4);
    expect(scope.page.fullSupply.length).toEqual(2);
  });

  it('should change page in url if current page changes', function () {
    routeParams.page = 1;
    requisition.fullSupplyLineItems = [
      {'id': 1},
      {'id': 2},
      {'id': 3},
      {'id': 4}
    ];

    controller(ViewRnrController, {$scope: scope, requisitionData: {rnr: requisition}, rnrColumns: columns, currency: '$', pageSize: pageSize,
      $location: location, $routeParams: routeParams, regimenTemplate: regimenTemplate,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      enableSkipPeriod: false,
      $dialog: {},
      comments: [],
      equipmentOperationalStatus:[]});
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

    controller(ViewRnrController, {$scope: scope, requisitionData: {rnr: requisition}, rnrColumns: columns, currency: '$', pageSize: pageSize,
      $location: location, $routeParams: routeParams, regimenTemplate: regimenTemplate,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      enableSkipPeriod: false,
      $dialog: {},
      comments: [],
      equipmentOperationalStatus:[]});
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

    controller(ViewRnrController, {$scope: scope, requisitionData: {rnr: requisition}, rnrColumns: columns, currency: '$', pageSize: pageSize,
      $location: location, $routeParams: routeParams, regimenTemplate: regimenTemplate,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      enableSkipPeriod: false,
      $dialog: {},
      comments: [],
      equipmentOperationalStatus:[]});
    scope.$broadcast('$routeUpdate');
    expect(location.search).toHaveBeenCalledWith('supplyType', 'fullSupply');
  });


});
