describe('ViewRnrController', function () {
  var scope, httpBackend, controller, routeParams;

  beforeEach(module('openlmis.services'));
  beforeEach(inject(function ($httpBackend, $rootScope, $controller) {
    routeParams = {'programId' : 2, 'id': 1, 'supplyType': 'full-supply'};
    scope = $rootScope.$new();
    httpBackend = $httpBackend;

    controller = $controller;

  }));

  it('should setup the grid columns according to visibility', function () {
    httpBackend.expect('GET', "/requisitions/1.json").respond(200, {'rnr':{lineItems:[], nonFullSupplyLineItems:[]}});
    httpBackend.expect('GET', "/reference-data/currency.json").respond(200, {'currency':{}});
    httpBackend.expect('GET', "/rnr/2/columns.json").respond(200, {'rnrColumnList':columns});
    controller(ViewRnrController, {$scope:scope, $routeParams: routeParams});
    httpBackend.flush();
    var columnDefs = [
      {field:'productCode', displayName:'Product Code'},
      {field:'product', displayName:'Product'},
      {field:'dispensingUnit', displayName:'Unit/Unit of Issue'},
      {field:'lossesAndAdjustments', displayName:'Total Losses / Adjustments', cellTemplate:lossesAndAdjustmentsTemplate}
    ];
    expect(columnDefs).toEqual(scope.columnDefs);
    expect('columnDefs').toEqual(scope.rnrGrid.columnDefs);
  });

  it('should include approved quantity column if status  approved', function (){
    columns.push({'name': 'quantityApproved', 'label': 'Approved Quantity', id: '99', visible: true});
    httpBackend.expect('GET', "/requisitions/1.json").respond(200, {'rnr': {lineItems: [], nonFullSupplyLineItems: [], status:'APPROVED'}});
    httpBackend.expect('GET', "/reference-data/currency.json").respond(200, {'currency': {}});
    httpBackend.expect('GET', "/rnr/2/columns.json").respond(200, {'rnrColumnList': columns});
    controller(ViewRnrController, {$scope:scope, $routeParams: routeParams});
    httpBackend.flush();
    var expectedDefinitions = [
      {field:'productCode', displayName:'Product Code'},
      {field:'product', displayName:'Product'},
      {field:'dispensingUnit', displayName:'Unit/Unit of Issue'},
      {field:'lossesAndAdjustments', displayName:'Total Losses / Adjustments', cellTemplate:lossesAndAdjustmentsTemplate},
      {field: 'quantityApproved', displayName: 'Approved Quantity'}
    ];

    expect(scope.columnDefs).toEqual(expectedDefinitions);
  });

  it('should not include approved quantity column if status not approved', function (){
    columns.push({'name': 'quantityApproved', 'label': 'Approved Quantity', id: '99', visible: true});
    httpBackend.expect('GET', "/requisitions/1.json").respond(200, {'rnr': {lineItems: [], nonFullSupplyLineItems: [], status:'INITIATED'}});
    httpBackend.expect('GET', "/reference-data/currency.json").respond(200, {'currency': {}});
    httpBackend.expect('GET', "/rnr/2/columns.json").respond(200, {'rnrColumnList': columns});
    controller(ViewRnrController, {$scope:scope, $routeParams: routeParams});
    httpBackend.flush();
    var expectedDefinitions = [
      {field:'productCode', displayName:'Product Code'},
      {field:'product', displayName:'Product'},
      {field:'dispensingUnit', displayName:'Unit/Unit of Issue'},
      {field:'lossesAndAdjustments', displayName:'Total Losses / Adjustments', cellTemplate:lossesAndAdjustmentsTemplate}
    ];

    expect(scope.columnDefs).toEqual(expectedDefinitions);
  });

  it('should assign  line items based on supply type', function() {
    routeParams.supplyType = 'full-supply';
    var rnr = {'rnr': {fullSupplyLineItems: [{'id' : 1}], nonFullSupplyLineItems: [], status:'INITIATED'}};
    httpBackend.expect('GET', "/requisitions/1.json").respond(200, rnr);
    httpBackend.expect('GET', "/reference-data/currency.json").respond(200, {'currency': {}});
    httpBackend.expect('GET', "/rnr/2/columns.json").respond(200, {'rnrColumnList': columns});
    controller(ViewRnrController, {$scope:scope, $routeParams: routeParams});
    httpBackend.flush();

    expect(rnr.rnr.fullSupplyLineItems.length).toEqual(scope.gridLineItems.length);
    expect(rnr.rnr.fullSupplyLineItems.length).toEqual(scope.gridLineItems.length);
  });

  it('should assign non full supply line items based on supply type', function() {
    routeParams.supplyType = 'non-full-supply';
    var rnr = {'rnr': {fullSupplyLineItems: [], nonFullSupplyLineItems: [{'id' : 1}], status:'INITIATED'}};
    httpBackend.expect('GET', "/requisitions/1.json").respond(200, rnr);
    httpBackend.expect('GET', "/reference-data/currency.json").respond(200, {'currency': {}});
    httpBackend.expect('GET', "/rnr/2/columns.json").respond(200, {'rnrColumnList': columns});
    controller(ViewRnrController, {$scope:scope, $routeParams: routeParams});
    httpBackend.flush();

    expect(rnr.rnr.nonFullSupplyLineItems.length).toEqual(scope.gridLineItems.length);
  });

  it('should set grid line items as data to the grid', function() {
    routeParams.supplyType = 'non-full-supply';
    var rnr = {'rnr': {lineItems: [], nonFullSupplyLineItems: [], status:'INITIATED'}};
    httpBackend.expect('GET', "/requisitions/1.json").respond(200, rnr);
    httpBackend.expect('GET', "/reference-data/currency.json").respond(200, {'currency': {}});
    httpBackend.expect('GET', "/rnr/2/columns.json").respond(200, {'rnrColumnList': columns});
    controller(ViewRnrController, {$scope:scope, $routeParams: routeParams});

    expect('gridLineItems').toEqual(scope.rnrGrid.data);
  })

});

