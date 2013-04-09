/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('CreateRequisitionController', function () {
  var scope, rootScope, ctrl, httpBackend, location, routeParams, controller, localStorageService, mockedRequisition, rnrColumns, lossesAndAdjustmentTypes, facilityApprovedProducts, requisitionRights ;

  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));
  beforeEach(module('ui.bootstrap.dialog'));

  beforeEach(inject(function ($httpBackend, $rootScope, $location, $controller, $routeParams, _localStorageService_) {
    scope = $rootScope.$new();
    rootScope = $rootScope;
    $rootScope.hasPermission = function () {
    };
    location = $location;
    controller = $controller;
    httpBackend = $httpBackend;
    scope.$parent.facility = "10134";
    scope.$parent.program = {code: "programCode", "id": 1};

    scope.saveRnrForm = {$error: { rnrError: false }};
    localStorageService = _localStorageService_;
    routeParams = {"facility": "1", "program": "1", "period": 2};
    scope.rnr = {"id": "rnrId", "fullSupplyLineItems": []};
    mockedRequisition = {'status': "INITIATED",
      fullSupplyItemsSubmittedCost: 100,
      nonFullSupplyItemsSubmittedCost: 14,
      period: {numberOfMonths: 5},
      fullSupplyLineItems: [
        {id: 1}
      ],
      nonFullSupplyLineItems: [
        {id: 2}
      ]
    };

    rnrColumns = [
      {"testField": "test"}
    ];
    lossesAndAdjustmentTypes = {"lossAdjustmentTypes": {"name":"damaged"}};

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
    httpBackend.when('GET', '/reference-data/currency.json').respond({"currency": "$"});
    httpBackend.when('GET', '/requisitions/lossAndAdjustments/reference-data.json').respond(200, lossesAndAdjustmentTypes);
    httpBackend.when('GET', '/facilityApprovedProducts/facility/1/program/1/nonFullSupply.json').respond(200, {"nonFullSupplyProducts": facilityApprovedProducts});

    $rootScope.fixToolBar = function () {
    };

   requisitionRights = [{right:'CREATE_REQUISITION'},{right:'AUTHORIZE_REQUISITION'}];

    ctrl = controller(CreateRequisitionController, {$scope: scope, $location: location, requisition: mockedRequisition, rnrColumns: rnrColumns,
      currency: '$', lossesAndAdjustmentsTypes: lossesAndAdjustmentTypes, facilityApprovedProducts: facilityApprovedProducts, requisitionRights: requisitionRights, $routeParams: routeParams, $rootScope: rootScope, localStorageService: localStorageService});

  }));

  it('should get list of Rnr Columns for program', function () {
    expect([
      {"testField": "test"}
    ]).toEqual(scope.programRnrColumnList);
  });

  it('should get lossesAndAdjustments types', function () {
    expect(lossesAndAdjustmentTypes).toEqual(scope.allTypes);
  });

  it('should get facility approved products', function () {
    expect(facilityApprovedProducts).toEqual(scope.facilityApprovedProducts);
  });

  it('should save work in progress for rnr', function () {
    scope.rnr = {"id": "rnrId"};
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond({'success': "R&R saved successfully!"});
    scope.saveRnr();
    httpBackend.flush();
    expect(scope.message).toEqual("R&R saved successfully!");
  });


  it('should get Currency from service', function () {
    expect(scope.currency).toEqual("$");
  });

  it('should not submit rnr if invalid but should save', function () {
    scope.rnr = new Rnr({"id": "rnrId"});
    spyOn(scope.rnr, 'validateFullSupply').andReturn('rnr.required.fields.missing.error');
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn('');

    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200);

    scope.submitRnr();
    httpBackend.flush();

    expect(scope.rnr.validateFullSupply).toHaveBeenCalled();
    expect(scope.rnr.validateNonFullSupply.calls.length).toEqual(1);
    expect(scope.submitError).toEqual("rnr.required.fields.missing.error");
  });

  it('should not submit rnr with non full supply required field missing error but should save', function () {
    scope.rnr = new Rnr({"id": "1", "fullSupplyLineItems": []});
    spyOn(scope.rnr, 'validateFullSupply').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn('rnr.required.fields.missing.error');
    httpBackend.expect('PUT', '/requisitions/1/save.json').respond(200);

    scope.submitRnr();
    httpBackend.flush();

    expect(scope.rnr.validateFullSupply).toHaveBeenCalled();
    expect(scope.rnr.validateNonFullSupply).toHaveBeenCalled();
    expect(scope.submitError).toEqual("rnr.required.fields.missing.error");
  });

  it('should set non full supply tab error class if non full supply line items have error', function () {
    scope.rnr = new Rnr({"id": "1", "fullSupplyLineItems": []});
    spyOn(scope.rnr, 'validateFullSupply').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn('rnr.required.fields.missing.error');
    httpBackend.expect('PUT', '/requisitions/1/save.json').respond(200);

    scope.submitRnr();

    expect(scope.nonFullSupplyTabError).toBeTruthy();
  });

  it('should set non full supply tab error class if non full supply line items have error', function () {
    scope.rnr = new Rnr({"id": "1", "fullSupplyLineItems": []});
    spyOn(scope.rnr, 'validateFullSupply').andReturn('rnr.required.fields.missing.error');
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn('');
    httpBackend.expect('PUT', '/requisitions/1/save.json').respond(200);

    scope.submitRnr();

    expect(scope.fullSupplyTabError).toBeTruthy();
  });

  it('should submit valid rnr', function () {
    scope.rnr = new Rnr({"id": "rnrId", "fullSupplyLineItems": []});
    spyOn(scope.rnr, 'validateFullSupply').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn('');
    httpBackend.expect('PUT', '/requisitions/rnrId/submit.json').respond(200, {success: "R&R submitted successfully!"});

    scope.submitRnr();
    httpBackend.flush();

    expect(scope.submitMessage).toEqual("R&R submitted successfully!");
    expect(scope.rnr.status).toEqual("SUBMITTED");
  });

  it('should display confirm modal if approve button is clicked', function () {
    spyOn(OpenLmisDialog, 'new');
    scope.showConfirmModal();
    httpBackend.expectGET('/public/pages/partials/dialogbox.html').respond(200);
    expect(OpenLmisDialog.new).toHaveBeenCalled();
  });

  it('should submit Rnr if ok is clicked on the confirm modal', function () {
    scope.rnr = new Rnr({"id": "rnrId", "status":"INITIATED", "fullSupplyLineItems": []});
    spyOn(scope.rnr, 'validateFullSupply').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn('');

    httpBackend.expect('PUT', '/requisitions/rnrId/submit.json').respond({'success':"R&R submitted successfully!"});
    scope.dialogCloseCallback(true);
    httpBackend.flush();
    expect(scope.submitMessage).toEqual("R&R submitted successfully!");
  });

  it('should return cell error class', function () {
    var lineItem = { "beginningBalance": 1, totalLossesAndAdjustments: 1, quantityDispensed: 2,
      quantityReceived: 1, stockInHand: 1};
    jQuery.extend(true, lineItem, new RnrLineItem());

    spyOn(lineItem, 'getErrorMessage').andReturn("error");
    var errorMsg = scope.getCellErrorClass(lineItem);
    expect(errorMsg).toEqual("cell-error-highlight");
  });

  it('should not return cell error class', function () {
    var lineItem = { "beginningBalance": 1, totalLossesAndAdjustments: 1, quantityDispensed: 2,
      quantityReceived: 1, stockInHand: 1};
    jQuery.extend(true, lineItem, new RnrLineItem());

    spyOn(lineItem, 'getErrorMessage').andReturn("");
    var errorMsg = scope.getCellErrorClass(lineItem);
    expect(errorMsg).toEqual("");
  });

  it('should return row error class', function () {
    var lineItem = { "beginningBalance": 1, totalLossesAndAdjustments: 1, quantityDispensed: 2,
      quantityReceived: 1, stockInHand: 1};
    jQuery.extend(true, lineItem, new RnrLineItem());

    spyOn(scope, 'getCellErrorClass').andReturn("error");
    var errorMsg = scope.getRowErrorClass(lineItem);
    expect(errorMsg).toEqual("row-error-highlight");
  });

  it('should not return row error class', function () {
    var lineItem = { "beginningBalance": 1, totalLossesAndAdjustments: 1, quantityDispensed: 2,
      quantityReceived: 1, stockInHand: 1};
    jQuery.extend(true, lineItem, new RnrLineItem());

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

    var rnr = {id: "rnrId", fullSupplyLineItems: [], status: "INITIATED"};

    ctrl = controller(CreateRequisitionController, {$scope: scope, $location: location, requisition: rnr, rnrColumns: [],
      currency: '$', lossesAndAdjustmentsTypes: lossesAndAdjustmentTypes, facilityApprovedProducts: facilityApprovedProducts, requisitionRights : requisitionRights,$routeParams: routeParams, $rootScope: rootScope, localStorageService: localStorageService});

    expect(scope.formDisabled).toEqual(false);
  });

  it('should not set disable flag if rnr is submitted and user have authorize right', function () {
    var rnr = {id: "rnrId", fullSupplyLineItems: [], status: "SUBMITTED"};

    ctrl = controller(CreateRequisitionController, {$scope: scope, $location: location, requisition: rnr, rnrColumns: [],
      currency: '$', lossesAndAdjustmentsTypes: lossesAndAdjustmentTypes, facilityApprovedProducts: facilityApprovedProducts, requisitionRights : requisitionRights, $routeParams: routeParams, $rootScope: rootScope, localStorageService: localStorageService});

    expect(scope.formDisabled).toEqual(false);
  });

  it('should set disable flag if rnr is not initiated/submitted', function () {
    var rnr = {id: "rnrId", fullSupplyLineItems: [], status: "some random status"};
    ctrl = controller(CreateRequisitionController, {$scope: scope, $location: location, requisition: rnr, rnrColumns: [],
      currency: '$', lossesAndAdjustmentsTypes: lossesAndAdjustmentTypes, facilityApprovedProducts: facilityApprovedProducts, requisitionRights : requisitionRights, $routeParams: routeParams, $rootScope: rootScope, localStorageService: localStorageService});
    expect(scope.formDisabled).toEqual(true);
  });

  it('should make rnr in scope as Rnr Instance', function () {
    var spyRnr = spyOn(window, 'Rnr').andCallThrough();
    ctrl = controller(CreateRequisitionController, {$scope: scope, $location: location, requisition: mockedRequisition, rnrColumns: rnrColumns,
      currency: '$', lossesAndAdjustmentsTypes: lossesAndAdjustmentTypes, facilityApprovedProducts: facilityApprovedProducts,requisitionRights : requisitionRights, $routeParams: routeParams, $rootScope: rootScope, localStorageService: localStorageService});

    expect(scope.rnr instanceof Rnr).toBeTruthy();
    expect(spyRnr).toHaveBeenCalledWith(mockedRequisition, rnrColumns);
  });

  it('should set message while saving if set message flag true', function () {
    scope.rnr = {"id": "rnrId"};
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200, {'success': "success message"});
    scope.saveRnr(false);
    httpBackend.flush();
    expect(scope.message).toEqual('success message');
  });

  it('should not set message while saving if set message flag false', function () {
    scope.rnr = {"id": "rnrId"};
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200, {'success': "success message"});
    scope.saveRnr(true);
    httpBackend.flush();
    expect(scope.message).toEqual('');
  });

  it('should calculate pages which have errors on submit', function () {
    scope.rnr = new Rnr({"id":"1", "fullSupplyLineItems":[
      {id:1},
      {id:2},
      {id:3}
    ], period:{numberOfMonths:7}}, null);

    scope.pageSize = 5;
    spyOn(scope.rnr, 'getErrorPages').andReturn({nonFullSupply:[1, 2], fullSupply:[2, 4]});
    spyOn(scope.rnr, 'validateFullSupply').andReturn("");
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn("some error");
    httpBackend.expect('PUT', '/requisitions/1/save.json').respond(200, {'success':"success message"});
    scope.submitRnr();

    expect(scope.errorPages).toEqual({nonFullSupply:[1, 2], fullSupply:[2, 4]});
  });

  it('should calculate pages which have errors on approve', function () {
    scope.rnr = new Rnr({"id":"1", "fullSupplyLineItems":[
      {id:1},
      {id:2},
      {id:3}
    ], period:{numberOfMonths:7}}, null);

    scope.pageSize = 5;
    spyOn(scope.rnr, 'getErrorPages').andReturn({nonFullSupply:[1, 2], fullSupply:[2, 4]});
    spyOn(scope.rnr, 'validateFullSupply').andReturn("");
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn("some error");
    httpBackend.expect('PUT', '/requisitions/1/save.json').respond(200, {'success':"success message"});
    scope.authorizeRnr();

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

  it('should set requisition rights in scope', function(){
    expect(scope.requisitionRights).toEqual([{right:'CREATE_REQUISITION'},{right:'AUTHORIZE_REQUISITION'}])
  });

  it('should check permission using requisition rights', function(){
    expect(scope.hasPermission('CREATE_REQUISITION')).toBeTruthy()
  })

});

