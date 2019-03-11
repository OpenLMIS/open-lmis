/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */


describe('ViewRnrViaDetailController', function () {
  var httpBackend, scope, route, filter, location, requisition, requisitionService, downloadPdfService, downloadSimamService;

  var rnrItemsForPagination = {
    rnr: {
      facility: {code: "F10", name: "Health Facility 1"},
      fullSupplyLineItems: [
        {beginningBalance: 1, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140, productCode: '008'},
        {beginningBalance: 2, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140, productCode: '009'},
        {beginningBalance: 3, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140, productCode: '010'},
        {beginningBalance: 4, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140, productCode: '004'},
        {beginningBalance: 5, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140, productCode: '005'},
        {beginningBalance: 6, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140, productCode: '006'},
        {beginningBalance: 7, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140, productCode: '007'},
        {beginningBalance: 8, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140, productCode: '001'},
        {beginningBalance: 9, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140, productCode: '002'},
        {beginningBalance: 10, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140, productCode: '022'},
        {beginningBalance: 11, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140, productCode: '122'},
        {beginningBalance: 12, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140, productCode: '002'},
        {beginningBalance: 13, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140, productCode: '003'},
        {beginningBalance: 14, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140, productCode: '010'},
        {beginningBalance: 15, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140, productCode: '011'},
        {beginningBalance: 16, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140, productCode: '012'},
        {beginningBalance: 17, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140, productCode: '014'},
        {beginningBalance: 18, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140, productCode: '015'},
        {beginningBalance: 19, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140, productCode: '027'},
        {beginningBalance: 20, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140, productCode: '029'},
        {beginningBalance: 21, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140, productCode: '110'},
        {beginningBalance: 22, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140, productCode: '111'}],
      period: {stringStartDate: "01/01/2014", stringEndDate: "31/01/2014"},
      patientQuantifications: [
        {id: 1, total: 1}
      ]
    }
  };

  var rnrItemsWithKits = {
    rnr: {
      facility: {code: "F10", name: "Health Facility 1"},
      fullSupplyLineItems: [
        {quantityReceived: 10, quantityDispensed: 3, isKit: true, productCode: '26A01'},
        {quantityReceived: 5, quantityDispensed: 2, isKit: true, productCode: '26A02'},
        {quantityReceived: 20, quantityDispensed: 3, isKit: false, productCode: 'P1'}
      ],
      period: {stringStartDate: "01/01/2014", stringEndDate: "31/01/2014"},
      patientQuantifications: [
        {id: 1, total: 1}
      ]
    }
  }

  beforeEach(module('openlmis'));

  beforeEach(inject(function ($httpBackend, $rootScope, $controller, $filter, $location, _requisitionService_, _downloadPdfService_, _downloadSimamService_) {
    httpBackend = $httpBackend;
    scope = $rootScope.$new();
    location = $location;
    requisition = {lineItems: [], nonFullSupplyLineItems: [], regimenLineItems: [], equipmentLineItems :[], period: {numberOfMonths: 3}};
    route = {current: {params:{'programId': 2, 'rnr': 1, 'supplyType': 'fullSupply'}}};
    requisitionService =  _requisitionService_;
    downloadPdfService = _downloadPdfService_;
    downloadSimamService = _downloadSimamService_;
    spyOn(downloadPdfService, "init").andReturn(function(a,b){});
    spyOn(downloadSimamService, "init").andReturn(function(a,b){});
    $controller(ViewRnrViaDetailController, {$scope: scope, $route: route, $location:$location});
  }));


  it('should get rnr items size is same as pageSize',function(){
    var rnrItems = {
      rnr: {
        facility: {code: "F10", name: "Health Facility 1"},
        fullSupplyLineItems: [{beginningBalance: 98, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140}],
        period: {stringStartDate: "01/01/2014", stringEndDate: "31/01/2014"},
        patientQuantifications: [
          {id: 1, total: 1}
        ]
      }
    };

    initMockRequisition(rnrItems);
    expect(scope.regularRnrItems.length).toEqual(20);
  });

  it('should get numberPages is 2 ',function(){

    initMockRequisition(rnrItemsForPagination);

    expect(scope.numPages).toEqual(2);
  });

  it('should sort by fnm ',function(){

    initMockRequisition(rnrItemsForPagination);

    expect(scope.regularRnrItems[0].productCode).toEqual('001');
    expect(scope.regularRnrItems[21].productCode).toEqual('122');
  });

  it('should get the 2 page of visible rnr items ',function(){
    scope.currentPage = 2;
    var expectRnrList = [
      {beginningBalance: 22, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140, productCode: '111'},
      {beginningBalance: 11, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140, productCode: '122'},
      {},
      {},
      {},
      {},
      {},
      {},
      {},
      {},
      {},
      {},
      {},
      {},
      {},
      {},
      {},
      {},
      {},
      {}];
    initMockRequisition(rnrItemsForPagination);

    expect(scope.rnrItemsVisible).toEqual(expectRnrList);
  });

  it('should get visible rnr items excluding kits and populate rnr kit items', function() {
    scope.currentPage = 1;
    scope.rnrItemsVisible = [];
    var expectedUrl = "/requisitions/1/skipped.json";
    httpBackend.expect('GET', expectedUrl).respond(200, rnrItemsWithKits);
    scope.initDownloadPdfButton = function(){};
    scope.loadRequisitionDetail();
    httpBackend.flush();

    expect(scope.rnrItemsVisible.length).toEqual(20);
    expect(scope.rnrItemsVisible[0].productCode).toEqual('P1');
    expect(scope.usKitItem.quantityDispensed).toEqual(3);
    expect(scope.usKitItem.productCode).toEqual('26A01');
    expect(scope.usKitItem.quantityReceived).toEqual(10);
    expect(scope.apeKitItem.quantityDispensed).toEqual(2);
    expect(scope.apeKitItem.quantityReceived).toEqual(5);
    expect(scope.apeKitItem.productCode).toEqual('26A02');

  });

  it('should get the correct submitter and approver on via view',function(){
    var submitterText = "submitterText";
    var approverText = "approverText";

    var rnrItems = {
      rnr: {
        facility: {code: "F10", name: "Health Facility 1"},
        fullSupplyLineItems: [{beginningBalance: 98, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140}],
        period: {stringStartDate: "01/01/2014", stringEndDate: "31/01/2014"},
        rnrSignatures:[{type: "SUBMITTER",text: submitterText},{type: "APPROVER",text: approverText}],
        patientQuantifications: [
          {id: 1, total: 1}
        ]
      }
    };

    initMockRequisition(rnrItems);

    expect(scope.submitterSignature).toEqual(submitterText);
    expect(scope.approverSignature).toEqual(approverText);
  });

  it('should set actualPeriod as the period when there is actualPeriod in rnr',function(){

    var startDate = new Date("2015/02/20");
    var endDate = new Date("2015/03/20");
    var actualStartDate = new Date("2015/02/18");
    var actualEndDate = new Date("2015/03/19");
    var rnrItems = {
      rnr: {
        facility: {code: "F10", name: "Health Facility 1"},
        fullSupplyLineItems: [{beginningBalance: 98, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140}],
        period: {stringStartDate: "02/20/2014", stringEndDate: "20/03/2014", startDate: startDate, endDate: endDate},
        actualPeriodStartDate: actualStartDate,
        actualPeriodEndDate: actualEndDate,
        patientQuantifications: [
          {id: 1, total: 1}
        ]
      }
    };

    initMockRequisition(rnrItems);

    expect(scope.displayStartDate).toEqual('18/02/2015');
    expect(scope.displayEndDate).toEqual('19/03/2015');
  });

  it('should set emergency cell as \\ when there rnr is emergency',function(){

    var startDate = new Date("2015/02/20");
    var endDate = new Date("2015/03/20");
    var actualStartDate = new Date("2015/02/18");
    var actualEndDate = new Date("2015/03/19");
    var rnrItems = {
      rnr: {
        facility: {code: "F10", name: "Health Facility 1"},
        fullSupplyLineItems: [{beginningBalance: 98, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140}],
        period: {stringStartDate: "02/20/2014", stringEndDate: "20/03/2014", startDate: startDate, endDate: endDate},
        actualPeriodStartDate: actualStartDate,
        actualPeriodEndDate: actualEndDate,
        emergency:true,
        patientQuantifications: [
          {id: 1, total: 1}
        ]
      }
    };

    initMockRequisition(rnrItems);

    expect(scope.displayStartDate).toEqual('\\');
    expect(scope.displayEndDate).toEqual('\\');

    expect(scope.consultationNumber).toEqual('\\');

    expect(scope.apeKitReceived).toEqual('\\');
    expect(scope.apeKitDispensed).toEqual('\\');
    expect(scope.usKitReceived).toEqual('\\');
    expect(scope.usKitDispensed).toEqual('\\');
  });

  it('should set schedulePeriod as the period when there is no actualPeriod in rnr',function(){

    var startDate = new Date("2015/02/20");
    var endDate = new Date("2015/03/20");
    var rnrItems = {
      rnr: {
        facility: {code: "F10", name: "Health Facility 1"},
        fullSupplyLineItems: [{beginningBalance: 98, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140}],
        period: {stringStartDate: "02/20/2014", stringEndDate: "20/03/2014", startDate: startDate, endDate: endDate},
        patientQuantifications: [
          {id: 1, total: 1}
        ]
      }
    };
    initMockRequisition(rnrItems);

    expect(scope.displayStartDate).toEqual("20/02/2015");
    expect(scope.displayEndDate).toEqual("20/03/2015");
  });

  it('should set kit view empty when normal requisition has no kit product',function(){

    var startDate = new Date("2015/02/20");
    var endDate = new Date("2015/03/20");
    var actualStartDate = new Date("2015/02/18");
    var actualEndDate = new Date("2015/03/19");
    var rnrItems = {
      rnr: {
        facility: {code: "F10", name: "Health Facility 1"},
        fullSupplyLineItems: [{beginningBalance: 98, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140}],
        period: {stringStartDate: "02/20/2014", stringEndDate: "20/03/2014", startDate: startDate, endDate: endDate},
        actualPeriodStartDate: actualStartDate,
        actualPeriodEndDate: actualEndDate,
        patientQuantifications: [
          {id: 1, total: 1}
        ]
      }
    };

    initMockRequisition(rnrItems);

    expect(scope.apeKitReceived).toEqual("");
    expect(scope.usKitReceived).toEqual("");
    expect(scope.apeKitDispensed).toEqual("");
    expect(scope.usKitDispensed).toEqual("");
  });

  function initMockRequisition(rnrItems) {
    var expectedUrl = "/requisitions/1/skipped.json";
    httpBackend.expect('GET', expectedUrl).respond(200, rnrItems);
    scope.initDownloadPdfButton = function(){};
    scope.loadRequisitionDetail();
    httpBackend.flush();
  }
});
