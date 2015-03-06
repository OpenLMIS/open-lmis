/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('CreateRequisitionController', function () {

  var scope, rootScope, ctrl, httpBackend, location, routeParams, controller, localStorageService, mockedRequisition, rnrColumns, regimenColumnList, pageSize,
    lossesAndAdjustmentTypes, facilityApprovedProducts, requisitionRights, rnrLineItem, messageService, regimenTemplate, requisitionService, categoryList, requisitions;
  beforeEach(module('openlmis'));

  beforeEach(module('ui.bootstrap.dialog'));

  beforeEach(inject(function ($httpBackend, $rootScope, $location, $controller, $routeParams, _localStorageService_, _messageService_, _requisitionService_, Requisitions) {
    scope = $rootScope.$new();
    rootScope = $rootScope;
    requisitionService = _requisitionService_;
    $rootScope.hasPermission = function () {
    };

    requisitions = Requisitions;
    spyOn(Requisitions, 'update').andCallThrough();
    location = $location;
    controller = $controller;
    httpBackend = $httpBackend;
    messageService = _messageService_;
    spyOn(messageService, 'get').andCallFake(function (arg) {
      if (arg == 'label.currency.symbol')
        return '$';
      return 'some message'
    });
    scope.$parent.facility = "10134";
    scope.$parent.program = {code: "programCode", "id": 1};

    scope.saveRnrForm = {$error: { rnrError: false }};
    localStorageService = _localStorageService_;
    routeParams = {"facility": "1", "program": "1", "period": 2};
    scope.rnr = {"id": "rnrId", "fullSupplyLineItems": [], equipmentLineItems: []};
    mockedRequisition = {'status': "INITIATED",
      fullSupplyItemsSubmittedCost: 100,
      nonFullSupplyItemsSubmittedCost: 14,
      period: {numberOfMonths: 5},
      fullSupplyLineItems: [
        {id: 1}
      ],
      nonFullSupplyLineItems: [
        {id: 2}
      ],
      regimenLineItems: [],
      equipmentLineItems: []
    };

    rnrColumns = [
      {"testField": "test"}
    ];

    regimenColumnList = [
      {name: "patientsOnTreatment", visible: true},
      {name: "patientsToInitiateTreatment", visible: false},
      {name: "patientsStoppedTreatment", visible: true}
    ];
    regimenTemplate = {columns: regimenColumnList};

    lossesAndAdjustmentTypes = {"lossAdjustmentTypes": {"name": "damaged"}};

    var category1 = {"id": 1, "name": "cat1", "code": "cat1Code"};
    var category2 = {"id": 2, "name": "cat2", "code": "cat2Code"};
    var category3 = {"id": 3, "name": "cat3", "code": "cat3Code"};
    categoryList = [category1, category2, category3];

    var product1 = {"id": 1, "code": "product1", "category": category1};
    var product2 = {"id": 2, "code": "product2", "category": category2};
    var product3 = {"id": 3, "code": "product3", "category": category3};
    var product4 = {"id": 4, "code": "product4", "category": category1};
    var product5 = {"id": 5, "code": "product5", "category": category2};

    var facilityApprovedProduct1 = {"programProduct": {"product": product1}};
    var facilityApprovedProduct2 = {"programProduct": {"product": product2}};
    var facilityApprovedProduct3 = {"programProduct": {"product": product3}};
    var facilityApprovedProduct4 = {"programProduct": {"product": product4}};
    var facilityApprovedProduct5 = {"programProduct": {"product": product5}};

    facilityApprovedProducts = [facilityApprovedProduct1, facilityApprovedProduct2, facilityApprovedProduct3, facilityApprovedProduct4, facilityApprovedProduct5];

    httpBackend.when('GET', '/rnr/1/columns.json').respond(rnrColumns);
    httpBackend.when('GET', '/programId/1/regimenColumns.json').respond(regimenColumnList);
    httpBackend.when('GET', '/reference-data/currency.json').respond({"currency": "$"});
    httpBackend.when('GET', '/requisitions/lossAndAdjustments/reference-data.json').respond(200, lossesAndAdjustmentTypes);
    httpBackend.when('GET', '/facilityApprovedProducts/facility/1/program/1/nonFullSupply.json').respond(200, {"nonFullSupplyProducts": facilityApprovedProducts});

    $rootScope.fixToolBar = function () {
    };
    rnrLineItem = new RegularRnrLineItem({"fullSupply": true});

    requisitionRights = [
      {name: 'CREATE_REQUISITION'},
      {name: 'AUTHORIZE_REQUISITION'}
    ];

    pageSize = "2";

    ctrl = controller(CreateRequisitionController, {$scope: scope, $location: location, requisitionData: {rnr: mockedRequisition},
      rnrColumns: rnrColumns, regimenTemplate: regimenTemplate, currency: '$', lossesAndAdjustmentsTypes: lossesAndAdjustmentTypes,
      facilityApprovedProducts: facilityApprovedProducts, requisitionRights: requisitionRights, $routeParams: routeParams,
      $rootScope: rootScope, localStorageService: localStorageService, pageSize: pageSize,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      enableSkipPeriod: false,
      comments: [],
      equipmentOperationalStatus:[]});

  }));

  it("should toggle skip flag for all rnrLineItems", function () {
    var lineItem1 = {skipped: true, canSkip: function(){return true}};
    lineItem1.cost = 100;
    var lineItem2 = {skipped: true, canSkip: function(){return true}};
    lineItem2.cost = 200;

    scope.page = {fullSupply: [lineItem1, lineItem2]};
    scope.rnr = new Rnr({"id": "rnrId", skipAll: false});
    spyOn(scope.rnr, 'calculateFullSupplyItemsSubmittedCost');

    scope.toggleSkipFlag();

    expect(lineItem1.skipped).toBeFalsy();
    expect(lineItem2.skipped).toBeFalsy();
    expect(scope.rnr.calculateFullSupplyItemsSubmittedCost).toHaveBeenCalled();

    scope.rnr.skipAll = true;

    scope.toggleSkipFlag();

    expect(lineItem1.skipped).toBeTruthy();
    expect(lineItem2.skipped).toBeTruthy();
    expect(scope.rnr.calculateFullSupplyItemsSubmittedCost).toHaveBeenCalled();
  });

  it('should get list of Rnr Columns for program', function () {
    expect([
      {"testField": "test"}
    ]).toEqual(scope.programRnrColumnList);
  });

  it('should get lossesAndAdjustments types', function () {
    expect(lossesAndAdjustmentTypes).toEqual(scope.lossesAndAdjustmentTypes);
  });

  it('should get facility approved products', function () {
    expect(facilityApprovedProducts).toEqual(scope.facilityApprovedProducts);
  });

  it('should set visible columns for regimen', function () {
    expect(scope.visibleRegimenColumns).toEqual([
      {name: "patientsOnTreatment", visible: true},
      {name: "patientsStoppedTreatment", visible: true}
    ])
  });


  it('should save work in progress for rnr', function () {
    scope.rnr = {"id": "rnrId"};
    scope.pageLineItems = [rnrLineItem];
    scope.saveRnrForm.$dirty = true;
    scope.saveRnrForm.$setPristine = function () {
      scope.saveRnrForm.pristine = true
    };
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond({'success': "R&R saved successfully!"});

    scope.saveRnr();

    httpBackend.flush();
    expect(scope.message).toEqual("R&R saved successfully!");
    expect(scope.saveRnrForm.pristine).toBeTruthy();
  });

  it("should not save if form not dirty", function () {
    scope.saveRnrForm.$dirty = false;

    scope.saveRnr();

    expect(requisitions.update.calls.length).toEqual(0);
  });

  it("should save if form dirty", function () {
    scope.rnr = {"id": "rnrId"};
    scope.saveRnrForm = {
      $dirty: true,
      $setPristine: function () {
      }
    };
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond({});

    scope.saveRnr();

    httpBackend.flush();
    expect(requisitions.update).toHaveBeenCalled();
  });

  it('should get Currency from service', function () {
    expect(scope.currency).toEqual("$");
  });

  it('should show confirmation for submit if no error', function () {
    scope.rnr = new Rnr({"id": "rnrId"});
    scope.saveRnrForm.$dirty = false;

    var deferredCallback = jasmine.createSpyObj('promise', ['then']);
    spyOn(scope, 'saveRnr').andReturn(deferredCallback);

    scope.submitRnr();

    expect(deferredCallback.then).toHaveBeenCalled();
  });

  it('should save rnr on submit if the form is dirty', function () {
    scope.rnr = new Rnr({"id": "rnrId"});
    scope.pageLineItems = [rnrLineItem];
    scope.saveRnrForm.$dirty = true;

    spyOn(scope.rnr, 'validateFullSupply').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn('');

    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200);

    httpBackend.expectGET('/public/pages/template/dialog/dialogbox.html').respond(200);
    scope.submitRnr();
    httpBackend.flush();

    expect(scope.rnr.validateFullSupply).toHaveBeenCalled();
    expect(scope.rnr.validateNonFullSupply.calls.length).toEqual(1);
    expect(scope.submitError).toEqual("");
  });

  it('should not submit rnr if invalid but should save', function () {
    scope.rnr = new Rnr({"id": "rnrId"});
    scope.pageLineItems = [rnrLineItem];
    scope.saveRnrForm.$dirty = true;
    spyOn(scope.rnr, 'validateFullSupply').andReturn('error.rnr.required.fields.missing');
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn('');

    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200);

    scope.submitRnr();
    httpBackend.flush();

    expect(scope.rnr.validateFullSupply).toHaveBeenCalled();
    expect(scope.rnr.validateNonFullSupply.calls.length).toEqual(1);
    expect(scope.submitError).toEqual("error.rnr.required.fields.missing");
  });

  it('should not submit rnr with non full supply required field missing error but should save', function () {
    scope.rnr = new Rnr({"id": "1", "fullSupplyLineItems": []});
    scope.pageLineItems = [rnrLineItem];
    scope.saveRnrForm.$dirty = true;
    spyOn(scope.rnr, 'validateFullSupply').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn('error.rnr.required.fields.missing');
    httpBackend.expect('PUT', '/requisitions/1/save.json').respond(200);

    scope.submitRnr();
    httpBackend.flush();

    expect(scope.rnr.validateFullSupply).toHaveBeenCalled();
    expect(scope.rnr.validateNonFullSupply).toHaveBeenCalled();
    expect(scope.submitError).toEqual("error.rnr.required.fields.missing");
  });

  it('should set non full supply tab error class if non full supply line items have error', function () {
    scope.rnr = new Rnr({"id": "1", "fullSupplyLineItems": []});
    var deferredCallback = jasmine.createSpyObj('promise', ['then']);
    spyOn(scope, 'saveRnr').andReturn(deferredCallback);
    spyOn(scope.rnr, 'validateFullSupply').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn('error.rnr.required.fields.missing');
    httpBackend.expect('PUT', '/requisitions/1/save.json').respond(200);

    scope.submitRnr();
    var saveSuccessCallback = deferredCallback.then.calls[0].args[0];
    saveSuccessCallback();

    expect(scope.nonFullSupplyTabError).toBeTruthy();
  });

  it('should set full supply tab error class if full supply line items have error', function () {
    scope.rnr = new Rnr({"id": "1", "fullSupplyLineItems": []});
    var deferredCallback = jasmine.createSpyObj('promise', ['then']);
    spyOn(scope, 'saveRnr').andReturn(deferredCallback);
    spyOn(scope.rnr, 'validateFullSupply').andReturn('error.rnr.required.fields.missing');
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn('');
    httpBackend.expect('PUT', '/requisitions/1/save.json').respond(200);

    scope.submitRnr();
    var saveSuccessCallback = deferredCallback.then.calls[0].args[0];
    saveSuccessCallback();

    expect(scope.fullSupplyTabError).toBeTruthy();
  });

  it('should submit valid rnr', function () {
    scope.rnr = new Rnr({"id": "rnrId", "status": 'INITIATED', "fullSupplyLineItems": []});
    scope.saveRnrForm = jasmine.createSpyObj('form', ['$setPristine']);
    spyOn(scope.rnr, 'validateFullSupply').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn('');
    spyOn(OpenLmisDialog, 'newDialog');
    httpBackend.expect('PUT', '/requisitions/rnrId/submit.json').respond(200, {success: "R&R submitted successfully!"});

    scope.submitRnr();
    rootScope.$apply();
    var confirmCallback = OpenLmisDialog.newDialog.calls[0].args[1];
    confirmCallback(true);

    httpBackend.flush();

    expect(scope.saveRnrForm.$setPristine).toHaveBeenCalled();
    expect(scope.submitMessage).toEqual("R&R submitted successfully!");
    expect(scope.rnr.status).toEqual("SUBMITTED");
  });


  it('should display confirm modal if submit button is clicked and rnr valid', function () {
    spyOn(scope.rnr, 'validateFullSupply').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn('');
    httpBackend.expect('GET', '/public/pages/template/dialog/dialogbox.html').respond(200);

    scope.submitRnr();

    rootScope.$apply();
    httpBackend.flush();
  });

  it('should not submit Rnr if not confirmed', function () {
    scope.rnr = new Rnr({"id": "rnrId", "status": "ORIGINAL", "fullSupplyLineItems": []});
    scope.saveRnrForm = jasmine.createSpyObj('form', ['$setPristine']);
    spyOn(scope.rnr, 'validateFullSupply').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn('');
    spyOn(OpenLmisDialog, 'newDialog');

    scope.submitRnr();
    rootScope.$apply();
    var confirmCallback = OpenLmisDialog.newDialog.calls[0].args[1];
    confirmCallback(false);

    httpBackend.verifyNoOutstandingRequest();
    httpBackend.verifyNoOutstandingExpectation();
    expect(scope.submitMessage).toEqual("");
    expect(scope.saveRnrForm.$setPristine).not.toHaveBeenCalled();
    expect(scope.rnr.status).toEqual('ORIGINAL');
  });

  it('should return cell error class', function () {
    var lineItem = { "beginningBalance": 1, totalLossesAndAdjustments: 1, quantityDispensed: 2,
      quantityReceived: 1, stockInHand: 1};
    jQuery.extend(true, lineItem, new RegularRnrLineItem());

    spyOn(lineItem, 'getErrorMessage').andReturn("error");
    var errorMsg = scope.getCellErrorClass(lineItem);
    expect(errorMsg).toEqual("cell-error-highlight");
  });

  it('should not return cell error class', function () {
    var lineItem = { "beginningBalance": 1, totalLossesAndAdjustments: 1, quantityDispensed: 2,
      quantityReceived: 1, stockInHand: 1};
    jQuery.extend(true, lineItem, new RegularRnrLineItem());

    spyOn(lineItem, 'getErrorMessage').andReturn("");
    var errorMsg = scope.getCellErrorClass(lineItem);
    expect(errorMsg).toEqual("");
  });

  it('should return row error class', function () {
    var lineItem = { "beginningBalance": 1, totalLossesAndAdjustments: 1, quantityDispensed: 2,
      quantityReceived: 1, stockInHand: 1};
    jQuery.extend(true, lineItem, new RegularRnrLineItem());

    spyOn(scope, 'getCellErrorClass').andReturn("error");
    var errorMsg = scope.getRowErrorClass(lineItem);
    expect(errorMsg).toEqual("row-error-highlight");
  });

  it('should not return row error class', function () {
    var lineItem = { "beginningBalance": 1, totalLossesAndAdjustments: 1, quantityDispensed: 2,
      quantityReceived: 1, stockInHand: 1};
    jQuery.extend(true, lineItem, new RegularRnrLineItem());

    spyOn(scope, 'getCellErrorClass').andReturn("");
    var errorMsg = scope.getRowErrorClass(lineItem);
    expect(errorMsg).toEqual("");
  });

  it('should highlight required field for modal dialog', function () {
    expect(scope.highlightRequiredFieldInModal(null)).toEqual("required-error");
    expect(scope.highlightRequiredFieldInModal(undefined)).toEqual("required-error");
    expect(scope.highlightRequiredFieldInModal('')).toEqual("required-error");
    expect(scope.highlightRequiredFieldInModal(3)).toEqual(null);
  });

  it('should not set disable flag if rnr is initiated and user has create right', function () {
    var rnr = {id: "rnrId", fullSupplyLineItems: [], regimenLineItems: [],equipmentLineItems:[], status: "INITIATED"};

    ctrl = controller(CreateRequisitionController, {$scope: scope, $location: location, requisitionData: {rnr: rnr}, rnrColumns: [], regimenTemplate: regimenTemplate,
      currency: '$', pageSize: pageSize, lossesAndAdjustmentsTypes: lossesAndAdjustmentTypes, facilityApprovedProducts: facilityApprovedProducts,
      requisitionRights: requisitionRights, $routeParams: routeParams, $rootScope: rootScope, localStorageService: localStorageService,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      enableSkipPeriod: false,
      comments: [],
      equipmentOperationalStatus:[]});

    expect(scope.formDisabled).toEqual(false);
  });

  it('should not set disable flag if rnr is submitted and user have authorize right', function () {
    var rnr = {id: "rnrId", fullSupplyLineItems: [], regimenLineItems: [], equipmentLineItems:[], status: "SUBMITTED"};

    ctrl = controller(CreateRequisitionController, {$scope: scope, $location: location, requisitionData: {rnr: rnr}, rnrColumns: [], regimenTemplate: regimenTemplate,
      currency: '$', pageSize: pageSize, lossesAndAdjustmentsTypes: lossesAndAdjustmentTypes, facilityApprovedProducts: facilityApprovedProducts,
      requisitionRights: requisitionRights, $routeParams: routeParams, $rootScope: rootScope, localStorageService: localStorageService,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      enableSkipPeriod: false,
      comments: [],
      equipmentOperationalStatus:[]});

    expect(scope.formDisabled).toEqual(false);
  });

  it('should set disable flag if rnr is not initiated/submitted', function () {
    var rnr = {id: "rnrId", fullSupplyLineItems: [], regimenLineItems: [], equipmentLineItems:[], status: "some random status"};

    ctrl = controller(CreateRequisitionController, {$scope: scope, $location: location, requisitionData: {rnr: rnr}, rnrColumns: [], regimenTemplate: regimenTemplate,
      currency: '$', pageSize: pageSize, lossesAndAdjustmentsTypes: lossesAndAdjustmentTypes, facilityApprovedProducts: facilityApprovedProducts,
      requisitionRights: requisitionRights, $routeParams: routeParams, $rootScope: rootScope, localStorageService: localStorageService,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      enableSkipPeriod: false,
      comments: [],
      equipmentOperationalStatus:[]});

    expect(scope.formDisabled).toEqual(true);
  });

  it('should make rnr in scope as Rnr Instance', function () {
    var spyRnr = spyOn(window, 'Rnr').andCallThrough();
    ctrl = controller(CreateRequisitionController, {$scope: scope, $location: location, requisitionData: {rnr: mockedRequisition, numberOfMonths: 5}, rnrColumns: rnrColumns, regimenTemplate: regimenTemplate,
      currency: '$', pageSize: pageSize, lossesAndAdjustmentsTypes: lossesAndAdjustmentTypes, facilityApprovedProducts: facilityApprovedProducts,
      requisitionRights: requisitionRights, $routeParams: routeParams, $rootScope: rootScope, localStorageService: localStorageService,
      hideAdditionalCommoditiesTab: false,
      hideSkippedProducts: false,
      enableSkipPeriod: false,
      comments: [],
      equipmentOperationalStatus:[]});

    expect(scope.rnr instanceof Rnr).toBeTruthy();
    //expect(spyRnr).toHaveBeenCalledWith(mockedRequisition, rnrColumns, 5);
  });

  it('should set message while saving if set message flag true', function () {
    scope.rnr = {"id": "rnrId"};
    scope.pageLineItems = [rnrLineItem];
    scope.saveRnrForm.$dirty = true;
    scope.saveRnrForm.$setPristine = function () {
      scope.saveRnrForm.pristine = true
    };
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200, {'success': "success message"});
    scope.saveRnr(false);
    httpBackend.flush();
    expect(scope.message).toEqual('success message');
    expect(scope.saveRnrForm.pristine).toBeTruthy();

  });

  it('should not set message while saving if set message flag false', function () {
    scope.rnr = {"id": "rnrId"};
    scope.pageLineItems = [rnrLineItem];
    scope.saveRnrForm.$dirty = true;
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200, {'success': "success message"});

    scope.saveRnr(true);

    httpBackend.flush();
    expect(scope.message).toEqual('');
  });

  it('should calculate pages which have errors on submit', function () {
    scope.rnr = new Rnr({"id": "1", "fullSupplyLineItems": [
      {id: 1},
      {id: 2},
      {id: 3}
    ], period: {numberOfMonths: 7}}, null);

    scope.pageSize = 5;
    spyOn(scope.rnr, 'getErrorPages').andReturn({nonFullSupply: [1, 2], fullSupply: [2, 4], regimen: []});
    spyOn(scope.rnr, 'validateFullSupply').andReturn("");
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn("some error");
    httpBackend.expect('PUT', '/requisitions/1/save.json').respond(200, {'success': "success message"});

    scope.submitRnr();

    rootScope.$apply();
    expect(scope.errorPages).toEqual({nonFullSupply: [1, 2], fullSupply: [2, 4], regimen: []});
  });

  it('should calculate pages which have errors on authorize', function () {
    scope.rnr = new Rnr({"id": "1", "fullSupplyLineItems": [
      {id: 1},
      {id: 2},
      {id: 3}
    ], period: {numberOfMonths: 7}}, null);

    scope.pageSize = 5;
    spyOn(scope.rnr, 'getErrorPages').andReturn({nonFullSupply: [1, 2], fullSupply: [2, 4], regimen: []});
    spyOn(scope.rnr, 'validateFullSupply').andReturn("");
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn("some error");
    httpBackend.expect('PUT', '/requisitions/1/save.json').respond(200, {'success': "success message"});

    scope.authorizeRnr();

    rootScope.$apply();
    expect(scope.errorPages).toEqual({nonFullSupply: [1, 2], fullSupply: [2, 4], regimen:[ ]});
    expect(scope.rnr.getErrorPages).toHaveBeenCalledWith(5);
  });

  it('should not save rnr if the form is not dirty on authorize', function () {
    scope.rnr = new Rnr({"id": "rnrId"});
    scope.saveRnrForm.$dirty = false;

    spyOn(scope.rnr, 'validateFullSupply').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn('');

    httpBackend.expectGET('/public/pages/template/dialog/dialogbox.html').respond(200);
    scope.authorizeRnr();
    httpBackend.flush();

    expect(scope.rnr.validateFullSupply).toHaveBeenCalled();
    expect(scope.rnr.validateNonFullSupply.calls.length).toEqual(1);
    expect(scope.submitError).toEqual("");
  });

  it('should save rnr on authorize if the form is dirty', function () {
    scope.rnr = new Rnr({"id": "rnrId"});
    scope.pageLineItems = [rnrLineItem];
    scope.saveRnrForm.$dirty = true;

    spyOn(scope.rnr, 'validateFullSupply').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn('');

    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200);

    httpBackend.expectGET('/public/pages/template/dialog/dialogbox.html').respond(200);
    scope.authorizeRnr();
    httpBackend.flush();

    expect(scope.rnr.validateFullSupply).toHaveBeenCalled();
    expect(scope.rnr.validateNonFullSupply.calls.length).toEqual(1);
    expect(scope.submitError).toEqual("");
  });

  it('should not authorize rnr if invalid but should save', function () {
    scope.rnr = new Rnr({"id": "rnrId"});
    scope.pageLineItems = [rnrLineItem];
    scope.saveRnrForm.$dirty = true;
    spyOn(scope.rnr, 'validateFullSupply').andReturn('error.rnr.required.fields.missing');
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn('');

    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200);

    scope.authorizeRnr();
    httpBackend.flush();

    expect(scope.rnr.validateFullSupply).toHaveBeenCalled();
    expect(scope.rnr.validateNonFullSupply.calls.length).toEqual(1);
    expect(scope.submitError).toEqual("error.rnr.required.fields.missing");
  });

  it('should not authorize rnr with non full supply required field missing error but should save', function () {
    scope.rnr = new Rnr({"id": "1", "fullSupplyLineItems": []});
    scope.pageLineItems = [rnrLineItem];
    scope.saveRnrForm.$dirty = true;
    spyOn(scope.rnr, 'validateFullSupply').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn('error.rnr.required.fields.missing');
    httpBackend.expect('PUT', '/requisitions/1/save.json').respond(200);

    scope.authorizeRnr();
    httpBackend.flush();

    expect(scope.rnr.validateFullSupply).toHaveBeenCalled();
    expect(scope.rnr.validateNonFullSupply).toHaveBeenCalled();
    expect(scope.submitError).toEqual("error.rnr.required.fields.missing");
  });

  it('should authorize valid rnr', function () {
    scope.rnr = new Rnr({"id": "rnrId", "status": 'SUBMITTED', "fullSupplyLineItems": []});
    scope.saveRnrForm = jasmine.createSpyObj('form', ['$setPristine']);
    spyOn(scope.rnr, 'validateFullSupply').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn('');
    spyOn(OpenLmisDialog, 'newDialog');
    httpBackend.expect('PUT', '/requisitions/rnrId/authorize.json').respond(200, {success: "R&R authorized successfully!"});

    scope.authorizeRnr();
    rootScope.$apply();
    var confirmCallback = OpenLmisDialog.newDialog.calls[0].args[1];
    confirmCallback(true);

    httpBackend.flush();

    expect(scope.submitMessage).toEqual("R&R authorized successfully!");
    expect(scope.rnr.status).toEqual("AUTHORIZED");
    expect(scope.saveRnrForm.$setPristine).toHaveBeenCalled();
  });

  it('should display confirm modal if authorize button is clicked and rnr valid', function () {
    spyOn(scope.rnr, 'validateFullSupply').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn('');
    httpBackend.expectGET('/public/pages/template/dialog/dialogbox.html').respond(200);

    scope.authorizeRnr();

    httpBackend.flush();
  });

  it('should not authorize Rnr if cancel is clicked on the confirm modal', function () {
    scope.rnr = new Rnr({"id": "rnrId", "status": "ORIGINAL", "fullSupplyLineItems": []});
    scope.saveRnrForm = jasmine.createSpyObj('form', ['$setPristine']);
    spyOn(scope.rnr, 'validateFullSupply').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn('');
    spyOn(OpenLmisDialog, 'newDialog');

    scope.authorizeRnr();
    rootScope.$apply();
    var confirmCallback = OpenLmisDialog.newDialog.calls[0].args[1];
    confirmCallback(false);

    httpBackend.verifyNoOutstandingRequest();
    httpBackend.verifyNoOutstandingExpectation();
    expect(scope.submitMessage).toEqual("");
    expect(scope.saveRnrForm.$setPristine).not.toHaveBeenCalled();
    expect(scope.rnr.status).toEqual('ORIGINAL');
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

  it('should set requisition rights in scope', function () {
    expect(scope.requisitionRights).toEqual([
      {name: 'CREATE_REQUISITION'},
      {name: 'AUTHORIZE_REQUISITION'}
    ]);
  });

  it('should check permission using requisition rights', function () {
    expect(scope.hasPermission('CREATE_REQUISITION')).toBeTruthy();
  });

  it('should set regimenLineItemInValid as true if the required fields are missing', function () {
    var regimenLineItems = [
      {"id": 6, "rnrId": 2, "code": "001", "name": "REGIMEN1", "patientsOnTreatment": 1, "patientsToInitiateTreatment": 7,
        "category": {"name": "Adults", "displayOrder": 1}},
      {"id": 7, "rnrId": 2, "code": "002", "name": "REGIMEN2", "patientsOnTreatment": 1, "patientsToInitiateTreatment": 7, "patientsStoppedTreatment": 4,
        "category": {"name": "Adults", "displayOrder": 1}}
    ];
    scope.rnr = new Rnr({"id": "1", "regimenLineItems": regimenLineItems});

    scope.submitRnr();

    rootScope.$apply();
    expect(scope.regimenLineItemInValid).toBeTruthy();
    expect(scope.submitError).toEqual('error.rnr.validation');
  });

  it('should set regimenLineItemInValid as false if the required fields are not missing', function () {
    var regimenLineItems = [
      {"id": 6, "rnrId": 2, "code": "001", "name": "REGIMEN1", "patientsOnTreatment": 1, "patientsToInitiateTreatment": 7, "patientsStoppedTreatment": 5,
        "category": {"name": "Adults", "displayOrder": 1}},
      {"id": 7, "rnrId": 2, "code": "002", "name": "REGIMEN2", "patientsOnTreatment": 1, "patientsToInitiateTreatment": 7, "patientsStoppedTreatment": 4,
        "category": {"name": "Adults", "displayOrder": 1}}
    ];
    scope.rnr = new Rnr({"id": "1", "regimenLineItems": regimenLineItems});

    scope.submitRnr();

    expect(scope.regimenLineItemInValid).toBeFalsy();
    expect(scope.error).toEqual("");
  });

  it('should set skipAll flag if formDisabled is false', function () {
    scope.rnr.skipAll = false;
    scope.formDisabled = false;
    spyOn(scope, 'toggleSkipFlag').andCallThrough();
    scope.setSkipAll(true);

    expect(scope.rnr.skipAll).toBeTruthy();
    expect(scope.toggleSkipFlag).toHaveBeenCalled();
  });

  it('should not set skipAll flag if formDisabled is true', function () {
    scope.rnr.skipAll = true;
    scope.formDisabled = true;
    spyOn(scope, 'toggleSkipFlag').andCallThrough();
    scope.setSkipAll(true);

    expect(scope.rnr.skipAll).toBeTruthy();
    expect(scope.toggleSkipFlag).not.toHaveBeenCalled();
  });
});