var columns = [
  {"id":1, "name":"productCode", "position":1, "source":{"description":"Reference Data", "name":"REFERENCE", "code":"R"}, "sourceConfigurable":false, "label":"Product Code", "formula":"", "indicator":"O", "used":true, "visible":true, "mandatory":true, "description":"Unique identifier for each commodity", "formulaValidationRequired":true},
  {"id":2, "name":"product", "position":2, "source":{"description":"Reference Data", "name":"REFERENCE", "code":"R"}, "sourceConfigurable":false, "label":"Product", "formula":"", "indicator":"R", "used":true, "visible":true, "mandatory":true, "description":"Primary name of the product", "formulaValidationRequired":true},
  {"id":3, "name":"dispensingUnit", "position":3, "source":{"description":"Reference Data", "name":"REFERENCE", "code":"R"}, "sourceConfigurable":false, "label":"Unit/Unit of Issue", "formula":"", "indicator":"U", "used":true, "visible":true, "mandatory":false, "description":"Dispensing unit for this product", "formulaValidationRequired":true},
  {"id":4, "name":"beginningBalance", "position":4, "source":{"description":"User Input", "name":"USER_INPUT", "code":"U"}, "sourceConfigurable":false, "label":"Beginning Balance", "formula":"", "indicator":"A", "used":true, "visible":false, "mandatory":false, "description":"Stock in hand of previous period.This is quantified in dispensing units", "formulaValidationRequired":true},
  {"id":7, "name":"lossesAndAdjustments", "position":7, "source":{"description":"User Input", "name":"USER_INPUT", "code":"U"}, "sourceConfigurable":false, "label":"Total Losses / Adjustments", "formula":"D1 + D2+D3...DN", "indicator":"D", "used":true, "visible":true, "mandatory":false, "description":"All kind of looses/adjustments made at the facility", "formulaValidationRequired":true}
];

var lossesAndAdjustmentsTemplate = '<div id="lossesAndAdjustments" modal="lossesAndAdjustmentsModal[row.entity.id]">' +
  '<div class="modal-header"><h3>Losses And Adjustments</h3></div>' +
  '<div class="modal-body">' +
  '<hr ng-show="row.entity.lossesAndAdjustments.length > 0"/>' +
  '<div class="adjustment-list" ng-show="row.entity.lossesAndAdjustments.length > 0">' +
  '<ul>' +
  '<li ng-repeat="oneLossAndAdjustment in row.entity.lossesAndAdjustments" class="clearfix">' +
  '<span class="tpl-adjustment-type" ng-bind="oneLossAndAdjustment.type.description"></span>' +
  '<span class="tpl-adjustment-qty" ng-bind="oneLossAndAdjustment.quantity"></span>' +
  '</li>' +
  '</ul>' +
  '</div>' +
  '<div class="adjustment-total clearfix alert alert-warning" ng-show="row.entity.lossesAndAdjustments.length > 0">' +
  '<span class="pull-left">Total</span> ' +
  '<span ng-bind="row.entity.totalLossesAndAdjustments"></span>' +
  '</div>' +
  '</div>' +
  '<div class="modal-footer">' +
  '<input type="button" class="btn btn-success save-button" style="width: 75px" ng-click="closeLossesAndAdjustmentsForRnRLineItem(row.entity)" value="Close"/>' +
  '</div>' +
  '</div>' +
  '<div>' +
  '<a ng-click="showLossesAndAdjustmentModalForLineItem(row.entity)" class="rnr-adjustment">' +
  '<span class="adjustment-value" ng-bind="row.entity.totalLossesAndAdjustments"></span>' +
  '</a>' +
  '</div>';
