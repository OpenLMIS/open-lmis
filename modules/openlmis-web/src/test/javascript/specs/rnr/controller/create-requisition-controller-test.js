describe('CreateRequisitionController', function () {
  var scope, rootScope, ctrl, httpBackend, location, routeParams, controller, localStorageService, mockedRequisition, rnrColumns;

  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));
  beforeEach(inject(function ($httpBackend, $rootScope, $location, $controller, $routeParams, _localStorageService_) {
    scope = $rootScope.$new();
    rootScope = $rootScope;
    $rootScope.hasPermission = function () {
    };
    location = $location;
    controller = $controller;
    httpBackend = $httpBackend;
    scope.$parent.facility = "10134";
    scope.$parent.program = {code:"programCode", "id":1};

    scope.saveRnrForm = {$error:{ rnrError:false }};
    localStorageService = _localStorageService_;
    routeParams = {"facility":"1", "program":"1", "period":2};
    scope.rnr = {"id":"rnrId", "fullSupplyLineItems":[]};
    mockedRequisition = {'status':"INITIATED",
      fullSupplyItemsSubmittedCost:100,
      nonFullSupplyItemsSubmittedCost:14,
      period:{numberOfMonths:5},
      fullSupplyLineItems:[
        {id:1}
      ],
      nonFullSupplyLineItems:[
        {id:2}
      ]
    };

    httpBackend.when('GET', '/facilityApprovedProducts/facility/1/program/1/nonFullSupply.json').respond(200);
    rnrColumns = [
      {"testField":"test"}
    ];
    httpBackend.when('GET', '/rnr/1/columns.json').respond(rnrColumns);
    httpBackend.when('GET', '/reference-data/currency.json').respond({"currency":"$"});
    $rootScope.fixToolBar = function () {
    };
    ctrl = controller(CreateRequisitionController, {$scope:scope, $location:location, requisition:mockedRequisition, rnrColumns:rnrColumns,
      currency:'$', $routeParams:routeParams, $rootScope:rootScope, localStorageService:localStorageService});

    scope.allTypes = [
      {"name":"some name"},
      {"name":"some other name"}
    ];
  }));

  it('should get list of Rnr Columns for program', function () {
    expect([
      {"testField":"test"}
    ]).toEqual(scope.programRnrColumnList);
  });

  it('should save work in progress for rnr', function () {
    scope.rnr = {"id":"rnrId"};
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond({'success':"R&R saved successfully!"});
    scope.saveRnr();
    httpBackend.flush();
    expect(scope.message).toEqual("R&R saved successfully!");
  });


  it('should get Currency from service', function () {
    expect(scope.currency).toEqual("$");
  });

  it('should not submit rnr if invalid but should save', function () {
    scope.rnr = new Rnr({"id":"rnrId"});
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
    scope.rnr = new Rnr({"id":"1", "fullSupplyLineItems":[]});
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
    scope.rnr = new Rnr({"id":"1", "fullSupplyLineItems":[]});
    spyOn(scope.rnr, 'validateFullSupply').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn('rnr.required.fields.missing.error');
    httpBackend.expect('PUT', '/requisitions/1/save.json').respond(200);

    scope.submitRnr();

    expect(scope.nonFullSupplyTabError).toBeTruthy();
  });

  it('should set non full supply tab error class if non full supply line items have error', function () {
    scope.rnr = new Rnr({"id":"1", "fullSupplyLineItems":[]});
    spyOn(scope.rnr, 'validateFullSupply').andReturn('rnr.required.fields.missing.error');
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn('');
    httpBackend.expect('PUT', '/requisitions/1/save.json').respond(200);

    scope.submitRnr();

    expect(scope.fullSupplyTabError).toBeTruthy();
  });

  it('should submit valid rnr', function () {
    scope.rnr = new Rnr({"id":"rnrId", "fullSupplyLineItems":[]});
    spyOn(scope.rnr, 'validateFullSupply').andReturn('');
    spyOn(scope.rnr, 'validateNonFullSupply').andReturn('');
    httpBackend.expect('PUT', '/requisitions/rnrId/submit.json').respond(200, {success:"R&R submitted successfully!"});

    scope.submitRnr();
    httpBackend.flush();

    expect(scope.submitMessage).toEqual("R&R submitted successfully!");
    expect(scope.rnr.status).toEqual("SUBMITTED");
  });

  it('should return cell error class', function () {
    var lineItem = { "beginningBalance":1, totalLossesAndAdjustments:1, quantityDispensed:2,
      quantityReceived:1, stockInHand:1};
    jQuery.extend(true, lineItem, new RnrLineItem());

    spyOn(lineItem, 'getErrorMessage').andReturn("error");
    var errorMsg = scope.getCellErrorClass(lineItem);
    expect(errorMsg).toEqual("cell-error-highlight");
  });

  it('should not return cell error class', function () {
    var lineItem = { "beginningBalance":1, totalLossesAndAdjustments:1, quantityDispensed:2,
      quantityReceived:1, stockInHand:1};
    jQuery.extend(true, lineItem, new RnrLineItem());

    spyOn(lineItem, 'getErrorMessage').andReturn("");
    var errorMsg = scope.getCellErrorClass(lineItem);
    expect(errorMsg).toEqual("");
  });

  it('should return row error class', function () {
    var lineItem = { "beginningBalance":1, totalLossesAndAdjustments:1, quantityDispensed:2,
      quantityReceived:1, stockInHand:1};
    jQuery.extend(true, lineItem, new RnrLineItem());

    spyOn(scope, 'getCellErrorClass').andReturn("error");
    var errorMsg = scope.getRowErrorClass(lineItem);
    expect(errorMsg).toEqual("row-error-highlight");
  });

  it('should not return row error class', function () {
    var lineItem = { "beginningBalance":1, totalLossesAndAdjustments:1, quantityDispensed:2,
      quantityReceived:1, stockInHand:1};
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

    var rnr = {id:"rnrId", fullSupplyLineItems:[], status:"INITIATED"};
    spyOn(rootScope, 'hasPermission').andReturn(true);

    ctrl = controller(CreateRequisitionController, {$scope:scope, $location:location, requisition:rnr, rnrColumns:[],
      currency:'$', $routeParams:routeParams, $rootScope:rootScope, localStorageService:localStorageService});

    expect(rootScope.hasPermission).toHaveBeenCalledWith('CREATE_REQUISITION');
    expect(scope.formDisabled).toEqual(false);
  });

  it('should not set disable flag if rnr is submitted and user have authorize right', function () {
    var rnr = {id:"rnrId", fullSupplyLineItems:[], status:"SUBMITTED"};
    spyOn(rootScope, 'hasPermission').andReturn(true);

    ctrl = controller(CreateRequisitionController, {$scope:scope, $location:location, requisition:rnr, rnrColumns:[],
      currency:'$', $routeParams:routeParams, $rootScope:rootScope, localStorageService:localStorageService});

    expect(rootScope.hasPermission).toHaveBeenCalledWith('AUTHORIZE_REQUISITION');
    expect(scope.formDisabled).toEqual(false);
  });

  it('should set disable flag if rnr is not initiated/submitted', function () {
    var rnr = {id:"rnrId", fullSupplyLineItems:[], status:"some random status"};
    spyOn(rootScope, 'hasPermission');
    ctrl = controller(CreateRequisitionController, {$scope:scope, $location:location, requisition:rnr, rnrColumns:[],
      currency:'$', $routeParams:routeParams, $rootScope:rootScope, localStorageService:localStorageService});
    expect(rootScope.hasPermission).not.toHaveBeenCalled();
    expect(scope.formDisabled).toEqual(true);
  });

  it('should make rnr in scope as Rnr Instance', function () {
    var spyRnr = spyOn(window, 'Rnr').andCallThrough();
    ctrl = controller(CreateRequisitionController, {$scope:scope, $location:location, requisition:mockedRequisition, rnrColumns:rnrColumns,
      currency:'$', $routeParams:routeParams, $rootScope:rootScope, localStorageService:localStorageService});

    expect(scope.rnr instanceof Rnr).toBeTruthy();
    expect(spyRnr).toHaveBeenCalledWith(mockedRequisition, rnrColumns);
  });

  it('should prepare period display name', function () {
    scope.rnr = {'status':"INITIATED"};
    scope.rnr.period = {"name":"Period 1", "startDate":1358274600000, "endDate":1367260200000};

    expect(scope.periodDisplayName()).toEqual('16/01/2013 - 30/04/2013');
  });


});

